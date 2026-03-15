package de.christian2003.data.files.domain.repositories

import android.net.Uri
import de.christian2003.data.files.domain.entities.InternalDirectory
import kotlinx.coroutines.flow.Flow


internal interface InternalFilesystemRepository {

    fun getAllSubdirectories(directory: InternalDirectory): Flow<List<InternalDirectory>>


    fun addDirectory(directory: InternalDirectory)


    fun updateDirectory(currentInternalPath: String, updatedDirectory: InternalDirectory)


    fun deleteDirectory(directory: InternalDirectory)



    fun getAllFileNamesInDirectory(directory: InternalDirectory): Flow<List<String>>

    suspend fun copyFileToDirectory(sourceFileUri: Uri, destinationFileName: String, directory: InternalDirectory)

    fun deleteFileFromDirectory(fileName: String, directory: InternalDirectory)

}
