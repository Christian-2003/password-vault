package de.christian2003.feature.analysis.application.services

import javax.inject.Inject


/**
 * Service detects patters like "abcdef" or "123456" in passwords.
 */
internal class PatternDetectionService @Inject constructor() {

    /**
     * Checks whether the specified password contains patterns.
     *
     * @param password  Password to check.
     * @return          Whether the specified password contains patterns.
     */
    fun containsPatterns(password: String): Boolean {
        for (i in 0 until password.length - 2) {
            if (password[i] + 1 == password[i + 1] && password[i] + 2 == password[i + 2]) {
                return true
            }
        }
        return false
    }

}
