package de.christian2003.feature.export.infrastructure.backup.v3

import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.core.security.domain.services.CipherService
import de.christian2003.core.security.domain.services.KdfService
import de.christian2003.data.accounts.application.usecases.CreateAccountsBatchUseCase
import de.christian2003.data.accounts.application.usecases.GetAccountsByIdsUseCase
import de.christian2003.data.accounts.application.usecases.GetAllTagsUseCase
import de.christian2003.data.accounts.domain.entities.Account
import de.christian2003.data.accounts.domain.entities.BatchOption
import de.christian2003.data.accounts.domain.entities.Tag
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
import java.io.OutputStream
import java.security.SecureRandom
import java.time.LocalDateTime
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import kotlin.uuid.Uuid
import de.christian2003.feature.export.R
import de.christian2003.feature.export.domain.entities.ImportConfig
import de.christian2003.feature.export.domain.entities.ImportDescriptor
import de.christian2003.feature.export.domain.entities.ImportOptions
import de.christian2003.feature.export.domain.entities.ImportProperties
import de.christian2003.feature.export.domain.exceptions.CorruptImportFileException
import de.christian2003.feature.export.domain.exceptions.IllegalImportConfigException
import de.christian2003.feature.export.domain.exceptions.UnsupportedFileFormatException
import de.christian2003.feature.export.domain.services.ImportService
import java.io.FileInputStream
import java.io.InputStream
import java.util.zip.ZipInputStream


/**
 * Backup service for V3 backups of Password Vault data.
 *
 * @param accountMapper                                 Mapper for accounts.
 * @param tagMapper                                     Mapper for tags.
 * @param getAccountsByIdsUseCase                       Use case to get accounts by their IDs.
 * @param createAccountsBatchUseCase                    Use case to create a batch of accounts.
 * @param getAllTagsUseCase                             Use case to get all tags.
 * @param kdfService                                    Service used to generate a cryptographic
 *                                                      key from a seed.
 * @param cipherService                                 Service used for cryptographic operations.
 */
