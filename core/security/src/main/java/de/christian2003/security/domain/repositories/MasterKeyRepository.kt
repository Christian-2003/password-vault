package de.christian2003.security.domain.repositories


/**
 * Repository through which to access the encrypted master key.
 */
interface MasterKeyRepository {

    /**
     * Returns the encrypted master key or null if no master key is available.
     *
     * @return  Bytes of the encrypted master key or null.
     */
    fun getEncryptedMasterKey(): ByteArray?


    /**
     * Sets the encrypted master key.
     *
     * @param encryptedMasterKey    Bytes of the encrypted master key.
     */
    fun setEncryptedMasterKey(encryptedMasterKey: ByteArray)


    /**
     * Tests whether an encrypted master key exists.
     *
     * @return  Whether an encrypted master key exists.
     */
    fun hasEncryptedMasterKey(): Boolean

}
