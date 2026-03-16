package de.christian2003.core.security.domain.services

import java.io.InputStream
import java.io.OutputStream


/**
 * Interface provides a service used for encryption and decryption using HMAC.
 */
interface HmacCipherService {

    /**
     * Encrypts the content passed using the specified seed for an HMAC.
     *
     * @param content       Plain text to encrypt.
     * @param hmacSeed      Seed for the HMAC to generate. The HMAC is used as key to encrypt the
     *                      plain text.
     * @return              Cipher text.
     * @throws Exception    Cannot encrypt content.
     */
    suspend fun encrypt(content: ByteArray, hmacSeed: ByteArray): ByteArray


    /**
     * Decrypts the passed cypher text using the specified seed for an HMAC.
     *
     * @param content       Cipher text to decrypt.
     * @param hmacSeed      Seed for the HMAC to generate. The HMAC is used as key to decrypt the
     *                      cipher text.
     * @return              Plain text.
     * @throws Exception    Cannot decrypt content.
     */
    suspend fun decrypt(content: ByteArray, hmacSeed: ByteArray): ByteArray


    /**
     * Encrypts the provided stream using the specified seed for an HMAC.
     *
     * @param output        Output stream to encrypt.
     * @param hmacSeed      Seed for the HMAC to generate. The HMAC is used as key to encrypt the
     *                      stream content.
     * @return              Encrypted output stream.
     * @throws Exception    Cannot encrypt the stream.
     */
    fun encryptStream(output: OutputStream, hmacSeed: ByteArray): OutputStream


    /**
     * Decrypts the provided stream using the specified seed for an HMAC.
     *
     * @param input         Input stream to decrypt.
     * @param hmacSeed      Seed for the HMAC to generate. The HMAC is used as key to decrypt the
     *                      stream content.
     * @return              Decrypted input stream.
     * @throws Exception    Cannot decrypt the stream.
     */
    fun decryptStream(input: InputStream, hmacSeed: ByteArray): InputStream

}
