package de.christian2003.feature.export.infrastructure.backup.v3

import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.core.security.domain.services.CipherService
import de.christian2003.core.security.domain.services.KdfService
import de.christian2003.data.accounts.application.usecases.GetAccountsByIdsUseCase
import de.christian2003.data.accounts.application.usecases.GetAllTagsUseCase
import de.christian2003.data.accounts.domain.entities.Account
import de.christian2003.data.accounts.domain.entities.Tag
import de.christian2003.data.files.application.usecases.FindInternalDirectoryForInternalFileUseCase
import de.christian2003.data.files.application.usecases.GetInternalFileByFileNameUseCase
import de.christian2003.data.files.application.usecases.GetStreamForInternalFileUseCase
import de.christian2003.data.files.domain.entities.InternalDirectory
import de.christian2003.data.files.domain.entities.InternalFile
import de.christian2003.feature.export.domain.entities.ExportConfig
import de.christian2003.feature.export.domain.exceptions.IllegalExportConfigException
import de.christian2003.feature.export.domain.entities.ExportDescriptor
import de.christian2003.feature.export.domain.services.ExportService
import de.christian2003.feature.export.infrastructure.backup.v3.dto.V3BackupAccountDto
import de.christian2003.feature.export.infrastructure.backup.v3.dto.V3BackupAccountsRootDto
import de.christian2003.feature.export.infrastructure.backup.v3.dto.V3BackupMetadataDto
import de.christian2003.feature.export.infrastructure.backup.v3.dto.V3BackupTagDto
import de.christian2003.feature.export.infrastructure.backup.v3.mapper.V3BackupAccountMapper
import de.christian2003.feature.export.infrastructure.backup.v3.mapper.V3BackupTagMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.security.SecureRandom
import java.time.LocalDateTime
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import kotlin.uuid.Uuid
import de.christian2003.feature.export.R


/**
 * Backup service for V3 backups of Password Vault data.
 *
 * @param accountMapper                                 Mapper for accounts.
 * @param tagMapper                                     Mapper for tags.
 * @param getAccountsByIdsUseCase                       Use case to get accounts by their IDs.
 * @param getAllTagsUseCase                             Use case to get all tags.
 * @param getInternalFileByFileNameUseCase              Use case to get an internal file by it's
 *                                                      internal name.
 * @param findInternalDirectoryForInternalFileUseCase   Use case to find the internal directory in
 *                                                      which an internal file is stored.
 * @param getStreamForInternalFileUseCase               Use case to get streams to an internal file.
 * @param kdfService                                    Service used to generate a cryptographic
 *                                                      key from a seed.
 * @param cipherService                                 Service used for cryptographic operations.
 */
