package de.christian2003.data.files.application.services

import javax.inject.Inject


/**
 * Service to map a file name to it's mime type.
 */
internal class MimeTypeMapperService @Inject constructor() {

    /**
     * Maps the provided file name to the corresponding mime type.
     *
     * @param fileName  File name including extension (e.g. "MyFile.txt").
     * @return          Mime type.
     */
    fun mapFilenameToMimeType(fileName: String): String {
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
