package de.christian2003.data.files.domain.services

import android.net.Uri
import de.christian2003.data.files.domain.entities.InternalDirectory
import de.christian2003.data.files.domain.entities.InternalFile
import de.christian2003.data.files.domain.entities.SharedFile


internal interface FileCopyService {

    suspend fun copyExternalFileToInternal(
        sourceUri: Uri,
        destinationInternalFilePath: String
    ): String


    /**
     * Copies the specified internal file from the specified directory to the shared files, where it
     * can be opened by other apps. The content URI for the shared file is returned afterwards.
     *
     * @param internalFile  File to copy to shared.
     * @param directory     Directory in which the internal file is located.
     * @return              Data of the shared file or null it the file cannot be shared.
     */
    suspend fun copyInternalFileToShared(internalFile: InternalFile, directory: InternalDirectory): SharedFile?

}
