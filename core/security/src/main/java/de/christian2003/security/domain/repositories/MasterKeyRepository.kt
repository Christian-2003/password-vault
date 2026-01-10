package de.christian2003.security.domain.repositories


/**
 * Repository through which to access the encrypted master key.
 */
interface MasterKeyRepository {

    /**
     * Returns the encrypted master key or null if no master key is setup.
     *
     * @return  Encrypted master key or null.
     */
    fun getEncryptedMasterKey(): ByteArray?


    /**
     * Sets the encrypted master key for the first time. This method will only set the encrypted
     * master key if no master key is already set, in which case true is returned. If a master
     * key is already available, this does nothing and returns false.
     *
     * @param encryptedMasterKey    New encrypted master key.
     * @return                      Whether the master key was setup or not.
     */
    fun setupEncryptedMasterKey(encryptedMasterKey: ByteArray): Boolean


    /**
     * Tests whether the repository contains an encrypted master key.
     *
     * @return  Whether an encrypted master key is available.
     */
    fun hasEncryptedMasterKey(): Boolean

}
