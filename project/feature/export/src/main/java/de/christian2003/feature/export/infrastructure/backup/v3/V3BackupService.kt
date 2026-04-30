package de.christian2003.feature.export.infrastructure.backup.v3

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
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
import de.christian2003.feature.export.domain.services.ExportService
import de.christian2003.feature.export.infrastructure.backup.v3.dto.V3BackupAccountDto
import de.christian2003.feature.export.infrastructure.backup.v3.dto.V3BackupAccountsRootDto
import de.christian2003.feature.export.infrastructure.backup.v3.dto.V3BackupTagDto
import de.christian2003.feature.export.infrastructure.backup.v3.mapper.V3BackupAccountMapper
import de.christian2003.feature.export.infrastructure.backup.v3.mapper.V3BackupTagMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import kotlin.uuid.Uuid


internal class V3BackupService @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val accountMapper: V3BackupAccountMapper,
    private val tagMapper: V3BackupTagMapper,
    private val getAccountsByIdsUseCase: GetAccountsByIdsUseCase,
    private val getAllTagsUseCase: GetAllTagsUseCase,
    private val getInternalFileByFileNameUseCase: GetInternalFileByFileNameUseCase,
    private val findInternalDirectoryForInternalFileUseCase: FindInternalDirectoryForInternalFileUseCase,
    private val getStreamForInternalFileUseCase: GetStreamForInternalFileUseCase
): ExportService {

    /**
     * Creates an export according to the provided configuration. The method starts a background
     * task and provides progress updates using the returned flow, which reports the progress as
     * a percentage between 0 % to 100 % (i.e. 0.0 to 1.0).
     *
     * @param config    Configuration for the export.
     * @return          Flow which reports the progress.
     */
    override fun createExport(config: ExportConfig): Flow<Float> = flow {
        val progressionStepCount: Int = config.files.size + 1
        var progressionCurrentStep = 0

        context.contentResolver.openFileDescriptor(config.exportDestination, "w")?.use { parcelFileDescriptor ->
            FileOutputStream(parcelFileDescriptor.fileDescriptor).use { fileOutputStream ->
                ZipOutputStream(fileOutputStream).use { zipOutputStream ->

                    //Export accounts:
                    exportAccounts(config.accounts, zipOutputStream)
                    emit(progressionCurrentStep++ / progressionStepCount.toFloat())

                    //Export files:
                    config.files.forEach { fileName ->
                        exportFile(fileName, zipOutputStream)
                        emit(progressionCurrentStep++ / progressionStepCount.toFloat())
                    }

                }
            }
        }

        //Once finishing, make sure that progress reports 100 %:
        emit(1.0f)
    }


    private suspend fun exportAccounts(accountIds: Set<Uuid>, zipOutputStream: ZipOutputStream) {
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

            val json = Json { }
            val serializedJson: String = json.encodeToString<V3BackupAccountsRootDto>(rootDto)

            val zipEntry = ZipEntry("accounts.json")
            zipOutputStream.putNextEntry(zipEntry)
            zipOutputStream.write(serializedJson.toByteArray())
            zipOutputStream.closeEntry()
        }
        catch (_: Exception) {
            //Do not log here because of sensitive data
        }
    }


    private suspend fun exportFile(fileName: String, zipOutputStream: ZipOutputStream) {
        val internalFile: InternalFile? = getInternalFileByFileNameUseCase.getInternalFile(fileName)

        if (internalFile != null) {
            val internalDir: InternalDirectory? = findInternalDirectoryForInternalFileUseCase.findInternalDirectoryFor(internalFile)

            if (internalDir != null) {
                val fileInputStream: InputStream? = getStreamForInternalFileUseCase.getInputStreamFor(internalFile, internalDir)

                fileInputStream?.use { fileInputStream ->
                    val zipEntry = ZipEntry(internalFile.actualFileName)
                    zipOutputStream.putNextEntry(zipEntry)
                    fileInputStream.copyTo(zipOutputStream)
                    zipOutputStream.closeEntry()
                }
            }
        }
    }

}
