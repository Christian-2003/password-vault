package de.christian2003.data.files.infrastructure.services

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.core.security.domain.services.HmacCipherService
import de.christian2003.data.files.domain.services.FileCopyService
import java.io.File
import java.io.InputStream
import javax.inject.Inject


internal class AndroidFileCopyService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val hmacCipherService: HmacCipherService
): FileCopyService {

    /**
     * Absolute path to the root directory of the user files:
     * ".../data/data/de.christian2003.passwordvault/files/userfiles"
     */
    private val absoluteInternalDirPath: File = File(context.filesDir, "userfiles")

    override suspend fun copyExternalFileToInternal(
        sourceUri: Uri,
        destinationInternalFilePath: String
    ) {
        val inputStream: InputStream? = context.contentResolver.openInputStream(sourceUri)
        if (inputStream == null) {
            throw IllegalArgumentException("Cannot open input stream")
        }

        val outputFile: File = getAbsolutePathForDir(destinationInternalFilePath)

        inputStream.use { input ->
            outputFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }


    private fun getAbsolutePathForDir(internalPath: String): File {
        return File(absoluteInternalDirPath, internalPath)
    }

}
