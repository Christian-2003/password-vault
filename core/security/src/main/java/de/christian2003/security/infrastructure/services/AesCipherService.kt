package de.christian2003.security.infrastructure.services

import de.christian2003.security.domain.services.CipherService
import java.security.InvalidKeyException
import java.security.SecureRandom
import javax.crypto.AEADBadTagException
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject


/**
 * Implementation of the cipher service using Advanced Encryption Standard (AES) with
 * Galois / Counter Mode (GCM). This implementation verifies authentication tags during decryption.
 */
class AesCipherService @Inject constructor(): CipherService {

    /**
     * Encrypts the specified plain text using the provided key. The resulting cipher text is returned
     * afterwards.
     *
     * @param plain Byte array to encrypt.
     * @param key   Key to use for encryption.
     */
    override suspend fun encrypt(plain: ByteArray, key: SecretKey): ByteArray {
        val aesCipher: Cipher = Cipher.getInstance("AES/GCM/NoPadding")

        aesCipher.init(Cipher.ENCRYPT_MODE, key, SecureRandom())
        val cipher: ByteArray = aesCipher.doFinal(plain)

        val iv: ByteArray = aesCipher.iv

        val final: ByteArray = iv + cipher
        iv.fill(0) //Wipe internal array
        cipher.fill(0) //Wipe internal array

        return final
    }


    /**
     * Decrypts the specified cipher text using the provided key. The resulting plain text is returned
     * afterwards.
     *
     * @param cipher                Cipher text to decrypt.
     * @param key                   Key to use for decryption.
     * @throws InvalidKeyException  If the cipher is in AEAD mode (such as AES-GCM) and the
     *                              authentication tag does not match, this exception is thrown.
     */
    override suspend fun decrypt(cipher: ByteArray, key: SecretKey): ByteArray {
        val aesCipher: Cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv: ByteArray = cipher.take(12).toByteArray()
        val cipherWithoutIv: ByteArray = cipher.drop(12).toByteArray()

        val gcmSpec = GCMParameterSpec(128, iv)

        aesCipher.init(Cipher.DECRYPT_MODE, key, gcmSpec)
        val plain: ByteArray = try {
            aesCipher.doFinal(cipherWithoutIv)
        } catch (e: AEADBadTagException) {
            throw InvalidKeyException(e.message ?: "")
        }

        iv.fill(0) //Wipe internal array
        cipherWithoutIv.fill(0) //Wipe internal array

        return plain
    }


    /**
     * Encrypts the specified plain text using the provided key. The resulting cipher text is returned
     * afterwards.
     *
     * @param plain     Byte array to encrypt.
     * @param keyBytes  Bytes of the key to use for encryption.
     */
    override suspend fun encrypt(plain: ByteArray, keyBytes: ByteArray): ByteArray {
        val aesCipher: Cipher = Cipher.getInstance("AES/GCM/NoPadding")

        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)

        val secretKeySpec = SecretKeySpec(keyBytes, "AES")
        val gcmSpec = GCMParameterSpec(128, iv)

        aesCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmSpec)
        val cipher: ByteArray = aesCipher.doFinal(plain)

        val final: ByteArray = iv + cipher
        iv.fill(0) //Wipe internal array
        cipher.fill(0) //Wipe internal array

        return final
    }


    /**
     * Decrypts the specified cipher text using the provided key. The resulting plain text is returned
     * afterwards.
     *
     * @param cipher                Cipher text to decrypt.
     * @param keyBytes              Bytes of the key to use for decryption.
     * @throws InvalidKeyException  If the cipher is in AEAD mode (such as AES-GCM) and the
     *                              authentication tag does not match, this exception is thrown.
     */
    override suspend fun decrypt(cipher: ByteArray, keyBytes: ByteArray): ByteArray {
        val aesCipher: Cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv: ByteArray = cipher.take(12).toByteArray()
        val cipherWithoutIv: ByteArray = cipher.drop(12).toByteArray()

        val secretKeySpec = SecretKeySpec(keyBytes, "AES")
        val gcmSpec = GCMParameterSpec(128, iv)

        aesCipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmSpec)
        val plain: ByteArray = try {
            aesCipher.doFinal(cipherWithoutIv)
        } catch (e: AEADBadTagException) {
            throw InvalidKeyException(e.message ?: "")
        }

        iv.fill(0) //Wipe internal array
        cipherWithoutIv.fill(0) //Wipe internal array

        return plain
    }

}
