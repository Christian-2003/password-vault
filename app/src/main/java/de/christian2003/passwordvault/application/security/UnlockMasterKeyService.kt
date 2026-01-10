package de.christian2003.passwordvault.application.security


/**
 * Service through which to unlock the master key.
 */
interface UnlockMasterKeyService {

    /**
     * Unlocks the master key with the user's master password. If the master key cannot be unlocked,
     * null is returned.
     *
     * @param password  Master password to use for unlocking the master key.
     * @return          Service through which to retrieve the master key, or null.
     */
    fun unlockWithPassword(password: CharArray): MasterKeyService?


    /**
     * Unlocks the master key with the configured biometrics. If the master key cannot be unlocked,
     * null is returned.
     *
     * @return  Service through which to retrieve the master key, or null.
     */
    fun unlockWithBiometrics(): MasterKeyService?


    /**
     * Unlocks the master key with the recovery codes. If the master key cannot be unlocked,
     * null is returned.
     *
     * @param recoveryCodes Recovery codes to use for unlocking the master key.
     * @return              Service through which to retrieve the master key, or null.
     */
    fun unlockWithRecoveryCodes(recoveryCodes: CharArray): MasterKeyService?

}
