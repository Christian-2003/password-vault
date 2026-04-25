package de.christian2003.feature.analysis.application.services

import javax.inject.Inject


internal class PatternDetectionService @Inject constructor() {

    fun containsPatterns(password: String): Boolean {
        for (i in 0 until password.length - 2) {
            if (password[i] + 1 == password[i + 1] && password[i] + 2 == password[i + 2]) {
                return true
            }
        }
        return false
    }

}
