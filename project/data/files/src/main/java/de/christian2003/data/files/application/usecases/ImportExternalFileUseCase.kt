package de.christian2003.data.files.application.usecases

import android.net.Uri
import de.christian2003.core.common.application.services.FileNameValidatorService
import de.christian2003.data.files.application.services.MimeTypeMapperService
import de.christian2003.data.files.domain.entities.InternalDirectory
import de.christian2003.data.files.domain.entities.InternalFile
import de.christian2003.data.files.domain.entities.InternalFileMetadata
import de.christian2003.data.files.domain.repositories.FileLookupRepository
import de.christian2003.data.files.domain.repositories.InternalFilesystemRepository
import de.christian2003.data.files.domain.services.InternalFileUtilsService
import javax.inject.Inject
import kotlin.uuid.Uuid


class ImportExternalFileUseCase @Inject internal constructor(
    private val internalFilesystemRepository: InternalFilesystemRepository,
    private val fileLookupRepository: FileLookupRepository,
    private val fileUtilsService: InternalFileUtilsService,
    private val mimeTypeMapperService: MimeTypeMapperService,
    private val fileNameValidatorService: FileNameValidatorService
) {

    suspend fun importExternalFile(externalFileUri: Uri, internalDirectory: InternalDirectory) {
        val internalFileName = "${Uuid.random()}.enc"

        val originalFileName: String = internalFilesystemRepository.copyFileToDirectory(
            sourceFileUri = externalFileUri,
            destinationFileName = internalFileName,
            directory = internalDirectory
        )

        val validatedFileName: String = if (!fileNameValidatorService.isValid(originalFileName)) {
            fileNameValidatorService.replaceIllegalChars(originalFileName)
        } else {
            originalFileName
        }

        val internalFilePath = "${internalDirectory.internalPath}/$internalFileName"
        val internalFileSize: Long = fileUtilsService.getSizeOfInternalFile(internalFilePath)

        val internalFile = InternalFile(
            internalName = internalFileName,
            actualFileName = validatedFileName,
            metadata = InternalFileMetadata(
                mimeType = mimeTypeMapperService.mapFilenameToMimeType(validatedFileName),
                size = internalFileSize
            )
        )
        fileLookupRepository.createFile(internalFile)
    }

}
