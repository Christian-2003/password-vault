package de.christian2003.data.files.application.usecases

import de.christian2003.data.files.domain.entities.InternalFile
import de.christian2003.data.files.domain.entities.SharedFileMetadata
import de.christian2003.data.files.domain.repositories.SharedFilesRepository
import javax.inject.Inject


class IsFileSharedUseCase @Inject internal constructor(
    private val sharedFilesRepository: SharedFilesRepository
) {

    fun isShared(file: InternalFile): Boolean {
        val sharedFiles: List<SharedFileMetadata> = sharedFilesRepository.getSharedFiles()
        val matchingFile: SharedFileMetadata? = sharedFiles.firstOrNull { sf -> sf.internalFileName == file.internalName }
        return matchingFile != null
    }

}
