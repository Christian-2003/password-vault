package de.christian2003.data.files.application.usecases

import de.christian2003.data.files.domain.entities.InternalDirectory
import de.christian2003.data.files.domain.entities.InternalFile
import de.christian2003.data.files.domain.entities.SharedFile
import de.christian2003.data.files.domain.repositories.SharedFilesRepository
import de.christian2003.data.files.domain.services.FileCopyService
import javax.inject.Inject


class PrepareFileForViewingUseCase @Inject internal constructor(
    private val sharedFilesRepository: SharedFilesRepository,
    private val fileCopyService: FileCopyService
) {

    suspend fun prepare(file: InternalFile, directory: InternalDirectory): SharedFile? {
        val sharedFile: SharedFile? = fileCopyService.copyInternalFileToShared(file, directory)
        if (sharedFile != null) {
            val fileName: String? = sharedFile.contentUri.lastPathSegment
            if (fileName != null) {
                sharedFilesRepository.addSharedFile(fileName)
            }
        }
        return sharedFile
    }

}
