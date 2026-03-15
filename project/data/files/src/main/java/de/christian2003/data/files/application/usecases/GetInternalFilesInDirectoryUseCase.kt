package de.christian2003.data.files.application.usecases

import de.christian2003.data.files.domain.entities.InternalDirectory
import de.christian2003.data.files.domain.entities.InternalFile
import de.christian2003.data.files.domain.repositories.FileLookupRepository
import de.christian2003.data.files.domain.repositories.InternalFilesystemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject


class GetInternalFilesInDirectoryUseCase @Inject internal constructor(
    private val internalFilesystemRepository: InternalFilesystemRepository,
    private val fileLookupRepository: FileLookupRepository
) {

    fun getInternalFiles(directory: InternalDirectory): Flow<List<InternalFile>> {
        val fileNames: Flow<List<String>> = internalFilesystemRepository.getAllFileNamesInDirectory(directory)

        val internalFiles: Flow<List<InternalFile>> = fileNames.flatMapLatest { fileNames ->
            fileLookupRepository.getFilesForNames(fileNames)
        }

        return internalFiles
    }

}
