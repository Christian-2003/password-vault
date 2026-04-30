package de.christian2003.data.files.application.usecases

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.core.security.domain.services.HmacCipherService
import de.christian2003.data.files.domain.entities.InternalDirectory
import de.christian2003.data.files.domain.entities.InternalFile
import java.io.File
import java.io.InputStream
import javax.inject.Inject


/**
 * Use case to get a stream for an internal file.
 *
 * @param context           Context.
 * @param hmacCipherService HMAC cipher service used to encrypt and decrypt file streams.
 */
class GetStreamForInternalFileUseCase @Inject internal constructor(
    @param:ApplicationContext private val context: Context,
    private val hmacCipherService: HmacCipherService
) {

    /**
     * Absolute path to the internal files.
     */
    private val absoluteInternalDirPath: File = File(context.filesDir, "userfiles")


    /**
     * Returns the input stream to use in order to read the specified internal file from the internal
     * directory. If the stream cannot be opened, null is returned.
     *
     * @param file  Internal file whose input stream to return.
     * @param dir   Internal directory in which the file is located.
     * @return      Input stream to read the specified internal file or null.
     */
    fun getInputStreamFor(file: InternalFile, dir: InternalDirectory): InputStream? {
        val absoluteDirPath = File(absoluteInternalDirPath, dir.internalPath)
        val absoluteFilePath = File(absoluteDirPath, file.internalName)

        if (absoluteFilePath.exists()) {
            return hmacCipherService.decryptStream(
                input = absoluteFilePath.inputStream(),
                hmacSeed = file.internalName.toByteArray()
            )
        }

        return null
    }

}
