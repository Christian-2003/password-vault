package de.christian2003.core.security.application.services

import java.security.SecureRandom


/**
 * Service to generate a random salt.
 */
internal class SaltGeneratorService {

    /**
     * Generates a random salt as bytes.
     *
     * @return  Random salt.
     */
    fun generateSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(32)
        random.nextBytes(salt)
        return salt
    }

}
