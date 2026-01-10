package de.christian2003.security.infrastructure.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import de.christian2003.security.domain.entities.KekEntry
import de.christian2003.security.domain.repositories.KekRepository
import de.christian2003.security.domain.repositories.MasterKeyRepository
import java.util.Base64
import javax.inject.Inject


/**
 * Repository implementation to access keys from SharedPreferences.
 *
 * @param context   Android context used to access SharedPreferences.
 */
class SharedPreferencesKeyRepository @Inject constructor(
    context: Context
): MasterKeyRepository, KekRepository {

    /**
     * Preferences used to access keys.
     */
    private val preferences: SharedPreferences = context.getSharedPreferences("security", Context.MODE_PRIVATE)


    /**
     * Returns the encrypted master key or null if no master key is setup.
     *
     * @return  Encrypted master key or null.
     */
    override fun getEncryptedMasterKey(): ByteArray? {
        val masterKeyAsString: String? = preferences.getString("master_key", null)
        if (masterKeyAsString != null) {
            val masterKeyAsByteArray: ByteArray = base64ToByteArray(masterKeyAsString)
            return masterKeyAsByteArray
        }
        return null
    }


    /**
     * Sets the encrypted master key for the first time. This method will only set the encrypted
     * master key if no master key is already set, in which case true is returned. If a master
     * key is already available, this does nothing and returns false.
     *
     * @param encryptedMasterKey    New encrypted master key.
     * @return                      Whether the master key was setup or not.
     */
    override fun setupEncryptedMasterKey(encryptedMasterKey: ByteArray): Boolean {
        if (!preferences.contains("master_key")) {
            val masterKeyAsString: String = byteArrayToBase64(encryptedMasterKey)
            preferences.edit {
                putString("master_key", masterKeyAsString)
            }
            return true
        }
        return false
    }


    /**
     * Tests whether the repository contains an encrypted master key.
     *
     * @return  Whether an encrypted master key is available.
     */
    override fun hasEncryptedMasterKey(): Boolean {
        val masterKeyAsString: String? = preferences.getString("master_key", null)
        return masterKeyAsString != null
    }


    /**
     * Gets the encrypted key encryption key (KEK) or returns null if no KEK is available.
     *
     * @param entry KEK entry to return.
     * @return      Encrypted KEK or null.
     */
    override fun getEncryptedKek(entry: KekEntry): ByteArray? {
        val key: String = when(entry) {
            KekEntry.MasterPassword -> "master_password_kek"
            KekEntry.RecoveryCodes -> "recovery_codes_kek"
        }

        val kekAsString: String? = preferences.getString(key, null)
        if (kekAsString != null) {
            val kekAsByteArray: ByteArray = base64ToByteArray(kekAsString)
            return kekAsByteArray
        }
        return null
    }


    /**
     * Changes the encrypted key encryption key (KEK).
     *
     * @param entry         KEK entry to set.
     * @param encryptedKek  New encrypted KEK.
     */
    override fun setEncryptedKek(entry: KekEntry, encryptedKek: ByteArray) {
        val key: String = when(entry) {
            KekEntry.MasterPassword -> "master_password_kek"
            KekEntry.RecoveryCodes -> "recovery_codes_kek"
        }

        val kekAsString: String = byteArrayToBase64(encryptedKek)
        preferences.edit {
            putString(key, kekAsString)
        }
    }


    /**
     * Returns whether the encrypted key encryption key (KEK) is available.
     *
     * @param entry KEK entry to test.
     * @return      Whether the KEK is available.
     */
    override fun hasEncryptedKek(entry: KekEntry): Boolean {
        val key: String = when(entry) {
            KekEntry.MasterPassword -> "master_password_kek"
            KekEntry.RecoveryCodes -> "recovery_codes_kek"
        }

        val kekAsString: String? = preferences.getString(key, null)
        return kekAsString != null
    }


    /**
     * Converts the specified byte array to a Base64-encoded string.
     *
     * @param bytes Byte array to convert to a Base64-encoded string.
     * @return      Base64-encoded string.
     */
    private fun byteArrayToBase64(bytes: ByteArray): String {
        return Base64.getEncoder().encodeToString(bytes)
    }


    /**
     * Converts the specified Base64-encoded string to a byte array.
     *
     * @param s Base64-encoded string to convert to a byte array.
     * @return  Converted byte array.
     */
    private fun base64ToByteArray(s: String): ByteArray {
        return Base64.getDecoder().decode(s)
    }

}
