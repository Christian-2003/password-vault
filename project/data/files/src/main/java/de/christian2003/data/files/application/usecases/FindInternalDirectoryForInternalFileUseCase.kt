package de.christian2003.data.files.application.usecases

import de.christian2003.data.files.domain.entities.InternalDirectory
import de.christian2003.data.files.domain.entities.InternalFile
import de.christian2003.data.files.domain.repositories.InternalFilesystemRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject


/**
 * Use case to find the internal directory for an internal file.
 *
 * @param filesystemRepository  Repository to access the internal filesystem.
 */
class FindInternalDirectoryForInternalFileUseCase @Inject internal constructor(
    private val filesystemRepository: InternalFilesystemRepository
) {

    /**
     * Finds the internal directory in which the specified internal file is stored. If the internal
     * file is found, it's internal directory is returned. If the file cannot be found, null is
     * returned instead.
     *
     * CAUTION: This method recursively iterates all internal directories until the file is either
     *      found or all directories have been visited. Repeatedly calling this method in order to
     *      search for multiple files in large nested directory structures may definitely introduce
     *      a measurable performance impact!
     *
     * @param internalFile  Internal file for which to find the internal directory.
     * @return              Internal directory containing the specified internal file or null.
     */
    suspend fun findInternalDirectoryFor(internalFile: InternalFile): InternalDirectory? {
        val rootDir: InternalDirectory = InternalDirectory("")
        return findInternalDirForFile(rootDir, internalFile)
    }


    /**
     * Recursively iterates through the provided internal directory and searches for the specified
     * internal file. If the file is found in an internal directory, the directory is returned,
     * otherwise null is returned.
     *
     * @param internalDir   Internal directory to search.
     * @param internalFile  Internal file to search in the directory.
     * @return              Internal directory which contains the file or null.
     */
    private suspend fun findInternalDirForFile(internalDir: InternalDirectory, internalFile: InternalFile): InternalDirectory? {
        val internalNames: List<String> = filesystemRepository.getAllFileNamesInDirectory(internalDir).first()

        if (internalNames.contains(internalFile.internalName)) {
            return internalDir
        }

        val subDirs: List<InternalDirectory> = filesystemRepository.getAllSubdirectories(internalDir).first()
        subDirs.forEach { internalSubDir ->
            val result: InternalDirectory? = findInternalDirForFile(internalSubDir, internalFile)
            if (result != null) {
                return result
            }
        }

        return null
    }

}
