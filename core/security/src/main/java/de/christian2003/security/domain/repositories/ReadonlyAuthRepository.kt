package de.christian2003.security.domain.repositories

import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime


/**
 * Read-only repository provides access to read authentication data.
 */
internal interface ReadonlyAuthRepository {

    /**
     * Returns the encrypted KEK of the master password or null if no KEK is available.
     *
     * @return  Bytes of the encrypted KEK or null.
     */
    fun getMasterPasswordKek(): ByteArray?

    /**
     * Returns the salt used for KDF of the master password or null if no salt is available.
     *
     * @return  Bytes of the salt or null.
     */
    fun getMasterPasswordSalt(): ByteArray?

    /**
     * Returns whether a master password is configured.
     *
     * @return  Whether a master password is configured.
     */
    fun isMasterPasswordConfigured(): Boolean

    /**
     * Returns the timestamp at which the master password was edited the last time or null if
     * unknown.
     *
     * @return  Timestamp at which the master password was edited the last time or null.
     */
    fun getMasterPasswordTimestamp(): Flow<LocalDateTime?>


    /**
     * Returns the encrypted KEK of the recovery code with the specified index or null if no KEK is
     * available.
     *
     * @param index Index of the KEK to return.
     * @return      Bytes of the encrypted KEK or null.
     */
    fun getRecoveryCodeKek(index: Int): ByteArray?

    /**
     * Returns the salt used for KDF of the recovery code with the specified index or null if no
     * salt is available.
     *
     * @param index Index of the salt to return.
     * @return      bytes of the salt or null.
     */
    fun getRecoveryCodeSalt(index: Int): ByteArray?

    /**
     * Returns the number of recovery codes that are configured.
     *
     * @return  Number of configured recovery codes.
     */
    fun getNumberOfRecoveryCodes(): Int

    /**
     * Returns whether recovery codes are configured or not.
     *
     * @return  Whether recovery codes are configured.
     */
    fun areRecoveryCodesConfigured(): Boolean

    /**
     * Returns the timestamp at which the recovery codes were edited the last time or null if
     * unknown.
     *
     * @return  Timestamp at which the recovery codes were edited the last time or null.
     */
    fun getRecoveryCodesTimestamp(): Flow<LocalDateTime?>


    /**
     * Returns the encrypted KEK for the biometrics or null if no KEK is available.
     *
     * @return  Bytes of the encrypted KEK or null.
     */
    fun getBiometricsKek(): ByteArray?

    /**
     * Returns whether biometrics are configured or not.
     *
     * @return  Whether biometrics are configured.
     */
    fun isBiometricsConfigured(): Boolean

    /**
     * Returns whether biometrics are configured or not.
     *
     * @return  Whether biometrics are configured.
     */
    fun isBiometricsConfiguredAsFlow(): Flow<Boolean>

    /**
     * Returns whether biometrics are available on the device.
     *
     * @return  Whether biometrics are available.
     */
    fun isBiometricsAvailable(): Boolean

    /**
     * Returns the timestamp at which the biometrics were edited the last time or null if
     * unknown.
     *
     * @return  Timestamp at which the biometrics were edited the last time or null.
     */
    fun getBiometricsTimestamp(): Flow<LocalDateTime?>


    /**
     * Returns the bytes of the encrypted master key. If no master key has been saved, null is
     * returned.
     *
     * @return  Bytes of the encrypted master key or null.
     */
    fun getEncryptedMasterKey(): ByteArray?

}