internal class V3BackupService @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val accountMapper: V3BackupAccountMapper,
    private val tagMapper: V3BackupTagMapper,
    private val getAccountsByIdsUseCase: GetAccountsByIdsUseCase,
    private val createAccountsBatchUseCase: CreateAccountsBatchUseCase,
    private val getAllTagsUseCase: GetAllTagsUseCase,
    private val kdfService: KdfService,
    private val cipherService: CipherService
): ExportService, ImportService {

    companion object {

        private const val SALT_LENGTH: Int = 32

        private const val ZIP_DIR_ACCOUNTS = "accounts"

        private const val ZIP_FILE_ACCOUNTS = "accounts.json"

    }


    /**
     * Json (de)serializer.
     */
    private val json = Json


    /**
     * Descriptor for the export service.
     */
    override val exportDescriptor: ExportDescriptor = ExportDescriptor(
        id = "V3Backup",
        exportFileExtension = "pvx",
        exportFileMimeType = "application/vnd.passwordvault.pvx",
        canExportAccounts = true,
        canExportFiles = false,
        isExportEncrypted = true,
        titleId = R.string.export_v3_title,
        subtitleId = R.string.export_v3_subtitle,
        helpTextId = R.string.export_v3_helpText
    )

    /**
     * Descriptor for the import service.
     */
    override val importDescriptor: ImportDescriptor = ImportDescriptor(
        id = "V3Backup",
        importFileExtensions = setOf("pvx"),
        importFileMimeTypes = setOf("application/vnd.passwordvault.pvx"),
        titleId = R.string.import_v3_title,
        subtitleId = R.string.import_v3_subtitle
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
        emit(0.0f) //Signal starting progress

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
     * Performs a preflight check of the provided import file to get additional data about the
     * file to import.
     *
     * @param importFile                        URI of the file to import for which to perform the
     *                                          preflight request.
     * @return                                  Response containing properties of the file to import.
     * @throws CorruptImportFileException       The file to import is corrupt and cannot be imported.
     * @throws UnsupportedFileFormatException   The selected file is unsupported and cannot be imported.
     */
    override fun getProperties(importFile: Uri): Flow<ImportProperties?> = flow {
        val properties = ImportProperties(
            isEncrypted = true //V3 backups are always encrypted
        )

        emit(properties)
    }


    /**
     * Begins an import according to the provided configuration. The method starts a background
     * task and provides progress updates using the returned flow, which reports the progress as
     * a percentage between 0 % to 100 % (i.e. 0.0 to 1.0).
     *
     * @param config                            Configuration for the import.
     * @return                                  Flow which reports the progress.
     * @throws CorruptImportFileException       The file to import is corrupt and cannot be imported.
     * @throws IllegalImportConfigException     The import config is illegal.
     * @throws UnsupportedFileFormatException   The selected file is unsupported and cannot be imported.
     */
    override fun beginImport(config: ImportConfig): Flow<Float> = flow {
        emit(0.0f)

        if (config.decryptionKeySeed == null || config.decryptionKeySeed.isEmpty()) {
            throw IllegalImportConfigException("Import config must contain decryption key seed")
        }

        var saltBytes: ByteArray? = null
        var keyBytes: ByteArray? = null

        context.contentResolver.openFileDescriptor(config.importFile, "r")?.use { parcelFileDescriptor ->
            FileInputStream(parcelFileDescriptor.fileDescriptor).use { fileInputStream ->
                //Decryption:
                saltBytes = fileInputStream.readNBytes(SALT_LENGTH)
                keyBytes = kdfService.derive(config.decryptionKeySeed, saltBytes)
                val decryptedStream: InputStream = cipherService.decryptStream(fileInputStream, keyBytes)

                //Name of the entry in which accounts are stored:
                val accountsDir = File(ZIP_DIR_ACCOUNTS)
                val accountsFile = File(accountsDir, ZIP_FILE_ACCOUNTS)
                val accountsEntryName: String = sanitizeZipEntryName(accountsFile.path)

                //Read the ZIP:
                ZipInputStream(decryptedStream).use { zipInputStream ->
                    while (zipInputStream.available() != 0) {
                        val zipEntry: ZipEntry = zipInputStream.nextEntry

                        if (zipEntry.name == accountsEntryName) {
                            //Accounts:
                            val accountBytes: ByteArray = zipInputStream.readBytes()
                            importAccounts(accountBytes, config.accountsImportOptions)
                        }

                        zipInputStream.closeEntry() //Continue to next entry
                    }
                }
            }
        }

        saltBytes?.fill(0)
        keyBytes?.fill(0)

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

            val accountsDir = File(ZIP_DIR_ACCOUNTS)
            val accountsFile = File(accountsDir, ZIP_FILE_ACCOUNTS)

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


    private suspend fun importAccounts(bytes: ByteArray, importOptions: ImportOptions) {
        try {
            //Deserialize JSON:
            val serializedJson: String = bytes.toString()
            val rootDto: V3BackupAccountsRootDto = json.decodeFromString<V3BackupAccountsRootDto>(serializedJson)

            //Map tags to domain:
            val tags: List<Tag> = rootDto.tags.mapNotNull { tagDto ->
                try {
                    tagMapper.toDomain(tagDto)
                }
                catch (_: Exception) {
                    null
                }
            }

            //Map accounts to domain:
            val accounts: List<Account> = rootDto.accounts.mapNotNull { accountDto ->
                try {
                    accountMapper.toDomain(accountDto, tags)
                }
                catch (_: Exception) {
                    null
                }
            }

            //Restore
            val batchOption: BatchOption = mapImportOptionToBatchOption(importOptions)
            createAccountsBatchUseCase.createAccountsBatch(accounts, tags, batchOption)
        }
        catch(_: Exception) {
            //Do not log here because of sensitive data
        }
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
        val salt = ByteArray(SALT_LENGTH)
        random.nextBytes(salt)
        return salt
    }


    /**
     * Maps the import option selected by the user to the corresponding batch option required
     * to create the batch of accounts for the import.
     *
     * @param importOption  Import option to map.
     * @return              Corresponding batch option.
     */
    private fun mapImportOptionToBatchOption(importOption: ImportOptions): BatchOption {
        return when (importOption) {
            ImportOptions.SkipDuplicates -> BatchOption.SkipDuplicates
            ImportOptions.OverrideDuplicates -> BatchOption.ReplaceDuplicates
            ImportOptions.DeleteAllExistingData -> BatchOption.DeleteAllExisting
        }
    }

}
