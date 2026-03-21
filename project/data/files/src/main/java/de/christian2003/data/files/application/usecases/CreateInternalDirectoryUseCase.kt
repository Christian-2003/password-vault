package de.christian2003.data.files.application.usecases

import de.christian2003.data.files.application.services.InternalDirectoryNameValidatorService
import de.christian2003.data.files.domain.entities.InternalDirectory
import de.christian2003.data.files.domain.repositories.InternalFilesystemRepository
import javax.inject.Inject


class CreateInternalDirectoryUseCase @Inject internal constructor(
    private val repository: InternalFilesystemRepository,
    private val directoryNameValidatorService: InternalDirectoryNameValidatorService
) {

    fun create(name: String, parentDirectory: InternalDirectory) {
        if (!directoryNameValidatorService.isValid(name)) {
            throw IllegalArgumentException("Directory name '$name' is invalid")
        }

        val directory = InternalDirectory(
            internalPath = parentDirectory.internalPath + "/" + name
        )
        repository.addDirectory(directory)
    }

}
