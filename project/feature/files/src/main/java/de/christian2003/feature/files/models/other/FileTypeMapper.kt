package de.christian2003.feature.files.models.other

import javax.inject.Inject


internal class FileTypeMapper @Inject constructor() {

    fun mapMimeTypeToFileType(mimeType: String): FileType {
        val type: String = mimeType.substringBefore("/").lowercase()
        val subtype: String = mimeType.substringAfter("/").lowercase()

        return when (type) {
            "image" -> FileType.Image
            "audio" -> FileType.Audio
            "video" -> FileType.Video
            "text" -> {
                when (subtype) {
                    "markdown" -> FileType.Markdown
                    else -> FileType.Text
                }
            }
            "font" -> FileType.Font
            "message" -> FileType.Message
            "model" -> FileType.Model
            "application" -> {
                when {
                    subtype.contains("msword") || subtype.contains("wordprocessingml") -> FileType.Word
                    subtype.contains("spreadsheetml") || subtype.contains("excel") || subtype.contains("vnd.ms-excel") -> FileType.Excel
                    subtype.contains("presentationml") || subtype.contains("powerpoint") || subtype.contains("vnd.ms-powerpoint") -> FileType.PowerPoint
                    subtype.contains("pdf") -> FileType.Pdf
                    subtype.contains("epub") -> FileType.Generic
                    subtype.contains("x-") && (subtype.contains("zip") || subtype.contains("rar") || subtype.contains("tar") || subtype.contains("7z") || subtype.contains("arj") || subtype.contains("cab")) -> FileType.Archive
                    subtype.contains("zip") || subtype.contains("7z") || subtype.contains("rar") || subtype.contains("tar") -> FileType.Archive
                    subtype.contains("json") || subtype.contains("xml") -> FileType.Code
                    subtype.contains("javascript") -> FileType.Code
                    subtype.contains("x-httpd-php") -> FileType.Code
                    subtype.contains("x-sh") || subtype.contains("x-csh") -> FileType.Code
                    subtype.contains("apk") || subtype.contains("vnd.android.package-archive") -> FileType.Apk
                    else -> FileType.Generic
                }
            }
            else -> FileType.Generic
        }
    }

}
