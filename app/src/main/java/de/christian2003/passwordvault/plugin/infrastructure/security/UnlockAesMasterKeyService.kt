package de.christian2003.passwordvault.plugin.infrastructure.security

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.passwordvault.application.security.MasterKeyService
import de.christian2003.passwordvault.application.security.UnlockMasterKeyService
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.inject.Inject


class UnlockAesMasterKeyService @Inject constructor(
    @param:ApplicationContext private val context: Context
): UnlockMasterKeyService {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("security", Context.MODE_PRIVATE)

    private val PBKDF2_ITERATIONS: Int = 600_000
    private val PBKDF2_KEY_LENGTH: Int = 256


    /**
     * Unlocks the master key with the user's master password. If the master key cannot be unlocked,
     * null is returned.
     *
     * @param password  Master password to use for unlocking the master key.
     * @return          Service through which to retrieve the master key, or null.
     */
    override fun unlockWithPassword(password: CharArray): MasterKeyService? {
        val salt: String? = sharedPreferences.getString("pw_salt", null)
        val encryptedMasterKey: String? = sharedPreferences.getString("pw_key", null)
        if (salt != null && encryptedMasterKey != null) {
            val keyService: MasterKeyService = AesMasterKeyService(deriveKeyFromPassword(password, byteArrayOf()))
            val aesHelper = AesHelper(keyService)
            val encryptedMasterKeyAsBytes: ByteArray = encryptedMasterKey.toByteArray()
            val decryptedMasterKey: ByteArray = aesHelper.decrypt(encryptedMasterKeyAsBytes)

            encryptedMasterKeyAsBytes.fill(0)
            keyService.clearMasterKey()

            return AesMasterKeyService(decryptedMasterKey)
        }
        return null
    }


    /**
     * Unlocks the master key with the configured biometrics. If the master key cannot be unlocked,
     * null is returned.
     *
     * @return  Service through which to retrieve the master key, or null.
     */
    override fun unlockWithBiometrics(): MasterKeyService? {
        TODO("Not yet implemented")
    }


    /**
     * Unlocks the master key with the recovery codes. If the master key cannot be unlocked,
     * null is returned.
     *
     * @param recoveryCodes Recovery codes to use for unlocking the master key.
     * @return              Service through which to retrieve the master key, or null.
     */
    override fun unlockWithRecoveryCodes(recoveryCodes: CharArray): MasterKeyService? {
        TODO("Not yet implemented")
    }


    private fun deriveKeyFromPassword(password: CharArray, salt: ByteArray): ByteArray {
        val keySpec = PBEKeySpec(password, salt, PBKDF2_ITERATIONS, PBKDF2_KEY_LENGTH)
        val factory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2withHmacSHA512")
        try {
            val key: ByteArray = factory.generateSecret(keySpec).encoded
            return key
        }
        finally {
            keySpec.clearPassword()
        }
    }

}
