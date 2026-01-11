package de.christian2003.security.domain.repositories


/**
 * Repository for the setup of the authentication can set the master password.
 */
interface SetupMasterPasswordRepository {

    /**
     * Sets the encrypted KEK from the master password.
     *
     * @param encryptedKekBytes Bytes of the encrypted KEK.
     * @param salt              Salt used to derive the key used to encrypt the KEK.
     */
    fun setEncryptedMasterPasswordKek(
        encryptedKekBytes: ByteArray,
        salt: ByteArray
    )


    /**
     * Returns the current encrypted KEK for the master password. If no KEK exists, null is returned.
     *
     * @return  Bytes of the encrypted KEK or null.
     */
    fun getEncryptedMasterPasswordKek(): ByteArray?


    /**
     * Returns the current salt for the master password. If no salt exists, null is returned.
     *
     * @return  Salt of the master password or null.
     */
    fun getMasterPasswordSalt(): ByteArray?


    /**
     * Tests whether the encrypted KEK for the master password exists.
     *
     * @return  Whether the encrypted KEK for the master password exists.
     */
    fun hasEncryptedMasterPasswordKek(): Boolean


    /**
     * Tests whether the salt for the master password exists.
     *
     * @return  Whether the salt for the master password exists.
     */
    fun hasMasterPasswordSalt(): Boolean

}
