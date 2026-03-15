package de.christian2003.data.files.application.usecases

import de.christian2003.data.files.domain.entities.InternalDirectory
import de.christian2003.data.files.domain.repositories.InternalFilesystemRepository
import javax.inject.Inject


class RenameInternalDirectoryUseCase @Inject internal constructor(
    private val repository: InternalFilesystemRepository
) {

    fun rename(currentDirectory: InternalDirectory, newName: String) {
        val currentInternalPath: String = currentDirectory.internalPath
        val updatedDirectory: InternalDirectory = currentDirectory.copy(
            internalPath = currentDirectory.parentInternalPath + "/" + newName
        )

        repository.updateDirectory(currentInternalPath, updatedDirectory)
    }

}
