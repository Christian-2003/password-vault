package de.christian2003.data.files.application.usecases

import de.christian2003.data.files.domain.entities.InternalDirectory
import de.christian2003.data.files.domain.repositories.InternalFilesystemRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetInternalFilesInDirectoryUseCase @Inject internal constructor(
    private val repository: InternalFilesystemRepository
) {

    fun getInternalFiles(directory: InternalDirectory): Flow<List<String>> {
        return repository.getAllFileNamesInDirectory(directory)
    }

}
