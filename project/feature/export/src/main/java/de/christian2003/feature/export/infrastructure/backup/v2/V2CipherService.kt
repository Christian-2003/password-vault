package de.christian2003.feature.export.infrastructure.backup.v2

import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Base64
import javax.crypto.AEADBadTagException
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject


/**
 * Custom cipher service which mimics the AES encryption used in Password Vault prior to v3.8.0.
 * Prior to v3.8.0, see: 'app\src\main\java\de\passwordvault\model\storage\encryption\AES.java'
 *
 * This custom encryption is required to decrypt encrypted V2 backups.
 */
internal class V2CipherService @Inject constructor() {

    /**
     * After initCipher(), this stores the secret key used for subsequent decryption.
     */
    var key: SecretKey? = null


    /**
     * Initializes the cipher with the specified key seed.
     *
     * @param seed                      Seed for the encryption key.
     * @throws IllegalStateException    Cannot generate key from seed.
     */
    fun initCipher(seed: CharArray) {
        val digest: MessageDigest

        val seedBytes: ByteArray = charArrayToByteArray(seed)
        var hash: ByteArray? = null

        try {
            digest = MessageDigest.getInstance("SHA-256")

            hash = digest.digest(seedBytes)
            key = SecretKeySpec(hash, 0, 16, "AES/GCM/NoPadding")
        }
        catch (_: Exception) {
            throw IllegalStateException("Cannot generate key from seed")
        }
        finally {
            seedBytes.fill(0)
            hash?.fill(0)
        }
    }


    /**
     * Decrypts the provided cipher text.
     *
     * @param cipherText                Cipher text to decrypt.
     * @return                          Decrypted plain text.
     * @throws IllegalStateException    Cannot decrypt the cipher text.
     * @throws AEADBadTagException      Cannot decrypt with current key. Call initCipher() with valid
     *                                  seed and try again.
     */
    fun decrypt(cipherText: String): String {
        if (key == null) {
            throw IllegalStateException("Must call initCipher() before decrypting")
        }

        var decryptedBytes: ByteArray? = null
        try {
            val encryptedBytes: ByteArray = Base64.getDecoder().decode(cipherText)
            val iv = ByteArray(12)
            encryptedBytes.copyInto(iv, 0, 0, iv.size)

            val cipher: Cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))

            decryptedBytes = cipher.doFinal(encryptedBytes, iv.size, encryptedBytes.size - iv.size)

            return decryptedBytes.toString(Charsets.UTF_8)
        }
        catch (e: Exception) {
            when (e) {
                is AEADBadTagException -> throw e
                else -> throw IllegalStateException("Unexpected error during V2 backup decryption")
            }
        }
        finally {
            //Zero internal arrays:
            decryptedBytes?.fill(0)
        }
    }


    /**
     * Converts the provided CharArray into a ByteArray.
     *
     * @param chars CharArray to convert.
     * @return      Converted ByteArray.
     */
    private fun charArrayToByteArray(chars: CharArray): ByteArray {
        val charBuffer: CharBuffer = CharBuffer.wrap(chars)
        val byteBuffer: ByteBuffer = StandardCharsets.UTF_8.encode(charBuffer)

        val bytes = ByteArray(byteBuffer.remaining())
        byteBuffer.get(bytes)

        //Zero buffer:
        byteBuffer.clear()
        while (byteBuffer.hasRemaining()) {
            byteBuffer.put(0)
        }
        //Do NOT zero CharBuffer, since this would zero the input CharArray!

        return bytes
    }

}
