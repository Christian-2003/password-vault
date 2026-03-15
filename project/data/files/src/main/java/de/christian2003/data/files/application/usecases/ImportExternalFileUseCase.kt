package de.christian2003.data.files.application.usecases

import android.net.Uri
import de.christian2003.data.files.domain.entities.InternalDirectory
import de.christian2003.data.files.domain.entities.InternalFile
import de.christian2003.data.files.domain.repositories.FileLookupRepository
import de.christian2003.data.files.domain.repositories.InternalFilesystemRepository
import javax.inject.Inject
import kotlin.uuid.Uuid


class ImportExternalFileUseCase @Inject internal constructor(
    private val internalFilesystemRepository: InternalFilesystemRepository,
    private val fileLookupRepository: FileLookupRepository
) {

    suspend fun importExternalFile(externalFileUri: Uri, internalDirectory: InternalDirectory) {
        val internalFileName = "${Uuid.random()}.enc"

        val originalFileName: String = internalFilesystemRepository.copyFileToDirectory(
            sourceFileUri = externalFileUri,
            destinationFileName = internalFileName,
            directory = internalDirectory
        )

        val internalFile = InternalFile(
            internalName = internalFileName,
            actualFileName = originalFileName
        )
        fileLookupRepository.createFile(internalFile)
    }

}
