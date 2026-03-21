package de.christian2003.data.files.domain.repositories

import de.christian2003.data.files.domain.entities.InternalFile
import de.christian2003.data.files.domain.entities.SharedFileMetadata


internal interface SharedFilesRepository {

    /**
     * Returns a list of the metadata of all shared files.
     *
     * @return  List contains the metadata for all shared files.
     */
    fun getSharedFiles(): List<SharedFileMetadata>


    /**
     * Adds the specified internal file to the shared files.
     *
     * @param file  File to add to the shared files.
     * @return      Actual name of the shared file. This is usually the actual name of the internal
     *              file. However, if the internal file was shared and then renamed, this can return
     *              the old name, because then the file is shared under the previous name.
     */
    fun addSharedFile(file: InternalFile): String


    /**
     * Removes the files specified from the shared files.
     *
     * @param internalFileNames List of the internal names for the files to delete
     */
    fun removeSharedFiles(internalFileNames: List<String>)

}
