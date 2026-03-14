package de.christian2003.data.files.application.usecases

import de.christian2003.data.files.domain.entities.InternalDirectory
import de.christian2003.data.files.domain.repositories.InternalFilesystemRepository
import javax.inject.Inject


class DeleteInternalDirectoryUseCase @Inject internal constructor(
    private val repository: InternalFilesystemRepository
) {

    fun delete(directory: InternalDirectory) {
        repository.deleteDirectory(directory)
    }

}
