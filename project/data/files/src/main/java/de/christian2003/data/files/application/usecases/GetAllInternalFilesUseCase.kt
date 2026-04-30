package de.christian2003.data.files.application.usecases

import de.christian2003.data.files.domain.entities.InternalFile
import de.christian2003.data.files.domain.repositories.FileLookupRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetAllInternalFilesUseCase @Inject internal constructor(
    private val fileLookupRepository: FileLookupRepository
) {

    fun getAllInternalFiles(): Flow<List<InternalFile>> {
        return fileLookupRepository.getAllFiles()
    }

}
