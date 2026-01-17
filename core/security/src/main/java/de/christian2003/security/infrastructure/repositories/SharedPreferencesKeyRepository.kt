package de.christian2003.security.infrastructure.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.security.domain.repositories.CommitRepository
import de.christian2003.security.domain.repositories.DecryptedKekRepository
import de.christian2003.security.domain.repositories.MasterKeyRepository
import de.christian2003.security.domain.repositories.MasterPasswordRepository
import de.christian2003.security.domain.repositories.RecoveryCodesRepository
import de.christian2003.security.infrastructure.repositories.dto.SharedPreferencesSetupRepositoryKekEntryDto
import java.util.Base64
import javax.inject.Inject


/**
 * Repository implementation for the authentication setup. This repository adheres to transactional
 * principles, whereas changes are not committed to memory unless a commit is specifically called.
 * The repository uses SharedPreferences for storage.
 *
 * @param context   Android context.
 */
class SharedPreferencesKeyRepository @Inject constructor(
    @ApplicationContext context: Context
): MasterPasswordRepository, RecoveryCodesRepository, DecryptedKekRepository, MasterKeyRepository, CommitRepository {

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
            val kekAsString: String? = preferences.getString("master_password_kek", null)
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
            val saltAsString: String? = preferences.getString("master_password_salt", null)
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
        return masterPasswordKek != null || preferences.contains("master_password_kek")
    }


    /**
     * Tests whether the salt for the master password exists.
     *
     * @return  Whether the salt for the master password exists.
     */
    override fun hasMasterPasswordSalt(): Boolean {
        return masterPasswordKek != null || preferences.contains("master_password_salt")
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
            val masterKeyAsString: String? = preferences.getString("master_key", null)
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
        return encryptedMasterKey != null || preferences.contains("master_key")
    }


    /**
     * Commits all changes that were done during the setup of the authentication.
     */
    override fun commitAllChanges() {
        preferences.edit {
            //Commit master password KEK:
            val masterPasswordKek: SharedPreferencesSetupRepositoryKekEntryDto? = this@SharedPreferencesKeyRepository.masterPasswordKek
            if (masterPasswordKek != null) {
                putString("master_password_kek", bytesToString(masterPasswordKek.encryptedKekBytes))
                putString("master_password_salt", bytesToString(masterPasswordKek.salt))
            }
            this@SharedPreferencesKeyRepository.masterPasswordKek = null

            //Commit changes to recovery:
            recoveryKeks.forEach { index, recoveryCodeKek ->
                putString("recovery_${index}_kek", bytesToString(recoveryCodeKek.encryptedKekBytes))
                putString("recovery_${index}_salt", bytesToString(recoveryCodeKek.salt))
            }
            recoveryKeks.clear()

            //Commit encrypted master key:
            val encryptedMasterKey: ByteArray? = this@SharedPreferencesKeyRepository.encryptedMasterKey
            if (encryptedMasterKey != null) {
                putString("master_key", bytesToString(encryptedMasterKey))
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
        return masterPasswordKek != null || recoveryKeks.isNotEmpty() || encryptedMasterKey != null
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
