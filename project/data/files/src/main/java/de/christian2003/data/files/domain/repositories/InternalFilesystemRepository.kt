package de.christian2003.data.files.domain.repositories

import de.christian2003.data.files.domain.entities.InternalDirectory
import kotlinx.coroutines.flow.Flow


internal interface InternalFilesystemRepository {

    fun getAllSubdirectories(directory: InternalDirectory): Flow<List<InternalDirectory>>


    fun addDirectory(directory: InternalDirectory)


    fun deleteDirectory(directory: InternalDirectory)

}
