package de.christian2003.security.infrastructure.services

import de.christian2003.security.domain.services.KeyGeneratorService
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


/**
 * Generator can generate cryptographic key for AES.
 */
class AesKeyGeneratorService: KeyGeneratorService {

    /**
     * Generates a cryptographic key.
     *
     * @return  Bytes of the generated key.
     */
    override fun generate(): ByteArray {
        val random = SecureRandom()

        val keyGenerator: KeyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(256, random)

        val secretKey: SecretKey = keyGenerator.generateKey()

        return secretKey.encoded
    }

}
