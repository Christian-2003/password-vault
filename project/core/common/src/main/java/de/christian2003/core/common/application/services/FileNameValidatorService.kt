package de.christian2003.core.common.application.services

import javax.inject.Inject


/**
 * Service for validating the names for internal files.
 */
class FileNameValidatorService @Inject constructor() {

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


    /**
     * Replaces all illegal characters in the provided file name with '_'.
     *
     * @param fileName  File name in which to replace illegal characters.
     * @return          File name containing only legal characters.
     */
    fun replaceIllegalChars(fileName: String): String {
        var validated: String = fileName

        val illegalCharacters: List<Char> = listOf('\\', '/', ':', '*', '?', '"', '<', '>', '|')
        illegalCharacters.forEach { illegalChar ->
            validated = validated.replace(illegalChar, '_')
        }

        if (validated.startsWith('.')) {
            validated = validated.replaceRange(0, 1, "_")
        }

        return validated
    }

}
