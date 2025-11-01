package de.christian2003.passwordvault.application.security


/**
 * Interface provides a service used for encryption and decryption using HMAC.
 */
interface CipherService {

    /**
     * Encrypts the content passed using the specified seed for an HMAC.
     *
     * @param content       Plain text to encrypt.
     * @param hmacSeed      Seed for the HMAC to generate. The HMAC is used as key to encrypt the
     *                      plain text.
     * @return              Cipher text.
     * @throws Exception    Cannot decrypt content.
     */
    fun encrypt(content: ByteArray, hmacSeed: ByteArray): ByteArray

    /**
     * Decrypts the passed cypher text using the specified seed for an HMAC.
     *
     * @param content       Cipher text to decrypt.
     * @param hmacSeed      Seed for the HMAC to generate. The HMAC is used as key to decrypt the
     *                      cipher text.
     * @return              Plain text.
     * @throws Exception    Cannot decrypt content.
     */
    fun decrypt(content: ByteArray, hmacSeed: ByteArray): ByteArray

}
