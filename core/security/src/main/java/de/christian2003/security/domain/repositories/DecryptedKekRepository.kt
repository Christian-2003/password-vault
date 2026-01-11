package de.christian2003.security.domain.repositories


/**
 * Repository for the authentication setup can manage the decrypted KEK that is required across
 * multiple setup steps.
 */
interface DecryptedKekRepository {

    /**
     * Returns the decrypted KEK or null, if no KEK is available.
     *
     * @return  Bytes of the decrypted KEK or null.
     */
    fun getDecryptedKek(): ByteArray?


    /**
     * Sets the decrypted KEK.
     *
     * @param decryptedKekBytes New bytes of the decrypted KEK.
     */
    fun setDecryptedKek(decryptedKekBytes: ByteArray)


    /**
     * Tests whether a decrypted KEK is available.
     *
     * @return  Whether a decrypted KEK is available.
     */
    fun hasDecryptedKek(): Boolean

}
