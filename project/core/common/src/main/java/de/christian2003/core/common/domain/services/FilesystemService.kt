package de.christian2003.core.common.domain.services

import android.net.Uri
import java.io.FileNotFoundException


/**
 * Service used for filesystem operations.
 */
interface FilesystemService {

    /**
     * Checks whether the file with the specified URI exists.
     *
     * @param file  URI of the file for which to check whether it exists.
     */
    fun exists(file: Uri): Boolean


    /**
     * Returns the MIME type for the specified file.
     *
     * @param file                      URI of the file whose MIME type to return.
     * @return                          MIME type of the specified file.
     * @throws FileNotFoundException    The specified file does not exist.
     */
    fun getMimeType(file: Uri): String


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
    fun getExtension(file: Uri): String


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
    fun getNameWithoutExtension(file: Uri): String


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
    fun getNameWithExtension(file: Uri): String

}
