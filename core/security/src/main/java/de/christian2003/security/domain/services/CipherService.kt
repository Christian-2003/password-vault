package de.christian2003.security.domain.services

import javax.crypto.SecretKey
import de.christian2003.security.domain.exceptions.InvalidKeyException


/**
 * Cipher service can encrypt and decrypt data using an encryption algorithm.
 */
interface CipherService {

    /**
     * Encrypts the specified plain text using the provided key. The resulting cipher text is returned
     * afterwards.
     *
     * @param plain Byte array to encrypt.
     * @param key   Key to use for encryption.
     */
    fun encrypt(plain: ByteArray, key: SecretKey): ByteArray


    /**
     * Decrypts the specified cipher text using the provided key. The resulting plain text is returned
     * afterwards.
     *
     * @param cipher                Cipher text to decrypt.
     * @param key                   Key to use for decryption.
     * @throws InvalidKeyException  If the cipher is in AEAD mode (such as AES-GCM) and the
     *                              authentication tag does not match, this exception is thrown.
     */
    fun decrypt(cipher: ByteArray, key: SecretKey): ByteArray


    /**
     * Encrypts the specified plain text using the provided key. The resulting cipher text is returned
     * afterwards.
     *
     * @param plain     Byte array to encrypt.
     * @param keyBytes  Bytes of the key to use for encryption.
     */
    fun encrypt(plain: ByteArray, keyBytes: ByteArray): ByteArray


    /**
     * Decrypts the specified cipher text using the provided key. The resulting plain text is returned
     * afterwards.
     *
     * @param cipher                Cipher text to decrypt.
     * @param keyBytes              Bytes of the key to use for decryption.
     * @throws InvalidKeyException  If the cipher is in AEAD mode (such as AES-GCM) and the
     *                              authentication tag does not match, this exception is thrown.
     */
    fun decrypt(cipher: ByteArray, keyBytes: ByteArray): ByteArray

}
