package de.christian2003.data.files.application.usecases

import de.christian2003.data.files.domain.entities.InternalDirectory
import de.christian2003.data.files.domain.entities.InternalFile
import de.christian2003.data.files.domain.repositories.FileLookupRepository
import de.christian2003.data.files.domain.repositories.InternalFilesystemRepository
import javax.inject.Inject

class DeleteInternalFileUseCase @Inject internal constructor(
    private val internalFilesystemRepository: InternalFilesystemRepository,
    private val fileLookupRepository: FileLookupRepository
) {

    suspend fun delete(internalFile: InternalFile, directory: InternalDirectory) {
        internalFilesystemRepository.deleteFileFromDirectory(internalFile.internalName, directory)
        fileLookupRepository.deleteFile(internalFile)
    }

}
