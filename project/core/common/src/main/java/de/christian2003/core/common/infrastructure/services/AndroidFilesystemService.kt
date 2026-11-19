package de.christian2003.core.common.infrastructure.services

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.documentfile.provider.DocumentFile
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.core.common.domain.services.FilesystemService
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject


/**
 * Implementation for the Filesystem service using the Android storage framework.
 */
internal class AndroidFilesystemService @Inject constructor(
    @param:ApplicationContext private val context: Context
): FilesystemService {

    /**
     * Checks whether the file with the specified URI exists.
     *
     * @param file  URI of the file for which to check whether it exists.
     */
    override fun exists(file: Uri): Boolean {
        var result: Boolean = false

        //First, try using java.io.File to check:
        if (file.path != null) {
            val javaFile = File(file.path!!)
            result = javaFile.exists()
        }

        //Second, try using ContentResolver to check:
        if (!result) {
            try {
                val fileDescriptor: ParcelFileDescriptor? = context.contentResolver.openFileDescriptor(file, "r")

                if (fileDescriptor != null) {
                    result = true
                    fileDescriptor.close()
                }
            }
            catch (_: Exception) { }
        }

        //Third, try using DocumentFile to check:
        if (!result) {
            val documentFile: DocumentFile? = DocumentFile.fromSingleUri(context, file)
            if (documentFile != null && documentFile.exists()) {
                result = true
            }
        }

        return result
    }


    /**
     * Returns the MIME type for the specified file.
     *
     * @param file                      URI of the file whose MIME type to return.
     * @return                          MIME type of the specified file.
     * @throws FileNotFoundException    The specified file does not exist.
     */
    override fun getMimeType(file: Uri): String {
        ensureExists(file)

        return getMimeTypeInternal(file)
    }


    /**
     * Returns the extension for the specified file.
     * Examples:
     *  - "hello.pdf"      -> "pdf"
     *  - "abc.def"        -> "def"
     *  - "export.pvx (1)" -> "pvx"
     *
     * @param file                      URI of the file whose extension to return.
     * @return                          Extension of the specified file.
     * @throws FileNotFoundException    The specified file does not exist.
     */
    override fun getExtension(file: Uri): String {
        ensureExists(file)

        return getExtensionInternal(file)
    }


    /**
     * Returns the file name without extension for the specified file.
     * Examples:
     *  - "hello.pdf"      -> "hello"
     *  - "abc.def"        -> "abc"
     *  - "export.pvx (1)" -> "export"
     *
     * @param file                      URI of the file whose name to return.
     * @return                          Name without extension of the specified file.
     * @throws FileNotFoundException    The specified file does not exist.
     */
    override fun getNameWithoutExtension(file: Uri): String {
        ensureExists(file)

        return getNameWithoutExtensionInternal(file)
    }


    /**
     * Returns the file name with extension for the specified file.
     * Examples:
     *  - "hello.pdf"      -> "hello.pdf"
     *  - "abc.def"        -> "abc.def"
     *  - "export.pvx (1)" -> "export.pvx (1)"
     *
     * @param file                      URI of the file whose name to return.
     * @return                          Name with extension of the specified file.
     * @throws FileNotFoundException    The specified file does not exist.
     */
    override fun getNameWithExtension(file: Uri): String {
        ensureExists(file)

        return getNameWithExtensionInternal(file)
    }


    /**
     * Returns the MIME type for the specified file.
     *
     * @param file                      URI of the file whose MIME type to return.
     * @return                          MIME type of the specified file.
     * @throws IllegalArgumentException Unspecified internal error
     */
    private fun getMimeTypeInternal(file: Uri): String {
        var mimeType: String? = null

        when (file.scheme) {
            ContentResolver.SCHEME_CONTENT -> {
                mimeType = context.contentResolver.getType(file)
            }

            ContentResolver.SCHEME_FILE -> {
                val extension: String = getExtensionInternal(file)
                val mimeTypeMap: MimeTypeMap = MimeTypeMap.getSingleton()

                mimeType = mimeTypeMap.getMimeTypeFromExtension(extension)
            }
        }

        if (mimeType == null) {
            throw IllegalArgumentException("Unspecified internal error")
        }
        return mimeType
    }


    /**
     * Returns the extension for the specified file.
     * Examples:
     *  - "hello.pdf"      -> "pdf"
     *  - "abc.def"        -> "def"
     *  - "export.pvx (1)" -> "pvx"
     *
     * @param file                      URI of the file whose extension to return.
     * @return                          Extension of the specified file.
     * @throws IllegalArgumentException Unspecified internal error.
     */
    private fun getExtensionInternal(file: Uri): String {
        var extension: String? = null
        val nameWithExtension: String = getNameWithExtensionInternal(file)

        val dotIndex: Int = nameWithExtension.lastIndexOf('.')
        if (dotIndex >= 0 && dotIndex < nameWithExtension.lastIndex) {
            extension = nameWithExtension.substring(dotIndex + 1)
        }

        //Some files contain "(<n>)" after the extension (e.g. "export.pvx (3)"). This can happen if
        //multiple files are created with the same name / extension. In this case, "(<n>)" may be
        //appended by some filesystems if the extension is not a known extension.
        //For known extensions (e.g. "txt" or "pdf", the "(<n>)" is usually appended after the filename
        //and before the extension (e.g. "example (3).txt").
        if (extension != null && extension.contains('(')) {
            val braceIndex: Int = extension.indexOf('(')
            if (braceIndex > 0 && braceIndex < extension.lastIndex) {
                extension = extension.substring(0, braceIndex)
            }
        }

        if (extension != null) {
            extension = extension.trim().lowercase()
        }

        if (extension == null) {
            throw IllegalArgumentException("Unspecified internal error")
        }
        return extension
    }


    /**
     * Returns the file name without extension for the specified file.
     * Examples:
     *  - "hello.pdf"      -> "hello"
     *  - "abc.def"        -> "abc"
     *  - "export.pvx (1)" -> "export"
     *
     * @param file                      URI of the file whose name to return.
     * @return                          Name without extension of the specified file.
     * @throws IllegalArgumentException Unspecified internal error.
     */
    private fun getNameWithoutExtensionInternal(file: Uri): String {
        var nameWithoutExtension: String? = null
        val nameWithExtension: String = getNameWithExtensionInternal(file)

        val dotIndex: Int = nameWithExtension.lastIndexOf('.')
        if (dotIndex >= 0 && dotIndex < nameWithExtension.lastIndex) {
            nameWithoutExtension = nameWithExtension.substring(0, dotIndex)
        }

        if (nameWithoutExtension == null) {
            throw IllegalArgumentException("Unspecified internal error")
        }
        return nameWithoutExtension
    }


    /**
     * Returns the file name with extension for the specified file.
     * Examples:
     *  - "hello.pdf"      -> "hello.pdf"
     *  - "abc.def"        -> "abc.def"
     *  - "export.pvx (1)" -> "export.pvx (1)"
     *
     * @param file                      URI of the file whose name to return.
     * @return                          Name with extension of the specified file.
     * @throws IllegalArgumentException Unspecified internal error.
     */
    private fun getNameWithExtensionInternal(file: Uri): String {
        var fileName: String? = null

        when (file.scheme) {
            ContentResolver.SCHEME_CONTENT -> {
                val projection: Array<String> = arrayOf(OpenableColumns.DISPLAY_NAME)
                val selection: String? = null
                val selectionArgs: Array<String>? = null
                val sortOrder: String? = null

                val cursor: Cursor? = context.contentResolver.query(file, projection, selection, selectionArgs, sortOrder)

                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        fileName = cursor.getString(0)
                    }
                    cursor.close()
                }
            }

            ContentResolver.SCHEME_FILE -> {
                fileName = file.lastPathSegment
            }
        }

        if (fileName == null) {
            throw IllegalArgumentException("Unspecified internal error")
        }
        return fileName
    }


    /**
     * Makes sure that the file with the specified URI exists. Otherwise, an exception is thrown.
     *
     * @param file                      URI of the file to ensure exists.
     * @throws FileNotFoundException    The file does not exist.
     */
    private fun ensureExists(file: Uri) {
        if (!exists(file)) {
            throw FileNotFoundException("'${file.path ?: "about:blank"}' does not exist")
        }
    }

}
