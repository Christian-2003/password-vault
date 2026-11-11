package de.christian2003.filesysteminspector.application

import android.content.ContentResolver
import android.webkit.MimeTypeMap
import androidx.compose.ui.text.toLowerCase
import androidx.documentfile.provider.DocumentFile
import de.christian2003.filesysteminspector.domain.FileMetadata

class FileMetadataProvider {

    fun getMetadata(
        file: DocumentFile,
        contentResolver: ContentResolver
    ): FileMetadata {
        val name: String = file.name ?: "Unknown"

        val extension: String? = name
            .substringAfterLast('.', "")
            .takeIf { it.isNotEmpty() }
            ?.lowercase()

        val mimeType = file.type
            ?: extension?.let {
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(it)
            }

        return FileMetadata(
            name = name,
            path = file.uri.toString(),
            size = if (file.isFile) file.length() else null,
            mimeType = mimeType,
            fileType = getFileType(mimeType, file.isDirectory),
            extension = extension,
            lastModified = file.lastModified().takeIf { it > 0 },
            isDirectory = file.isDirectory,
            canRead = file.canRead(),
            canWrite = file.canWrite()
        )
    }


    private fun getFileType(
        mimeType: String?,
        isDirectory: Boolean
    ): String {
        if (isDirectory) {
            return "Directory"
        }

        if (mimeType == null) {
            return "Unknown"
        }

        return when {
            mimeType.startsWith("image/") -> "Image"
            mimeType.startsWith("video/") -> "Video"
            mimeType.startsWith("audio/") -> "Audio"
            mimeType.startsWith("text/") -> "Text"
            mimeType == "application/pdf" -> "PDF"
            mimeType.contains("zip") -> "Archive"
            mimeType.contains("compressed") -> "Archive"
            mimeType.contains("spreadsheet") -> "Spreadsheet"
            mimeType.contains("word") -> "Document"
            mimeType.contains("presentation") -> "Presentation"
            else -> "File"
        }
    }

}
