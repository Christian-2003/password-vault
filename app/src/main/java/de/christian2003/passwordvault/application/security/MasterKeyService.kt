package de.christian2003.passwordvault.application.security


/**
 * Service through which to access the master key.
 */
interface MasterKeyService {

    /**
     * Gets the master secret key instance to use for encryption and decryption.
     *
     * @return  Secret master key.
     */
    fun getMasterKey(): ByteArray


    /**
     * Clears the master key from memory. This needs to be called before the app is closed and once
     * the key is no longer needed. It is irreversible and cannot be undone.
     */
    fun clearMasterKey()

}
