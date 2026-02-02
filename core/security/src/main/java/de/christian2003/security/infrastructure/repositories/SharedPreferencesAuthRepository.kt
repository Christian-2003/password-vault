package de.christian2003.security.infrastructure.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.biometric.BiometricManager
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.security.domain.entities.SecurityAliases
import de.christian2003.security.domain.exceptions.AuthTransactionException
import de.christian2003.security.domain.repositories.AuthTransactionRepository
import de.christian2003.security.domain.repositories.ReadonlyAuthRepository
import de.christian2003.security.infrastructure.repositories.dto.AuthRepositoryKekItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton


/**
 * SharedPreferences-based authentication repository implements both the transaction-based repository
 * for modifications as well as the read-only repository for accessing data.
 *
 * @param context   Application context.
 */
@Singleton
internal class SharedPreferencesAuthRepository @Inject constructor(
    @param:ApplicationContext private val context: Context
): AuthTransactionRepository, ReadonlyAuthRepository {

    /**
     * Preferences used to permanently store data.
     */
    private val preferences: SharedPreferences = context.getSharedPreferences("security", Context.MODE_PRIVATE)

    /**
     * Indicates whether a transaction has been started.
     */
    private var transactionStarted: Boolean = false

    /**
     * Indicates whether a transaction has been committed.
     */
    private var transactionCommited: Boolean = false

    /**
     * Stores the master password KEK waiting for the commit.
     */
    private var masterPassword: AuthRepositoryKekItem? = null

    /**
     * Stores the recovery codes KEKs waiting for the commit.
     */
    private var recoveryCodes: MutableList<AuthRepositoryKekItem>? = null

    /**
     * Stores the biometrics KEK waiting for the commit.
     */
    private var biometricsKekBytes: ByteArray? = null

    /**
     * Stores the master key waiting for the commit.
     */
    private var masterKeyBytes: ByteArray? = null

    /**
     * Flag indicates whether to remove the biometrics KEK during a commit.
     */
    private var deleteBiometricsKek: Boolean = false


    /**
     * Begins a new transaction.
     * This must be called before any data is passed to the repository.
     */
    override fun beginTransaction() {
        masterPassword = null
        recoveryCodes = null
        biometricsKekBytes = null
        masterKeyBytes = null
        deleteBiometricsKek = false
        transactionStarted = true
        transactionCommited = false
    }


    /**
     * Commits a transaction.
     * This must be called after passing the data that should be committed to permanent storage.
     *
     * @throws AuthTransactionException Cannot commit transaction.
     */
    override fun commitTransaction() {
        if (!transactionStarted) {
            throw AuthTransactionException("Transaction cannot be committed, because it was not started")
        }

        val now: LocalDateTime = LocalDateTime.now()

        preferences.edit {
            //Commit master password:
            val masterPassword: AuthRepositoryKekItem? = this@SharedPreferencesAuthRepository.masterPassword
            if (masterPassword != null) {
                putString(SecurityAliases.MasterPasswordKek.getAlias(), bytesToString(masterPassword.keyBytes))
                putString(SecurityAliases.MasterPasswordSalt.getAlias(), bytesToString(masterPassword.salt))
                putLong(SecurityAliases.MasterPasswordTime.getAlias(), localDateTimeToEpochSecond(now))
            }

            //Commit recovery codes:
            val recoveryCodes: List<AuthRepositoryKekItem>? = this@SharedPreferencesAuthRepository.recoveryCodes?.toList()
            if (recoveryCodes != null) {
                val currentNumberOfRecoveryCodes: Int = preferences.getInt(SecurityAliases.NumberOfRecoveryCodes.getAlias(), 0)
                for (i: Int in 0 until currentNumberOfRecoveryCodes) {
                    remove(SecurityAliases.RecoveryCodeKek.getAlias(i))
                    remove(SecurityAliases.RecoveryCodeSalt.getAlias(i))
                }

                recoveryCodes.forEachIndexed { index, recoveryCodeItem ->
                    putString(SecurityAliases.RecoveryCodeKek.getAlias(index), bytesToString(recoveryCodeItem.keyBytes))
                    putString(SecurityAliases.RecoveryCodeSalt.getAlias(index), bytesToString(recoveryCodeItem.salt))
                }

                putInt(SecurityAliases.NumberOfRecoveryCodes.getAlias(), recoveryCodes.size)
                putLong(SecurityAliases.RecoveryCodesTime.getAlias(), localDateTimeToEpochSecond(now))
            }

            //Commit biometrics:
            val biometricsKekBytes: ByteArray? = this@SharedPreferencesAuthRepository.biometricsKekBytes
            if (biometricsKekBytes != null) {
                putString(SecurityAliases.BiometricsKek.getAlias(), bytesToString(biometricsKekBytes))
                putLong(SecurityAliases.BiometricsTime.getAlias(), localDateTimeToEpochSecond(now))
            } else if (deleteBiometricsKek) {
                remove(SecurityAliases.BiometricsKek.getAlias())
                remove(SecurityAliases.BiometricsTime.getAlias())
            }

            //Commit master key:
            val masterKeyBytes: ByteArray? = this@SharedPreferencesAuthRepository.masterKeyBytes
            if (masterKeyBytes != null) {
                putString(SecurityAliases.MasterKey.getAlias(), bytesToString(masterKeyBytes))
            }
        }

        //Finish commit:
        masterPassword?.keyBytes?.fill(0)
        masterPassword?.salt?.fill(0)
        masterPassword = null

        recoveryCodes?.forEach { item ->
            item.keyBytes.fill(0)
            item.salt.fill(0)
        }
        recoveryCodes = null

        biometricsKekBytes?.fill(0)
        biometricsKekBytes = null
        deleteBiometricsKek = false

        masterKeyBytes?.fill(0)
        masterKeyBytes = null

        transactionCommited = true
        transactionStarted = false
    }


    /**
     * Sets the encrypted KEK for the master password, as well as the salt used for KDF.
     *
     * @param masterPasswordKekBytes    Bytes of the encrypted KEK.
     * @param masterPasswordSalt        Salt used for KDF.
     * @throws AuthTransactionException Cannot set the master password.
     */
    override fun setMasterPassword(
        masterPasswordKekBytes: ByteArray,
        masterPasswordSalt: ByteArray
    ) {
        if (!transactionStarted) {
            throw AuthTransactionException("Cannot set master password because transaction has not started")
        }
        masterPassword = AuthRepositoryKekItem(
            keyBytes = masterPasswordKekBytes,
            salt = masterPasswordSalt
        )
    }


    /**
     * Adds the encrypted KEK for the recovery code with the specified index, as well as the salt
     * used for KDF.
     *
     * @param recoveryCodeKekBytes      Bytes of the encrypted KEK.
     * @param recoveryCodeSalt          Salt used for KDF.
     * @throws AuthTransactionException Cannot add the recovery code.
     */
    override fun addRecoveryCode(
        recoveryCodeKekBytes: ByteArray,
        recoveryCodeSalt: ByteArray
    ) {
        if (!transactionStarted) {
            throw AuthTransactionException("Cannot add recovery code because transaction has not started")
        }

        if (recoveryCodes == null) {
            recoveryCodes = mutableListOf()
        }
        val item = AuthRepositoryKekItem(
            keyBytes = recoveryCodeKekBytes,
            salt = recoveryCodeSalt
        )
        recoveryCodes!!.add(item)
    }


    /**
     * Sets the encrypted KEK for the biometrics.
     *
     * @param biometricsKekBytes    Bytes of the encrypted KEK.
     */
    override fun setBiometricsKek(
        biometricsKekBytes: ByteArray
    ) {
        if (!transactionStarted) {
            throw AuthTransactionException("Cannot set biometrics because transaction has not started")
        }
        this.biometricsKekBytes = biometricsKekBytes
    }


    /**
     * Deletes the encrypted KEK for the biometrics, if one is available.
     */
    override fun deleteBiometricsKek() {
        if (!transactionStarted) {
            throw AuthTransactionException("Cannot delete biometrics KEK because transaction has not started")
        }
        deleteBiometricsKek = true
    }


    /**
     * Sets the encrypted master key.
     *
     * @param masterKeyBytes            Bytes of the encrypted master key.
     * @throws AuthTransactionException Cannot set the master key.
     */
    override fun setMasterKey(
        masterKeyBytes: ByteArray
    ) {
        if (!transactionStarted) {
            throw AuthTransactionException("Cannot set master key because transaction has not started")
        }
        this.masterKeyBytes = masterKeyBytes
    }


    /**
     * Returns the encrypted KEK of the master password or null if no KEK is available.
     *
     * @return  Bytes of the encrypted KEK or null.
     */
    override fun getMasterPasswordKek(): ByteArray? {
        val kekAsString: String? = preferences.getString(SecurityAliases.MasterPasswordKek.getAlias(), null)
        if (kekAsString != null) {
            val kekAsBytes = stringToBytes(kekAsString)
            return kekAsBytes
        }
        return null
    }


    /**
     * Returns the salt used for KDF of the master password or null if no salt is available.
     *
     * @return  Bytes of the salt or null.
     */
    override fun getMasterPasswordSalt(): ByteArray? {
        val saltAsString: String? = preferences.getString(SecurityAliases.MasterPasswordSalt.getAlias(), null)
        if (saltAsString != null) {
            val saltAsBytes = stringToBytes(saltAsString)
            return saltAsBytes
        }
        return null
    }


    /**
     * Returns whether a master password is configured.
     *
     * @return  Whether a master password is configured.
     */
    override fun isMasterPasswordConfigured(): Boolean {
        return preferences.contains(SecurityAliases.MasterPasswordKek.getAlias())
                && preferences.contains(SecurityAliases.MasterPasswordSalt.getAlias())
    }


    /**
     * Returns the timestamp at which the master password was edited the last time or null if
     * unknown.
     *
     * @return  Timestamp at which the master password was edited the last time or null.
     */
    override fun getMasterPasswordTimestamp(): Flow<LocalDateTime?> = callbackFlow {
        //Emit current value immediately:
        trySend(getMasterPasswordTimestampFromPreferences(preferences))

        //Register listener for updates:
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            if (key == SecurityAliases.MasterPasswordTime.getAlias()) {
                trySend(getMasterPasswordTimestampFromPreferences(prefs))
            }
        }
        preferences.registerOnSharedPreferenceChangeListener(listener)

        //Cleanup when flow is gone:
        awaitClose {
            preferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }.distinctUntilChanged()


    /**
     * Returns the timestamp at which the master password was edited the last time or null if
     * unknown.
     *
     * @param prefs Preferences from which to load the timestamp.
     * @return      Timestamp at which the master password was edited the last time or null.
     */
    private fun getMasterPasswordTimestampFromPreferences(prefs: SharedPreferences): LocalDateTime? {
        val timestampAsEpochSecond: Long = prefs.getLong(SecurityAliases.MasterPasswordTime.getAlias(), -1)
        if (timestampAsEpochSecond >= 0) {
            val timestamp: LocalDateTime = epochSecondToLocalDateTime(timestampAsEpochSecond)
            return timestamp
        }
        return null
    }


    /**
     * Returns the encrypted KEK of the recovery code with the specified index or null if no KEK is
     * available.
     *
     * @param index Index of the KEK to return.
     * @return      Bytes of the encrypted KEK or null.
     */
    override fun getRecoveryCodeKek(index: Int): ByteArray? {
        val kekAsString: String? = preferences.getString(SecurityAliases.RecoveryCodeKek.getAlias(index), null)
        if (kekAsString != null) {
            val kekAsBytes = stringToBytes(kekAsString)
            return kekAsBytes
        }
        return null
    }


    /**
     * Returns the salt used for KDF of the recovery code with the specified index or null if no
     * salt is available.
     *
     * @param index Index of the salt to return.
     * @return      bytes of the salt or null.
     */
    override fun getRecoveryCodeSalt(index: Int): ByteArray? {
        val saltAsString: String? = preferences.getString(SecurityAliases.RecoveryCodeSalt.getAlias(index), null)
        if (saltAsString != null) {
            val saltAsBytes = stringToBytes(saltAsString)
            return saltAsBytes
        }
        return null
    }


    /**
     * Returns the number of recovery codes that are configured.
     *
     * @return  Number of configured recovery codes.
     */
    override fun getNumberOfRecoveryCodes(): Int {
        return preferences.getInt(SecurityAliases.NumberOfRecoveryCodes.getAlias(), 0)
    }


    /**
     * Returns whether recovery codes are configured or not.
     *
     * @return  Whether recovery codes are configured.
     */
    override fun areRecoveryCodesConfigured(): Boolean {
        val numberOfRecoveryCodes: Int = getNumberOfRecoveryCodes()
        if (numberOfRecoveryCodes == 0) {
            return false
        }

        for (i: Int in 0 until numberOfRecoveryCodes) {
            if (!preferences.contains(SecurityAliases.RecoveryCodeKek.getAlias(i)) || preferences.contains(SecurityAliases.RecoveryCodeSalt.getAlias(i))) {
                return false
            }
        }

        return true
    }


    /**
     * Returns the timestamp at which the recovery codes were edited the last time or null if
     * unknown.
     *
     * @return  Timestamp at which the recovery codes were edited the last time or null.
     */
    override fun getRecoveryCodesTimestamp(): Flow<LocalDateTime?> = callbackFlow {
        //Emit current value immediately:
        trySend(getRecoveryCodesTimestampFromPreferences(preferences))

        //Register listener for updates:
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            if (key == SecurityAliases.RecoveryCodesTime.getAlias()) {
                trySend(getRecoveryCodesTimestampFromPreferences(prefs))
            }
        }
        preferences.registerOnSharedPreferenceChangeListener(listener)

        //Cleanup when flow is gone:
        awaitClose {
            preferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }.distinctUntilChanged()


    /**
     * Returns the timestamp at which the recovery codes were edited the last time or null if
     * unknown.
     *
     * @param prefs Preferences from which to load the timestamp.
     * @return      Timestamp at which the recovery codes were edited the last time or null.
     */
    fun getRecoveryCodesTimestampFromPreferences(prefs: SharedPreferences): LocalDateTime? {
        val timestampAsEpochSecond: Long = prefs.getLong(SecurityAliases.RecoveryCodesTime.getAlias(), -1)
        if (timestampAsEpochSecond >= 0) {
            val timestamp: LocalDateTime = epochSecondToLocalDateTime(timestampAsEpochSecond)
            return timestamp
        }
        return null
    }


    /**
     * Returns the encrypted KEK for the biometrics or null if no KEK is available.
     *
     * @return  Bytes of the encrypted KEK or null.
     */
    override fun getBiometricsKek(): ByteArray? {
        val kekAsString: String? = preferences.getString(SecurityAliases.BiometricsKek.getAlias(), null)
        if (kekAsString != null) {
            val kekAsBytes = stringToBytes(kekAsString)
            return kekAsBytes
        }
        return null
    }


    /**
     * Returns whether biometrics are configured or not.
     *
     * @return  Whether biometrics are configured.
     */
    override fun isBiometricsConfigured(): Boolean {
        return isBiometricsConfiguredWithPreferences(preferences)
    }


    /**
     * Returns whether biometrics are configured or not.
     *
     * @return  Whether biometrics are configured.
     */
    override fun isBiometricsConfiguredAsFlow(): Flow<Boolean> = callbackFlow {
        //Emit current value immediately:
        trySend(isBiometricsConfiguredWithPreferences(preferences))

        //Register listener for updates:
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            if (key == SecurityAliases.BiometricsTime.getAlias()) {
                trySend(isBiometricsConfiguredWithPreferences(prefs))
            }
        }
        preferences.registerOnSharedPreferenceChangeListener(listener)

        //Cleanup when flow is gone:
        awaitClose {
            preferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }.distinctUntilChanged()


    /**
     * Returns whether biometrics are configured or not.
     *
     * @return  Whether biometrics are configured.
     */
    private fun isBiometricsConfiguredWithPreferences(prefs: SharedPreferences): Boolean {
        return prefs.contains(SecurityAliases.BiometricsKek.getAlias())
    }


    /**
     * Returns whether biometrics are available on the device.
     *
     * @return  Whether biometrics are available.
     */
    override fun isBiometricsAvailable(): Boolean {
        val biometricManager: BiometricManager = BiometricManager.from(context)
        val canAuthenticate: Int = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )

        return canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS
    }


    /**
     * Returns the timestamp at which the biometrics were edited the last time or null if
     * unknown.
     *
     * @return  Timestamp at which the biometrics were edited the last time or null.
     */
    override fun getBiometricsTimestamp(): Flow<LocalDateTime?> = callbackFlow {
        //Emit current value immediately:
        trySend(getBiometricsTimestampFromPreferences(preferences))

        //Register listener for updates:
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            if (key == SecurityAliases.BiometricsTime.getAlias()) {
                trySend(getBiometricsTimestampFromPreferences(prefs))
            }
        }
        preferences.registerOnSharedPreferenceChangeListener(listener)

        //Cleanup when flow is gone:
        awaitClose {
            preferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }.distinctUntilChanged()


    /**
     * Returns the timestamp at which the biometrics were edited the last time or null if
     * unknown.
     *
     * @param prefs Preferences from which to load the timestamp.
     * @return      Timestamp at which the biometrics were edited the last time or null.
     */
    private fun getBiometricsTimestampFromPreferences(prefs: SharedPreferences): LocalDateTime? {
        val timestampAsEpochSecond: Long = prefs.getLong(SecurityAliases.BiometricsTime.getAlias(), -1)
        if (timestampAsEpochSecond >= 0) {
            val timestamp: LocalDateTime = epochSecondToLocalDateTime(timestampAsEpochSecond)
            return timestamp
        }
        return null
    }


    /**
     * Returns the bytes of the encrypted master key. If no master key has been saved, null is
     * returned.
     *
     * @return  Bytes of the encrypted master key or null.
     */
    override fun getEncryptedMasterKey(): ByteArray? {
        val masterKeyAsString: String? = preferences.getString(SecurityAliases.MasterKey.getAlias(), null)
        if (masterKeyAsString != null) {
            val masterKeyAsBytes: ByteArray? = stringToBytes(masterKeyAsString)
            return masterKeyAsBytes
        }
        return null
    }


    /**
     * Converts the specified byte array to a Base64-encoded string.
     *
     * @param bytes Byte array to convert to a Base64-encoded string.
     * @return      Base64-encoded string.
     */
    private fun bytesToString(bytes: ByteArray): String {
        return Base64.getEncoder().encodeToString(bytes)
    }


    /**
     * Converts the specified Base64-encoded string to a byte array.
     *
     * @param s Base64-encoded string to convert to a byte array.
     * @return  Converted byte array.
     */
    private fun stringToBytes(s: String): ByteArray {
        return Base64.getDecoder().decode(s)
    }


    /**
     * Converts the specified local date time into epoch seconds.
     *
     * @param dateTime  Date time to convert to epoch seconds.
     * @return          Epoch seconds.
     */
    private fun localDateTimeToEpochSecond(dateTime: LocalDateTime): Long {
        return dateTime.toEpochSecond(ZoneOffset.UTC)
    }


    /**
     * Converts the specified epoch seconds to a local date time.
     *
     * @param epochSecond   Epoch seconds to convert to local date time.
     * @return              Local date time.
     */
    private fun epochSecondToLocalDateTime(epochSecond: Long): LocalDateTime {
        return LocalDateTime.ofEpochSecond(epochSecond, 0, ZoneOffset.UTC)
    }

}
