package de.christian2003.security.domain.repositories

import de.christian2003.security.domain.exceptions.AuthTransactionException


/**
 * Transaction based repository to set the authentication data.
 */
internal interface AuthTransactionRepository {

    /**
     * Begins a new transaction.
     * This must be called before any data is passed to the repository.
     */
    fun beginTransaction()


    /**
     * Commits a transaction.
     * This must be called after passing the data that should be committed to permanent storage.
     *
     * @throws AuthTransactionException Cannot commit transaction.
     */
    fun commitTransaction()


    /**
     * Sets the encrypted KEK for the master password, as well as the salt used for KDF.
     *
     * @param masterPasswordKekBytes    Bytes of the encrypted KEK.
     * @param masterPasswordSalt        Salt used for KDF.
     * @throws AuthTransactionException Cannot set the master password.
     */
    fun setMasterPassword(
        masterPasswordKekBytes: ByteArray,
        masterPasswordSalt: ByteArray
    )


    /**
     * Adds the encrypted KEK for the recovery code with the specified index, as well as the salt
     * used for KDF.
     *
     * @param recoveryCodeKekBytes      Bytes of the encrypted KEK.
     * @param recoveryCodeSalt          Salt used for KDF.
     * @throws AuthTransactionException Cannot add the recovery code.
     */
    fun addRecoveryCode(
        recoveryCodeKekBytes: ByteArray,
        recoveryCodeSalt: ByteArray
    )


    /**
     * Sets the encrypted KEK for the biometrics.
     *
     * @param biometricsKekBytes    Bytes of the encrypted KEK.
     */
    fun setBiometricsKek(
        biometricsKekBytes: ByteArray
    )


    /**
     * Deletes the encrypted KEK for the biometrics, if one is available.
     */
    fun deleteBiometricsKek()


    /**
     * Sets the encrypted master key.
     *
     * @param masterKeyBytes            Bytes of the encrypted master key.
     * @throws AuthTransactionException Cannot set the master key.
     */
    fun setMasterKey(
        masterKeyBytes: ByteArray
    )

}
