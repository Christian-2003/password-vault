package de.christian2003.passwordvault.plugin.infrastructure.security

import de.christian2003.passwordvault.application.security.MasterKeyService
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec


/**
 * AES-implementation that uses the key retrieved from the specified master key service.
 *
 * @param masterKeyService  Service through which to get the bytes of the master key.
 */
class AesHelper(
    private val masterKeyService: MasterKeyService
) {

    /**
     * Encrypts the specified content using AES.
     *
     * @param content   Plain text to encrypt.
     * @return          Encrypted ciphertext.
     */
    fun encrypt(content: ByteArray): ByteArray {
        val cipher: Cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)

        val secretKeySpec = SecretKeySpec(masterKeyService.getMasterKey(), "AES")
        val gcmSpec = GCMParameterSpec(128, iv)

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmSpec)
        val ciphertext: ByteArray = cipher.doFinal(content)

        val final: ByteArray = iv + ciphertext
        iv.fill(0) //Wipe internal array
        ciphertext.fill(0) //Wipe internal array

        return final
    }


    /**
     * Decrypts the specified content using AES.
     *
     * @param content   Ciphertext to decrypt.
     * @return          Decrypted plain text.
     */
    fun decrypt(content: ByteArray): ByteArray {
        val cipher: Cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv: ByteArray = content.take(12).toByteArray()
        val ciphertext: ByteArray = content.drop(12).toByteArray()

        val secretKeySpec = SecretKeySpec(masterKeyService.getMasterKey(), "AES")
        val gcmSpec = GCMParameterSpec(128, iv)

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmSpec)
        val plaintext: ByteArray = cipher.doFinal(ciphertext)

        iv.fill(0) //Wipe internal array
        ciphertext.fill(0) //Wipe internal array

        return plaintext
    }

}
