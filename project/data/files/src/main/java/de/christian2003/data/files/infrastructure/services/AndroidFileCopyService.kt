package de.christian2003.data.files.infrastructure.services

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.core.security.domain.services.HmacCipherService
import de.christian2003.data.files.application.services.MimeTypeMapperService
import de.christian2003.data.files.domain.entities.InternalDirectory
import de.christian2003.data.files.domain.entities.InternalFile
import de.christian2003.data.files.domain.entities.SharedFile
import de.christian2003.data.files.domain.services.FileCopyService
import java.io.File
import java.io.InputStream
import javax.inject.Inject


internal class AndroidFileCopyService @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val hmacCipherService: HmacCipherService,
    private val mimeTypeMapperService: MimeTypeMapperService
): FileCopyService {

    /**
     * Absolute path to the root directory of the user files:
     * ".../data/data/de.christian2003.passwordvault/files/userfiles"
     */
    private val absoluteInternalDirPath: File = File(context.filesDir, "userfiles")

    /**
     * Absolute path to the root directory of shared (decrypted) files:
     * ".../data/data/de.christian2003.passwordvault/cache/shared"
     */
    private val absoluteSharedDirPath: File = File(context.cacheDir, "shared")


    override suspend fun copyExternalFileToInternal(
        sourceUri: Uri,
        destinationInternalFilePath: String
    ): String {
        val inputStream: InputStream? = context.contentResolver.openInputStream(sourceUri)
        if (inputStream == null) {
            throw IllegalArgumentException("Cannot open input stream")
        }

        val outputFile: File = getAbsolutePathForDir(destinationInternalFilePath)
        val outputFileInternalName: String = getInternalFilenameFromInternalPath(destinationInternalFilePath)

        inputStream.use { input ->
            outputFile.outputStream().use { output ->
                hmacCipherService.encryptStream(output, outputFileInternalName.toByteArray()).use { encryptedOutput ->
                    input.copyTo(encryptedOutput)
                }
            }
        }

        //Get the original file name:
        var originalFileName = ""
        context.contentResolver.query(sourceUri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && cursor.moveToFirst()) {
                originalFileName = cursor.getString(nameIndex)
            }
        }

        return originalFileName
    }


    /**
     * Copies the specified internal file from the specified directory to the shared files, where it
     * can be opened by other apps. The content URI for the shared file is returned afterwards.
     *
     * @param internalFile  File to copy to shared.
     * @param directory     Directory in which the internal file is located.
     * @return              Data of the shared file or null it the file cannot be shared.
     */
    override suspend fun copyInternalFileToShared(internalFile: InternalFile, directory: InternalDirectory): SharedFile? {
        try {
            val internalSourcePath = "${directory.internalPath}/${internalFile.internalName}"
            val absoluteSourcePath: File = getAbsolutePathForDir(internalSourcePath)
            if (!absoluteSourcePath.exists()) {
                return null
            }

            val destinationFileName = internalFile.actualFileName
            val absoluteDestPath: File = getAbsolutePathForShared(destinationFileName)

            //Prepare shared:
            if (!absoluteSharedDirPath.exists()) {
                absoluteSharedDirPath.mkdirs()
            }
            if (absoluteDestPath.exists()) {
                absoluteDestPath.delete()
            }

            //Copy to shared cache:
            absoluteSourcePath.inputStream().use { input ->
                absoluteDestPath.outputStream().use { output ->
                    val internalFileName: String = getInternalFilenameFromInternalPath(internalFile.internalName)
                    hmacCipherService.decryptStream(input, internalFileName.toByteArray()).use { encryptedInput ->
                        encryptedInput.copyTo(output)
                    }
                }
            }

            //Generate content URI:
            val contentUri: Uri = FileProvider.getUriForFile(context, "de.christian2003.fileprovider", absoluteDestPath)
            return SharedFile(
                contentUri = contentUri,
                mimeType = mimeTypeMapperService.mapFilenameToMimeType(destinationFileName)
            )
        }
        catch (_: Exception) {
            return null
        }
    }


    private fun getAbsolutePathForDir(internalPath: String): File {
        return File(absoluteInternalDirPath, internalPath)
    }

    private fun getAbsolutePathForShared(sharedFileName: String): File {
        return File(absoluteSharedDirPath, sharedFileName)
    }


    private fun getInternalFilenameFromInternalPath(internalPath: String): String {
        val separatorIndex: Int = internalPath.lastIndexOf('/')
        return if (separatorIndex >= 0 && separatorIndex < internalPath.length - 1) {
            internalPath.substring(separatorIndex + 1)
        }
        else {
            internalPath
        }
    }

}