internal class V3BackupService @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val accountMapper: V3BackupAccountMapper,
    private val tagMapper: V3BackupTagMapper,
    private val getAccountsByIdsUseCase: GetAccountsByIdsUseCase,
    private val getAllTagsUseCase: GetAllTagsUseCase,
    private val getInternalFileByFileNameUseCase: GetInternalFileByFileNameUseCase,
    private val findInternalDirectoryForInternalFileUseCase: FindInternalDirectoryForInternalFileUseCase,
    private val getStreamForInternalFileUseCase: GetStreamForInternalFileUseCase,
    private val kdfService: KdfService,
    private val cipherService: CipherService
): ExportService {

    /**
     * Json serializer.
     */
    private val json = Json { }

    /**
     * Descriptor for the export service.
     */
    override val exportDescriptor: ExportDescriptor = ExportDescriptor(
        id = "V3Backup",
        exportFileExtension = "pvx",
        canExportAccounts = true,
        canExportFiles = true,
        isExportEncrypted = true,
        titleId = R.string.export_v3_title,
        subtitleId = R.string.export_v3_subtitle,
        helpTextId = R.string.export_v3_helpText
    )


    /**
     * Creates an export according to the provided configuration. The method starts a background
     * task and provides progress updates using the returned flow, which reports the progress as
     * a percentage between 0 % to 100 % (i.e. 0.0 to 1.0).
     *
     * @param config                        Configuration for the export.
     * @return                              Flow which reports the progress.
     * @throws IllegalExportConfigException The export config does not contain an encryption key
     *                                      seed.
     */
    override fun createExport(config: ExportConfig): Flow<Float> = flow {
        if (config.encryptionKeySeed == null || config.encryptionKeySeed.isEmpty()) {
            throw IllegalExportConfigException("Export config must contain encryption key seed")
        }
        val saltBytes: ByteArray = generateSalt()
        val keyBytes: ByteArray = kdfService.derive(config.encryptionKeySeed, saltBytes)

        val progressionStepCount: Int = config.files.size + 2 //2 = Export accounts + export metadata
        var progressionCurrentStep = 0

        var exportedAccountsCount: Int
        var exportedFilesCount = 0

        context.contentResolver.openFileDescriptor(config.exportDestination, "w")?.use { parcelFileDescriptor ->
            FileOutputStream(parcelFileDescriptor.fileDescriptor).use { fileOutputStream ->
                fileOutputStream.write(saltBytes)

                val encryptedStream: OutputStream = cipherService.encryptStream(fileOutputStream, keyBytes)

                ZipOutputStream(encryptedStream).use { zipOutputStream ->

                    //Export accounts:
                    exportedAccountsCount = exportAccounts(config.accounts, zipOutputStream)
                    emit(progressionCurrentStep++ / progressionStepCount.toFloat())

                    //Export files:
                    config.files.forEach { fileName ->
                        val result: Boolean = exportFile(fileName, zipOutputStream)
                        if (result) {
                            exportedFilesCount++
                        }

                        emit(progressionCurrentStep++ / progressionStepCount.toFloat())
                    }

                    //Export metadata:
                    exportMetadata(exportedAccountsCount, exportedFilesCount, zipOutputStream)
                    //No emit needed after last step, because 'emit(1.0f)' is called at the end

                }
            }
        }

        saltBytes.fill(0)
        keyBytes.fill(0)

        //Once finishing, make sure that progress reports 100 %:
        emit(1.0f)
    }


    /**
     * Exports the specified accounts to the provided ZIP output stream.
     *
     * @param accountIds        IDs of the accounts to export.
     * @param zipOutputStream   ZIP output stream into which to write the accounts.
     * @return                  Number of accounts that were written into the ZIP OS.
     */
    private suspend fun exportAccounts(accountIds: Set<Uuid>, zipOutputStream: ZipOutputStream): Int {
        var exportedAccountsCount = 0

        try {
            val accounts: List<Account> = getAccountsByIdsUseCase.getAccountsByIds(accountIds.toList())
            val tags: List<Tag> = getAllTagsUseCase.getAllTags().first()

            val accountDtos: List<V3BackupAccountDto> = accounts.mapNotNull { account ->
                try {
                    accountMapper.toDto(account)
                } catch (_: Exception) {
                    null
                }
            }

            val tagDtos: List<V3BackupTagDto> = tags.mapNotNull { tag ->
                try {
                    tagMapper.toDto(tag)
                }
                catch (_: Exception) {
                    null
                }
            }

            val rootDto = V3BackupAccountsRootDto(
                accounts = accountDtos,
                tags = tagDtos
            )

            val serializedJson: String = json.encodeToString<V3BackupAccountsRootDto>(rootDto)

            val accountsDir = File("accounts")
            val accountsFile = File(accountsDir, "accounts.json")

            val zipEntry = ZipEntry(sanitizeZipEntryName(accountsFile.path))
            zipOutputStream.putNextEntry(zipEntry)
            zipOutputStream.write(serializedJson.toByteArray())
            zipOutputStream.closeEntry()

            exportedAccountsCount = accountDtos.size
        }
        catch (_: Exception) {
            //Do not log here because of sensitive data
        }

        return exportedAccountsCount
    }


    /**
     * Exports the specified file to the provided ZIP output stream.
     *
     * @param fileName          Internal file name of the file to export.
     * @param zipOutputStream   ZIP output stream into which to write the file.
     * @return                  Whether the file was written successfully into the ZIP OS.
     */
    private suspend fun exportFile(fileName: String, zipOutputStream: ZipOutputStream): Boolean {
        val internalFile: InternalFile? = getInternalFileByFileNameUseCase.getInternalFile(fileName)

        var success = false

        if (internalFile != null) {
            val internalDir: InternalDirectory? = findInternalDirectoryForInternalFileUseCase.findInternalDirectoryFor(internalFile)

            if (internalDir != null) {
                val fileInputStream: InputStream? = getStreamForInternalFileUseCase.getInputStreamFor(internalFile, internalDir)

                fileInputStream?.use { fileInputStream ->
                    val userfilesDir = File("userfiles")
                    val fileDir = File(userfilesDir, internalDir.internalPath)
                    val file = File(fileDir, internalFile.actualFileName)

                    val zipEntry = ZipEntry(sanitizeZipEntryName(file.path))
                    zipOutputStream.putNextEntry(zipEntry)
                    fileInputStream.copyTo(zipOutputStream)
                    zipOutputStream.closeEntry()

                    success = true
                }
            }
        }

        return success
    }


    /**
     * Exports the metadata for the backup to the provided ZIP output stream.
     *
     * @param exportedAccountsCount Number of accounts that are exported in the backup.
     * @param exportedFilesCount    Number of files that are exported in the backup.
     * @param zipOutputStream       ZIP output stream into which to write the metadata.
     */
    private fun exportMetadata(exportedAccountsCount: Int, exportedFilesCount: Int, zipOutputStream: ZipOutputStream) {
        try {
            val deviceName: String = getDeviceName()
            val packageInfo: PackageInfo = getPasswordVaultPackageInfo()

            val metadataDto = V3BackupMetadataDto(
                deviceName = deviceName,
                appVersionName = packageInfo.versionName ?: "N/A",
                appVersionCode = packageInfo.longVersionCode.toInt(),
                createdAt = LocalDateTime.now(),
                includedAccountsCount = exportedAccountsCount,
                includedFilesCount = exportedFilesCount
            )

            val serializedJson: String = json.encodeToString<V3BackupMetadataDto>(metadataDto)

            val zipEntry = ZipEntry(sanitizeZipEntryName("metadata.json"))
            zipOutputStream.putNextEntry(zipEntry)
            zipOutputStream.write(serializedJson.toByteArray())
            zipOutputStream.closeEntry()
        }
        catch (e: Exception) {
            Log.e("Export", "Cannot export backup metadata: ${e.message ?: "Unknown error"}")
        }
    }


    /**
     * Sanitizes names for ZIP entries. The following changes are made:
     *  * All directory separators are turned to '/'
     *  * Problematic chars (<>:"|?*) are replaced with '_'
     *  * Relative paths (..) are replaced with '_'
     *
     * @param name  ZIP entry name to sanitize.
     * @return      Sanitized ZIP entry name.
     */
    private fun sanitizeZipEntryName(name: String): String {
        return name
            .replace("\\", "/")
            .replace(Regex("""[<>:"|?*]"""), "_")
            .replace("..", "/")
            .trim()
    }


    /**
     * Returns the Android device name (e.g. "John's Samsung Galaxy S22").
     *
     * @return  Android device name.
     */
    private fun getDeviceName(): String {
        val contentResolver: ContentResolver = context.contentResolver

        return Settings.Global.getString(contentResolver, Settings.Global.DEVICE_NAME)
            ?: Settings.Secure.getString(contentResolver, "bluetooth_name")
            ?: "${Build.MANUFACTURER} ${Build.MODEL}"
    }


    /**
     * Returns the package info for Password Vault.
     *
     * @return  Package info for Password Vault.
     */
    private fun getPasswordVaultPackageInfo(): PackageInfo {
        val packageName: String = context.packageName
        val packageManager: PackageManager = context.packageManager
        val flags: PackageManager.PackageInfoFlags = PackageManager.PackageInfoFlags.of(0)

        return packageManager.getPackageInfo(packageName, flags)
    }


    /**
     * Generates a random salt as bytes.
     *
     * @return  Random salt.
     */
    private fun generateSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(32)
        random.nextBytes(salt)
        return salt
    }

}
