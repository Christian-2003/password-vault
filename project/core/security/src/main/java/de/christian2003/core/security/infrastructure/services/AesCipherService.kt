package de.christian2003.core.security.infrastructure.services

import de.christian2003.core.security.domain.services.CipherService
import java.io.EOFException
import java.io.InputStream
import java.io.OutputStream
import java.security.InvalidKeyException
import java.security.SecureRandom
import javax.crypto.AEADBadTagException
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject


/**
 * Implementation of the cipher service using Advanced Encryption Standard (AES) with
 * Galois / Counter Mode (GCM). This implementation verifies authentication tags during decryption.
 */
internal class AesCipherService @Inject constructor(): CipherService {

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
            throw InvalidKeyException(e.message ?: "Tags do not match")
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
        val trimmedKeyBytes: ByteArray = keyBytes.take(32).toByteArray()
        val aesCipher: Cipher = Cipher.getInstance("AES/GCM/NoPadding")

        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)

        val secretKeySpec = SecretKeySpec(trimmedKeyBytes, "AES")
        val gcmSpec = GCMParameterSpec(128, iv)

        aesCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmSpec)
        val cipher: ByteArray = aesCipher.doFinal(plain)

        val final: ByteArray = iv + cipher

        trimmedKeyBytes.fill(0) // Wipe internal array
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
        val trimmedKeyBytes: ByteArray = keyBytes.take(32).toByteArray()
        val aesCipher: Cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv: ByteArray = cipher.take(12).toByteArray()
        val cipherWithoutIv: ByteArray = cipher.drop(12).toByteArray()

        val secretKeySpec = SecretKeySpec(trimmedKeyBytes, "AES")
        val gcmSpec = GCMParameterSpec(128, iv)

        aesCipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmSpec)
        val plain: ByteArray = try {
            aesCipher.doFinal(cipherWithoutIv)
        } catch (e: AEADBadTagException) {
            throw InvalidKeyException(e.message ?: "Tags do not match")
        }

        trimmedKeyBytes.fill(0) // Wipe internal array
        iv.fill(0) //Wipe internal array
        cipherWithoutIv.fill(0) //Wipe internal array

        return plain
    }


    /**
     * Encrypts the provided stream using the specified secret key.
     *
     * @param output        Output stream to encrypt.
     * @param keyBytes      Key bytes used to encrypt the stream.
     * @return              Encrypted output stream.
     * @throws Exception    Cannot encrypt the stream.
     */
    override fun encryptStream(output: OutputStream, keyBytes: ByteArray): OutputStream {
        val trimmedKeyBytes: ByteArray = keyBytes.take(32).toByteArray()
        val cipher: Cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)

        val secretKeySpec = SecretKeySpec(trimmedKeyBytes, "AES")
        val gcmSpec = GCMParameterSpec(128, iv)

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmSpec)
        output.write(iv)

        return CipherOutputStream(output, cipher)
    }


    /**
     * Decrypts the provided stream using the specified secret key.
     *
     * @param input         Input stream to decrypt.
     * @param keyBytes      Key bytes used to encrypt the stream.
     * @return              Decrypted input stream.
     * @throws Exception    Cannot decrypt the stream.
     */
    override fun decryptStream(input: InputStream, keyBytes: ByteArray): InputStream {
        val trimmedKeyBytes: ByteArray = keyBytes.take(32).toByteArray()
        val cipher: Cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = readFullIvFromStream(input)

        val secretKeySpec = SecretKeySpec(trimmedKeyBytes, "AES")
        val gcmSpec = GCMParameterSpec(128, iv)

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmSpec)

        return CipherInputStream(input, cipher)
    }


    /**
     * Reads the full IV from the provided input stream. If the input stream does not contain enough
     * bytes, an EOF exception is thrown.
     *
     * @param input         Input stream from which to read the IV.
     * @return              IV read from the input stream
     * @throws EOFException The input stream does not contain enough bytes for the IV.
     */
    private fun readFullIvFromStream(input: InputStream): ByteArray {
        val iv = ByteArray(12)
        var offset = 0

        while (offset < iv.size) {
            val bytesRead: Int = input.read(iv, offset, iv.size - offset)
            if (bytesRead == -1) {
                throw EOFException("Unexpected end of stream")
            }
            offset += bytesRead
        }

        return iv
    }

}
