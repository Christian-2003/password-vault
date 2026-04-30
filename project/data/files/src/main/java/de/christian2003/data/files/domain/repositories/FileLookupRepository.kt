package de.christian2003.data.files.domain.repositories

import de.christian2003.data.files.domain.entities.InternalFile
import kotlinx.coroutines.flow.Flow


internal interface FileLookupRepository {

    fun getFilesForNames(internalFileNames: List<String>): Flow<List<InternalFile>>

    suspend fun getFileForName(internalFileName: String): InternalFile?

    fun getAllFiles(): Flow<List<InternalFile>>

    suspend fun createFile(internalFile: InternalFile)

    suspend fun deleteFile(internalFile: InternalFile)

    suspend fun updateFile(internalFile: InternalFile)

}
