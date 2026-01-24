package de.christian2003.security.infrastructure.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.biometric.BiometricManager
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.security.domain.entities.SecurityAliases
import de.christian2003.security.domain.repositories.BiometricsRepository
import de.christian2003.security.domain.repositories.CommitRepository
import de.christian2003.security.domain.repositories.DecryptedKekRepository
import de.christian2003.security.domain.repositories.MasterKeyRepository
import de.christian2003.security.domain.repositories.MasterPasswordRepository
import de.christian2003.security.domain.repositories.RecoveryCodesRepository
import de.christian2003.security.infrastructure.repositories.dto.SharedPreferencesSetupRepositoryKekEntryDto
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository implementation for the authentication setup. This repository adheres to transactional
 * principles, whereas changes are not committed to memory unless a commit is specifically called.
 * The repository uses SharedPreferences for storage.
 *
 * @param context   Android context.
 */
@Singleton
class SharedPreferencesKeyRepository @Inject constructor(
    @ApplicationContext private val context: Context
): MasterPasswordRepository, RecoveryCodesRepository, DecryptedKekRepository, MasterKeyRepository, BiometricsRepository, CommitRepository {

    /**
     * Shared preferences used for storing the data.
     */
    private val preferences: SharedPreferences = context.getSharedPreferences("security", Context.MODE_PRIVATE)

    /**
     * KEK for the master password that was set. This is not stored in permanent memory until data
     * is committed.
     */
    private var masterPasswordKek: SharedPreferencesSetupRepositoryKekEntryDto? = null

    /**
     * KEKs for the recovery codes are mapped to their indices that were set. This is not stored in
     * permanent memory until data is committed.
     */
    private val recoveryKeks: MutableMap<Int, SharedPreferencesSetupRepositoryKekEntryDto> = mutableMapOf()

    /**
     * KEK for the biometrics that was set. This is not stored in permanent memory until data is
     * committed.
     */
    private var biometricsKek: ByteArray? = null

    /**
     * Encrypted master key.
     */
    private var encryptedMasterKey: ByteArray? = null

    /**
     * Decrypted KEK that is required across multiple setup steps.
     */
    private var decryptedKek: ByteArray? = null


    /**
     * Sets the encrypted KEK from the master password.
     *
     * @param encryptedKekBytes Bytes of the encrypted KEK.
     * @param salt              Salt used to derive the key used to encrypt the KEK.
     */
    override fun setEncryptedMasterPasswordKek(
        encryptedKekBytes: ByteArray,
        salt: ByteArray
    ) {
        masterPasswordKek = SharedPreferencesSetupRepositoryKekEntryDto(
            encryptedKekBytes = encryptedKekBytes,
            salt = salt
        )
    }


    /**
     * Returns the current encrypted KEK for the master password. If no KEK exists, null is returned.
     *
     * @return  Bytes of the encrypted KEK or null.
     */
    override fun getEncryptedMasterPasswordKek(): ByteArray? {
        val masterPasswordKek: SharedPreferencesSetupRepositoryKekEntryDto? = this.masterPasswordKek
        if (masterPasswordKek != null) {
            return masterPasswordKek.encryptedKekBytes
        }
        else {
            val kekAsString: String? = preferences.getString(SecurityAliases.MasterPasswordKek.getAlias(), null)
            if (kekAsString != null) {
                val kekAsBytes: ByteArray = stringToBytes(kekAsString)
                return kekAsBytes
            }
        }
        return null
    }


    /**
     * Returns the current salt for the master password. If no salt exists, null is returned.
     *
     * @return  Salt of the master password or null.
     */
    override fun getMasterPasswordSalt(): ByteArray? {
        val masterPasswordKek: SharedPreferencesSetupRepositoryKekEntryDto? = this.masterPasswordKek
        if (masterPasswordKek != null) {
            return masterPasswordKek.salt
        }
        else {
            val saltAsString: String? = preferences.getString(SecurityAliases.MasterPasswordSalt.getAlias(), null)
            if (saltAsString != null) {
                val saltAsBytes: ByteArray = stringToBytes(saltAsString)
                return saltAsBytes
            }
        }
        return null
    }


    /**
     * Tests whether the encrypted KEK for the master password exists.
     *
     * @return  Whether the encrypted KEK for the master password exists.
     */
    override fun hasEncryptedMasterPasswordKek(): Boolean {
        return masterPasswordKek != null || preferences.contains(SecurityAliases.MasterPasswordKek.getAlias())
    }


    /**
     * Tests whether the salt for the master password exists.
     *
     * @return  Whether the salt for the master password exists.
     */
    override fun hasMasterPasswordSalt(): Boolean {
        return masterPasswordKek != null || preferences.contains(SecurityAliases.MasterPasswordSalt.getAlias())
    }


    /**
     * Sets the encrypted KEK for a recovery code.
     *
     * @param index             Index of the encrypted KEK.
     * @param encryptedKekBytes Bytes of the encrypted KEK.
     * @param salt              Salt used to encrypt the recovery code.
     */
    override fun setEncryptedRecoveryKek(
        index: Int,
        encryptedKekBytes: ByteArray,
        salt: ByteArray
    ) {
        recoveryKeks.put(
            key = index,
            value = SharedPreferencesSetupRepositoryKekEntryDto(
                encryptedKekBytes = encryptedKekBytes,
                salt = salt
            )
        )
    }


    /**
     * Returns the encrypted recovery KEK for the specified index. If no KEK is available, null is
     * returned.
     *
     * @param index Index of the encrypted KEK to return.
     * @return      Encrypted KEK or null.
     */
    override fun getEncryptedRecoveryKek(index: Int): ByteArray? {
        if (recoveryKeks.contains(index)) {
            return recoveryKeks[index]?.encryptedKekBytes
        }
        else {
            val alias: String = SecurityAliases.RecoveryCodeKek.getAlias(index)
            val kekAsString: String? = preferences.getString(alias, null)
            if (kekAsString != null) {
                val kekAsBytes: ByteArray = stringToBytes(kekAsString)
                return kekAsBytes
            }
        }
        return null
    }


    /**
     * Returns the salt for the recovery code with the specified index. If no salt is available,
     * null is returned.
     *
     * @param index Index of the salt to return.
     * @return      Salt of the specified index or null.
     */
    override fun getRecoverySalt(index: Int): ByteArray? {
        if (recoveryKeks.contains(index)) {
            return recoveryKeks[index]?.salt
        }
        else {
            val alias: String = SecurityAliases.RecoveryCodeSalt.getAlias(index)
            val saltAsString: String? = preferences.getString(alias, null)
            if (saltAsString != null) {
                val saltAsBytes: ByteArray = stringToBytes(saltAsString)
                return saltAsBytes
            }
        }
        return null
    }


    /**
     * Returns the decrypted KEK or null, if no KEK is available.
     *
     * @return  Bytes of the decrypted KEK or null.
     */
    override fun getDecryptedKek(): ByteArray? {
        return decryptedKek
    }


    /**
     * Sets the decrypted KEK.
     *
     * @param decryptedKekBytes New bytes of the decrypted KEK.
     */
    override fun setDecryptedKek(decryptedKekBytes: ByteArray) {
        decryptedKek = decryptedKekBytes
    }


    /**
     * Tests whether a decrypted KEK is available.
     *
     * @return  Whether a decrypted KEK is available.
     */
    override fun hasDecryptedKek(): Boolean {
        return decryptedKek != null
    }


    /**
     * Returns the encrypted master key or null if no master key is available.
     *
     * @return  Bytes of the encrypted master key or null.
     */
    override fun getEncryptedMasterKey(): ByteArray? {
        if (encryptedMasterKey != null) {
            return encryptedMasterKey
        }
        else {
            val masterKeyAsString: String? = preferences.getString(SecurityAliases.MasterKey.getAlias(), null)
            if (masterKeyAsString != null) {
                val masterKeyAsBytes: ByteArray = stringToBytes(masterKeyAsString)
                return masterKeyAsBytes
            }
            return null
        }
    }


    /**
     * Sets the encrypted master key.
     *
     * @param encryptedMasterKey    Bytes of the encrypted master key.
     */
    override fun setEncryptedMasterKey(encryptedMasterKey: ByteArray) {
        this.encryptedMasterKey = encryptedMasterKey
    }


    /**
     * Tests whether an encrypted master key exists.
     *
     * @return  Whether an encrypted master key exists.
     */
    override fun hasEncryptedMasterKey(): Boolean {
        return encryptedMasterKey != null || preferences.contains(SecurityAliases.MasterKey.getAlias())
    }


    /**
     * Sets the encrypted KEK from the biometrics.
     *
     * @param encryptedKekBytes Bytes of the encrypted KEK.
     */
    override fun setEncryptedBiometricsKek(encryptedKekBytes: ByteArray) {
        biometricsKek = encryptedKekBytes
    }


    /**
     * Returns the current encrypted KEK for the biometrics. If no KEK exists, null is returned.
     *
     * @return  Bytes of the encrypted KEK or null.
     */
    override fun getEncryptedBiometricsKek(): ByteArray? {
        if (biometricsKek != null) {
            return biometricsKek
        }
        else {
            val biometricsKekAsString: String? = preferences.getString(SecurityAliases.BiometricsKek.getAlias(), null)
            if (biometricsKekAsString != null) {
                val biometricsKekAsBytes: ByteArray = stringToBytes(biometricsKekAsString)
                return biometricsKekAsBytes
            }
            return null
        }
    }


    /**
     * Tests whether the encrypted KEK for the biometrics exists.
     *
     * @return  Whether the encrypted KEK for the biometrics exists.
     */
    override fun hasEncryptedBiometricsKek(): Boolean {
        return biometricsKek != null || preferences.contains(SecurityAliases.BiometricsKek.getAlias())
    }


    /**
     * Returns whether biometrics are available on the device.
     *
     * @return  Whether biometrics are available.
     */
    override fun areBiometricsAvailable(): Boolean {
        val biometricManager: BiometricManager = BiometricManager.from(context)
        val canAuthenticate: Int = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )

        return canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS
    }


    /**
     * Commits all changes that were done during the setup of the authentication.
     */
    override fun commitAllChanges() {
        preferences.edit {
            //Commit master password KEK:
            val masterPasswordKek: SharedPreferencesSetupRepositoryKekEntryDto? = this@SharedPreferencesKeyRepository.masterPasswordKek
            if (masterPasswordKek != null) {
                putString(SecurityAliases.MasterPasswordKek.getAlias(), bytesToString(masterPasswordKek.encryptedKekBytes))
                putString(SecurityAliases.MasterPasswordSalt.getAlias(), bytesToString(masterPasswordKek.salt))
            }
            this@SharedPreferencesKeyRepository.masterPasswordKek = null

            //Commit changes to recovery:
            recoveryKeks.forEach { index, recoveryCodeKek ->
                putString(SecurityAliases.RecoveryCodeKek.getAlias(index), bytesToString(recoveryCodeKek.encryptedKekBytes))
                putString(SecurityAliases.RecoveryCodeSalt.getAlias(index), bytesToString(recoveryCodeKek.salt))
            }
            recoveryKeks.clear()

            //Commit changes to biometrics:
            val biometricsKek: ByteArray? = this@SharedPreferencesKeyRepository.biometricsKek
            if (biometricsKek != null) {
                putString(SecurityAliases.BiometricsKek.getAlias(), bytesToString(biometricsKek))
            }
            this@SharedPreferencesKeyRepository.biometricsKek = null

            //Commit encrypted master key:
            val encryptedMasterKey: ByteArray? = this@SharedPreferencesKeyRepository.encryptedMasterKey
            if (encryptedMasterKey != null) {
                putString(SecurityAliases.MasterKey.getAlias(), bytesToString(encryptedMasterKey))
                encryptedMasterKey.fill(0)
            }
            this@SharedPreferencesKeyRepository.encryptedMasterKey = null
        }
    }


    /**
     * Tests whether changes are staged that can be committed.
     *
     * @return  Whether changes are staged and waiting for commit.
     */
    override fun areChangesStaged(): Boolean {
        return masterPasswordKek != null || recoveryKeks.isNotEmpty() || biometricsKek != null || encryptedMasterKey != null
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

}
