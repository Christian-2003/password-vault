package de.christian2003.data.files.infrastructure.services

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.data.files.domain.services.InternalFileUtilsService
import java.io.File
import javax.inject.Inject


internal class AndroidFileUtilsService @Inject constructor(
    @param:ApplicationContext private val context: Context
) : InternalFileUtilsService {

    /**
     * Stores the path to the internal files.
     * e.g. "/data/user/0/de.christian2003.passwordvault/files/userfiles"
     */
    private val pathToInternalFiles: String = "${context.filesDir}/userfiles"


    override fun getSizeOfInternalFile(internalFilePath: String): Long {
        val absoluteFilePath: String = getAbsolutePathForInternalFile(internalFilePath)
        val file = File(absoluteFilePath)
        val fileSize: Long = file.length()
        return fileSize
    }


    override fun getAbsolutePathForInternalFile(internalFilePath: String): String {
        return "$pathToInternalFiles/$internalFilePath"
    }

}
