package de.christian2003.passwordvault.plugin.infrastructure.security

import de.christian2003.passwordvault.application.security.MasterKeyService


/**
 * Implementation for the master key service, which handles the master key for all AES operations.
 *
 * @param bytes                     Bytes of the master key. This cannot be null and must have exactly
 *                                  32 bytes (i.e. 256 bits).
 * @throws IllegalArgumentException Thrown if the passed bytes for the master key is null or has
 *                                  incorrect size.
 */
class AesMasterKeyService(
    private var bytes: ByteArray?
): MasterKeyService {

    /**
     * Initializes the master key service.
     */
    init {
        require(bytes != null) { "Master key bytes cannot be null" }
        require(bytes!!.size == 32) { "Master key has incorrect size" }
    }


    /**
     * Gets the master secret key instance to use for encryption and decryption.
     *
     * @return  Secret master key.
     */
    override fun getMasterKey(): ByteArray {
        return bytes!!
    }


    /**
     * Clears the master key from memory. This needs to be called before the app is closed and once
     * the key is no longer needed. It is irreversible and cannot be undone.
     */
    override fun clearMasterKey() {
        bytes!!.fill(0)
        bytes = null
    }

}
