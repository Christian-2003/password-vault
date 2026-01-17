package de.christian2003.security.infrastructure.services

import de.christian2003.security.domain.services.KdfService
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.inject.Inject


/**
 * Implementation of the KDF service using Password-Based Key Derivation Function 2.
 */
class Pbkdf2Service @Inject constructor(): KdfService {

    /**
     * Derives another key based on the specified input.
     *
     * @param input Input from which to derive a key.
     * @param salt  Salt.
     * @return      Derived key.
     */
    override suspend fun derive(input: ByteArray, salt: ByteArray): ByteArray {
        val inputAsCharArray: CharArray = byteArrayToCharArray(input)
        val result: ByteArray = derive(inputAsCharArray, salt)
        return result
    }

    /**
     * Derives another key based on the specified input.
     *
     * @param input Input from which to derive a key.
     * @param salt  Salt
     * @return      Derived key.
     */
    override suspend fun derive(input: CharArray, salt: ByteArray): ByteArray {
        val result: ByteArray = deriveKeyWithSalt(input, salt)
        return result
    }


    /**
     * Derives a key with the specified salt.
     *
     * @param input Input from which to derive a key.
     * @param salt  Salt to use for key derivation.
     * @return      Derived key bytes.
     */
    private fun deriveKeyWithSalt(input: CharArray, salt: ByteArray): ByteArray {
        val keySpec = PBEKeySpec(input, salt, 600_000, 256)
        val factory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2withHmacSHA512")
        val result: ByteArray = factory.generateSecret(keySpec).encoded
        return result
    }


    /**
     * Converts the specified byte array to a char array.
     *
     * @param bytes Byte array to convert to a char array.
     * @return      Converted char array.
     */
    private fun byteArrayToCharArray(bytes: ByteArray): CharArray {
        val chars = CharArray(bytes.size) { i ->
            (bytes[i].toInt() and 0xFF).toChar()
        }
        return chars
    }

}
