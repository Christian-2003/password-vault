package de.christian2003.data.files.infrastructure.services

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.core.security.domain.services.HmacCipherService
import de.christian2003.data.files.domain.entities.InternalDirectory
import de.christian2003.data.files.domain.entities.InternalFile
import de.christian2003.data.files.domain.entities.SharedFile
import de.christian2003.data.files.domain.services.FileCopyService
import java.io.File
import java.io.InputStream
import java.io.OutputStream
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
                mimeType = mapFileNameToMimeType(destinationFileName)
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


    private fun mapFileNameToMimeType(fileName: String): String {
        val dotIndex: Int = fileName.lastIndexOf('.')
        val fileExtension: String = if (dotIndex >= 0 && dotIndex < fileName.length - 2) {
            fileName.substring(dotIndex + 1)
        } else {
            ""
        }

        return when (fileExtension) {
            "aac" -> "audio/aac"
            "abw" -> "application/x-abiword"
            "apng" -> "image/apng"
            "arc" -> "application/x-freearc"
            "avif" -> "image/avif"
            "avi" -> "video/x-msvideo"
            "azw" -> "application/vnd.amazon.ebook"
            "bin" -> "application/octet-stream"
            "bmp" -> "image/bmp"
            "bz" -> "application/x-bzip"
            "bz2" -> "application/x-bzip2"
            "cda" -> "application/x-cdf"
            "csh" -> "application/x-csh"
            "css" -> "text/css"
            "csv" -> "text/csv"
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "eot" -> "application/vnd.ms-fontobject"
            "epub" -> "application/epub+zip"
            "gz" -> "application/gzip"
            "gif" -> "image/gif"
            "htm", "html" -> "text/html"
            "ico" -> "image/vnd.microsoft.icon"
            "ics" -> "text/calendar"
            "jar" -> "application/java-archive"
            "jpg", "jpeg" -> "image/jpeg"
            "js", "mjs" -> "text/javascript"
            "json" -> "application/json"
            "jsonld" -> "application/ld+json"
            "md" -> "text/markdown"
            "mid", "midi" -> "audio/midi"
            "mp3", "mpeg" -> "audio/mpeg"
            "mp4" -> "video/mp4"
            "mpkg" -> "application/vnd.apple.installer+xml"
            "odp" -> "application/vnd.oasis.opendocument.presentation"
            "ods" -> "application/vnd.oasis.opendocument.spreadsheet"
            "odt" -> "application/vnd.oasis.opendocument.text"
            "oga", "ogv", "opus" -> "audio/ogg"
            "ogx" -> "application/ogg"
            "otf" -> "font/otf"
            "png" -> "image/png"
            "pdf" -> "application/pdf"
            "php" -> "application/x-httpd-php"
            "ppt" -> "application/vnd.ms-powerpoint"
            "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            "rar" -> "application/vnd.rar"
            "rtf" -> "application/rtf"
            "sh" -> "application/x-sh"
            "svg" -> "image/svg+xml"
            "tar" -> "application/x-tar"
            "tif", "tiff" -> "image/tiff"
            "ts" -> "video/mp2t"
            "ttf" -> "font/ttf"
            "txt" -> "text/plain"
            "vsd" -> "application/vnd.visio"
            "wav" -> "audio/wav"
            "weba", "webm" -> "audio/webm"
            "webmanifest" -> "application/manifest+json"
            "webp" -> "image/webp"
            "woff" -> "image/woff"
            "woff2" -> "image/woff2"
            "xhtml" -> "application/xhtml+xml"
            "xls" -> "application/vnd.ms-excel"
            "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            "xml" -> "application/xml"
            "xul" -> "application/vnd.mozilla.xul+xml"
            "zip" -> "application/zip"
            "3gp" -> "video/3gpp"
            "3g2" -> "video/3gpp2"
            "7z" -> "application/x-7z-compressed"
            else -> "*/*"
        }
    }

}
