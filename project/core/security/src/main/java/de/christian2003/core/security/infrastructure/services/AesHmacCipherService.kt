package de.christian2003.core.security.infrastructure.services

import de.christian2003.core.security.domain.exceptions.CryptographicException
import de.christian2003.core.security.domain.repositories.UnlockedMasterKeyRepository
import de.christian2003.core.security.domain.services.HmacCipherService
import java.io.EOFException
import java.io.InputStream
import java.io.OutputStream
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.Mac
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject


/**
 * Implements the cipher service for the AES algorithm.
 *
 * @param unlockedMasterKeyRepository   Repository to access the unlocked master key.
 */
internal class AesHmacCipherService @Inject constructor(
    private val unlockedMasterKeyRepository: UnlockedMasterKeyRepository
): HmacCipherService {

    /**
     * Encrypts the content passed using the specified seed for an HMAC.
     *
     * @param content       Plain text to encrypt.
     * @param hmacSeed      Seed for the HMAC to generate. The HMAC is used as key to encrypt the
     *                      plain text.
     * @return              Cipher text.
     * @throws Exception    Cannot encrypt content.
     */
    override suspend fun encrypt(content: ByteArray, hmacSeed: ByteArray): ByteArray {
        val cipher: Cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val hmac: ByteArray = deriveHmac(hmacSeed)
        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)

        val secretKeySpec = SecretKeySpec(hmac, "AES")
        val gcmSpec = GCMParameterSpec(128, iv)

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmSpec)
        val ciphertext: ByteArray = cipher.doFinal(content)

        return iv + ciphertext
    }


    /**
     * Decrypts the passed cypher text using the specified seed for an HMAC.
     *
     * @param content       Cipher text to decrypt.
     * @param hmacSeed      Seed for the HMAC to generate. The HMAC is used as key to decrypt the
     *                      cipher text.
     * @return              Plain text.
     * @throws Exception    Cannot decrypt content.
     */
    override suspend fun decrypt(content: ByteArray, hmacSeed: ByteArray): ByteArray {
        val cipher: Cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val hmac: ByteArray = deriveHmac(hmacSeed)
        val iv: ByteArray = content.take(12).toByteArray()
        val ciphertext: ByteArray = content.drop(12).toByteArray()

        val secretKeySpec = SecretKeySpec(hmac, "AES")
        val gcmSpec = GCMParameterSpec(128, iv)

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmSpec)
        val plaintext: ByteArray = cipher.doFinal(ciphertext)

        return plaintext
    }


    /**
     * Encrypts the provided stream using the specified seed for an HMAC.
     *
     * @param output        Output stream to encrypt.
     * @param hmacSeed      Seed for the HMAC to generate. The HMAC is used as key to encrypt the
     *                      stream content.
     * @return              Encrypted output stream.
     * @throws Exception    Cannot encrypt the stream.
     */
    override fun encryptStream(output: OutputStream, hmacSeed: ByteArray): OutputStream {
        val cipher: Cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val hmac: ByteArray = deriveHmac(hmacSeed)
        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)

        val secretKeySpec = SecretKeySpec(hmac, "AES")
        val gcmSpec = GCMParameterSpec(128, iv)

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmSpec)
        output.write(iv)

        return CipherOutputStream(output, cipher)
    }


    /**
     * Decrypts the provided stream using the specified seed for an HMAC.
     *
     * @param input         Input stream to decrypt.
     * @param hmacSeed      Seed for the HMAC to generate. The HMAC is used as key to decrypt the
     *                      stream content.
     * @return              Decrypted input stream.
     * @throws Exception    Cannot decrypt the stream.
     */
    override fun decryptStream(input: InputStream, hmacSeed: ByteArray): InputStream {
        val cipher: Cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val hmac: ByteArray = deriveHmac(hmacSeed)
        val iv = readFullIvFromStream(input)

        val secretKeySpec = SecretKeySpec(hmac, "AES")
        val gcmSpec = GCMParameterSpec(128, iv)

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmSpec)

        return CipherInputStream(input, cipher)
    }


    /**
     * Method derives the HMAC from the specified bytes (seed).
     *
     * @param bytes         Seed used to generate HMAC.
     * @return              Generated HMAC.
     * @throws Exception    Cannot generate HMAC.
     */
    private fun deriveHmac(bytes: ByteArray): ByteArray {
        val masterKeyBytes: ByteArray? = unlockedMasterKeyRepository.getUnlockedMasterKeyBytes()
        if (masterKeyBytes == null) {
            throw CryptographicException("Master key is not unlocked")
        }
        val keySpec = SecretKeySpec(masterKeyBytes, "HmacSHA512")

        val mac = Mac.getInstance("HmacSHA512")
        mac.init(keySpec)
        val fullHmacOutput = mac.doFinal(bytes)

        //Use only the first 32 bytes of the key, since AES does not support 64 byte keys:
        return fullHmacOutput.copyOf(32)
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
