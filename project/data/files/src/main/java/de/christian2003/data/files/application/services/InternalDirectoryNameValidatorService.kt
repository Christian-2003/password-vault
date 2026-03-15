package de.christian2003.data.files.application.services

import javax.inject.Inject


/**
 * Service for validating the names for internal directories.
 */
class InternalDirectoryNameValidatorService @Inject constructor() {

    /**
     * Regex contains all illegal characters and matches illegal directory names.
     */
    private val invalidCharacters: Regex = Regex(".*[\\\\/:*?\"<>|].*")


    /**
     * Tests whether the provided directory name is valid.
     *
     * @param directoryName Directory name to validate.
     * @return              Whether the provided directory name is valid.
     */
    fun isValid(directoryName: String): Boolean {
        return directoryName.isNotBlank() && !directoryName.matches(invalidCharacters)
    }

}
