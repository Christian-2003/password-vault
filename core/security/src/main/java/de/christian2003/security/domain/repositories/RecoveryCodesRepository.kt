package de.christian2003.security.domain.repositories


/**
 * Repository for the setup of the authentication can add an encrypted KEK for the a recovery code.
 */
interface RecoveryCodesRepository {

    /**
     * Sets the encrypted KEK for a recovery code.
     *
     * @param index             Index of the encrypted KEK.
     * @param encryptedKekBytes Bytes of the encrypted KEK.
     * @param salt              Salt used to encrypt the recovery code.
     */
    fun setEncryptedRecoveryKek(
        index: Int,
        encryptedKekBytes: ByteArray,
        salt: ByteArray
    )


    /**
     * Returns the encrypted recovery KEK for the specified index. If no KEK is available, null is
     * returned.
     *
     * @param index Index of the encrypted KEK to return.
     * @return      Encrypted KEK or null.
     */
    fun getEncryptedRecoveryKek(index: Int): ByteArray?


    /**
     * Returns the salt for the recovery code with the specified index. If no salt is available,
     * null is returned.
     *
     * @param index Index of the salt to return.
     * @return      Salt of the specified index or null.
     */
    fun getRecoverySalt(index: Int): ByteArray?

}
