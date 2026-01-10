package de.christian2003.security.infrastructure.services

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import de.christian2003.security.domain.services.KdfService
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec


/**
 * Implementation of the KDF service using Password-Based Key Derivation Function 2.
 *
 * @param context   Android context.
 * @param saltKey   Key with which the salt is being stored. If no salt exists with this key,
 *                  the service creates a new salt and stores it with the provided key.
 */
class Pbkdf2Service(
    private val context: Context,
    private val saltKey: String
): KdfService {

    /**
     * Derives another key based on the specified input.
     *
     * @param input Input from which to derive a key.
     * @return      Derived key.
     */
    override fun derive(input: ByteArray): ByteArray {
        val inputAsCharArray: CharArray = byteArrayToCharArray(input)
        val result: ByteArray = derive(inputAsCharArray)
        return result
    }

    /**
     * Derives another key based on the specified input.
     *
     * @param input Input from which to derive a key.
     * @return      Derived key.
     */
    override fun derive(input: CharArray): ByteArray {
        val salt = getOrGenerateSalt()
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
     * Gets the existing salt or generates a new one if none exists already.
     *
     * @return  Random salt.
     */
    private fun getOrGenerateSalt(): ByteArray {
        val preferences: SharedPreferences = context.getSharedPreferences("security", Context.MODE_PRIVATE)
        val saltAsString: String? = preferences.getString(saltKey, null)
        if (saltAsString == null) {
            //Generate new salt:
            val random = SecureRandom()
            val salt = ByteArray(32)
            random.nextBytes(salt)
            val newSaltAsString: String = byteArrayToBase64(salt)
            preferences.edit {
                putString(saltKey, newSaltAsString)
            }
            return salt
        }
        else {
            //Return existing salt:
            val saltAsByteArray: ByteArray = base64ToByteArray(saltAsString)
            return saltAsByteArray
        }
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


    /**
     * Converts the specified byte array to a base64-encoded string.
     *
     * @param bytes Byte array to convert to a base64-encoded string.
     * @return      Base64-encoded string.
     */
    private fun byteArrayToBase64(bytes: ByteArray): String {
        return Base64.getEncoder().encodeToString(bytes)
    }


    /**
     * Converts the specified base64-encoded string to a byte array.
     *
     * @param s Base64-encoded string to convert to a byte array.
     * @return  Converted byte array.
     */
    private fun base64ToByteArray(s: String): ByteArray {
        return Base64.getDecoder().decode(s)
    }

}
