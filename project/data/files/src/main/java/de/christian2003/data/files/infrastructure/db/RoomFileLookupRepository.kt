package de.christian2003.data.files.infrastructure.db

import de.christian2003.data.files.domain.entities.InternalFile
import de.christian2003.data.files.domain.repositories.FileLookupRepository
import de.christian2003.data.files.infrastructure.db.dao.InternalFileDao
import de.christian2003.data.files.infrastructure.db.entities.InternalFileEntity
import de.christian2003.data.files.infrastructure.db.mapper.InternalFileDbMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class RoomFileLookupRepository @Inject constructor(
    private val internalFileDao: InternalFileDao,
    private val internalFileMapper: InternalFileDbMapper
): FileLookupRepository {

    override fun getFilesForNames(internalFileNames: List<String>): Flow<List<InternalFile>> {
        val entities: Flow<List<InternalFileEntity>> = internalFileDao.selectInternalFilesByInternalNames(internalFileNames)
        val internalFiles: Flow<List<InternalFile>> = entities.map { list ->
            list.map { entity ->
                internalFileMapper.toDomain(entity)
            }
        }
        return internalFiles
    }


    override suspend fun createFile(internalFile: InternalFile) {
        val entity: InternalFileEntity = internalFileMapper.toEntity(internalFile)
        internalFileDao.insert(entity)
    }


    override suspend fun deleteFile(internalFile: InternalFile) {
        val entity: InternalFileEntity = internalFileMapper.toEntity(internalFile)
        internalFileDao.delete(entity)
    }


    override suspend fun updateFile(internalFile: InternalFile) {
        val entity: InternalFileEntity = internalFileMapper.toEntity(internalFile)
        internalFileDao.update(entity)
    }

}
