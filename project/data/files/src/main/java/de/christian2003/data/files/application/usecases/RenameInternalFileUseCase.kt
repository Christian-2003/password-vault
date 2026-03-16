package de.christian2003.data.files.application.usecases

import de.christian2003.data.files.domain.entities.InternalFile
import de.christian2003.data.files.domain.repositories.FileLookupRepository
import javax.inject.Inject


class RenameInternalFileUseCase @Inject internal constructor(
    private val fileLookupRepository: FileLookupRepository
) {

    suspend fun renameFile(file: InternalFile, newName: String) {
        val updatedFile: InternalFile = file.copy(
            actualFileName = newName
        )
        fileLookupRepository.updateFile(updatedFile)
    }

}
