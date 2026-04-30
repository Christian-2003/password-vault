package de.christian2003.data.files.application.usecases

import de.christian2003.data.files.domain.entities.InternalFile
import de.christian2003.data.files.domain.repositories.FileLookupRepository
import javax.inject.Inject


/**
 * Use case to get an internal file by it's internal name.
 *
 * @param fileLookupRepository  Repository to lookup internal files.
 */
class GetInternalFileByFileNameUseCase @Inject internal constructor(
    private val fileLookupRepository: FileLookupRepository
) {

    /**
     * Returns the internal file with the specified internal file name or null if no file can be
     * determined.
     *
     * @param internalFileName  Internal name of the file to return.
     * @return                  Internal file for the specified internal name.
     */
    suspend fun getInternalFile(internalFileName: String): InternalFile? {
        return fileLookupRepository.getFileForName(internalFileName)
    }

}
