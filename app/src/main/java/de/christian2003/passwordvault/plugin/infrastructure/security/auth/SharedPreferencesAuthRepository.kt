package de.christian2003.passwordvault.plugin.infrastructure.security.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.biometric.BiometricManager
import de.christian2003.passwordvault.application.repository.AuthRepository
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import androidx.core.content.edit


/**
 * Implementation of the AuthRepository that uses 'PBKDF2withHmacSHA512' and shared preferences.
 *
 * @param context   Android context.
 */
class SharedPreferencesAuthRepository(
    private val context: Context
): AuthRepository {

    /**
     * Shared preferences used for storing and retrieving data.
     */
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("security", Context.MODE_PRIVATE)


    /**
     * Returns whether a password is set by the user.
     *
     * @return  Whether a password is set by the user.
     */
    override fun hasPassword(): Boolean {
        val password: String? = sharedPreferences.getString("password_hash", null)
        val salt: String? = sharedPreferences.getString("password_salt", null)
        return password != null && salt != null
    }

    /**
     * Changes the password.
     *
     * @param newPassword   New password.
     * @return              Whether the password was changed successfully.
     */
    override fun setPassword(newPassword: String) {
        val salt: ByteArray = generateSalt()
        val hashedPassword: String = hash(newPassword, salt)
        val saltAsString: String = byteArrayToString(salt)
        sharedPreferences.edit {
            putString("password_hash", hashedPassword)
            putString("password_salt", saltAsString)
        }
    }

    /**
     * Tests whether the specified password is valid.
     *
     * @param password  Password to test.
     * @return          Whether the password is valid.
     */
    override fun isPasswordValid(password: String): Boolean {
        val storedHash: String? = sharedPreferences.getString("password_hash", null)
        val storedSalt: String? = sharedPreferences.getString("password_salt", null)
        if (storedHash != null && storedSalt != null) {
            val saltAsByteArray: ByteArray = stringToByteArray(storedSalt)
            val computedHash: String = hash(password, saltAsByteArray)
            return storedHash == computedHash
        }
        return false
    }


    /**
     * Returns whether the device supports biometric authentication.
     *
     * @return  Whether the device supports biometric authentication.
     */
    override fun doesDeviceSupportBiometrics(): Boolean {
        val biometricManager: BiometricManager = BiometricManager.from(context)
        val canAuthenticate: Int = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )

        return canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS
    }


    /**
     * Returns whether the app should use biometrics for authentication.
     *
     * @return  Whether to use biometrics for authentication.
     */
    override fun hasBiometrics(): Boolean {
        val biometrics: Boolean = sharedPreferences.getBoolean("use_biometrics", false)
        return biometrics
    }


    /**
     * Changes whether the app should use biometrics for authentication.
     *
     * @param biometrics    Whether to use biometrics for authentication.
     */
    override fun setBiometrics(biometrics: Boolean) {
        sharedPreferences.edit {
            putBoolean("use_biometrics", biometrics)
        }
    }


    /**
     * Hashes the specified password with the specified salt.
     *
     * @param password  Plain text password to hash.
     * @param salt      Salt to use for hashing.
     */
    private fun hash(password: String, salt: ByteArray): String {
        val passwordAsCharArray: CharArray = password.toCharArray()
        val keySpec = PBEKeySpec(passwordAsCharArray, salt, 65536, 256)
        val factory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2withHmacSHA512")
        val hash: ByteArray = factory.generateSecret(keySpec).encoded
        return byteArrayToString(hash)
    }


    /**
     * Generates a new salt.
     *
     * @return  Salt.
     */
    private fun generateSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(32)
        random.nextBytes(salt)
        return salt
    }


    /**
     * Converts the specified string to a byte array.
     *
     * @param s String to convert to a byte array.
     * @return  Converted byte array.
     */
    private fun stringToByteArray(s: String): ByteArray {
        return Base64.getDecoder().decode(s)
    }


    /**
     * Converts the specified byte array to a string.
     *
     * @param array Byte array to convert to a string.
     * @return      Converted string.
     */
    private fun byteArrayToString(array: ByteArray): String {
        return Base64.getEncoder().encodeToString(array)
    }

}
