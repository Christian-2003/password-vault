package de.christian2003.data.files.application.services

import javax.inject.Inject


/**
 * Service for validating the names for internal files.
 */
class InternalFileNameValidatorService @Inject constructor() {

    /**
     * Regex contains all illegal characters and matches illegal file names.
     */
    private val invalidCharacters: Regex = Regex("(.*[\\\\/:*?\"<>|].*)|(^\\..*)")


    /**
     * Tests whether the provided file name is valid.
     *
     * @param fileName  File name to validate.
     * @return          Whether the provided file name is valid.
     */
    fun isValid(fileName: String): Boolean {
        return fileName.isNotBlank() && !fileName.matches(invalidCharacters)
    }

}
