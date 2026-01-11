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

}
