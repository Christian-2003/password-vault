package de.christian2003.passwordvault.plugin.infrastructure.security.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.biometric.BiometricManager
import de.christian2003.passwordvault.application.repository.AuthRepository
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.passwordvault.domain.security.auth.SecurityQuestion
import javax.inject.Inject


/**
 * Implementation of the AuthRepository that uses 'PBKDF2withHmacSHA512' and shared preferences.
 *
 * @param context   Android context.
 */
class SharedPreferencesAuthRepository @Inject constructor(
    @ApplicationContext private val context: Context
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
        if (newPassword.isBlank()) {
            return
        }
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
        if (password.isBlank()) {
            return false
        }
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
     * Returns whether security questions are configured for the app.
     *
     * @return  Whether security questions are configured.
     */
    override fun hasSecurityQuestions(): Boolean {
        val questions: List<SecurityQuestion> = getConfiguredQuestions()
        return questions.isNotEmpty()
    }


    /**
     * Adds the specified combination of security question and answer to the configured questions.
     *
     * @param question  Security question.
     * @param answer    Answer to the security question.
     */
    override fun addSecurityQuestion(question: SecurityQuestion, answer: String) {
        if (answer.isBlank()) {
            return
        }
        val salt: ByteArray = generateSalt()
        val hashedAnswer: String = hash(answer, salt)
        val saltAsString: String = byteArrayToString(salt)

        val questions: List<SecurityQuestion> = getConfiguredQuestions()
        var questionListAsString: String? = null
        if (!questions.contains(question)) {
            val questionListStringBuilder = StringBuilder()
            questions.forEach { existingQuestion ->
                questionListStringBuilder.append(existingQuestion.ordinal)
                questionListStringBuilder.append(',')
            }
            questionListStringBuilder.append(question.ordinal)
            questionListAsString = questionListStringBuilder.toString()
        }

        sharedPreferences.edit {
            putString("question_${question.ordinal}_hash", hashedAnswer)
            putString("question_${question.ordinal}_salt", saltAsString)
            if (questionListAsString != null) {
                putString("question_list", questionListAsString)
            }
        }
    }


    /**
     * Removes the specified security question from the configured questions.
     *
     * @param question  Question to remove.
     */
    override fun removeSecurityQuestion(question: SecurityQuestion) {
        val questions: List<SecurityQuestion> = getConfiguredQuestions()
        if (questions.contains(question)) {
            val questionListStringBuilder = StringBuilder()
            questions.forEach { existingQuestion ->
                if (existingQuestion != question) {
                    questionListStringBuilder.append(existingQuestion.ordinal)
                    questionListStringBuilder.append(',')
                }
            }
            val questionListAsString = questionListStringBuilder.removeSuffix(",").toString()

            sharedPreferences.edit {
                putString("question_list", questionListAsString)
                remove("question_${question.ordinal}_hash")
                remove("question_${question.ordinal}_salt")
            }
        }
    }


    /**
     * Returns a list of the configured security questions.
     *
     * @return  List of configured security questions.
     */
    override fun getConfiguredQuestions(): List<SecurityQuestion> {
        val questionsList: String? = sharedPreferences.getString("question_list", null)
        val questionOrdinalsAsString: List<String> = questionsList?.split(",") ?: listOf()
        val questionOrdinals: List<Int?> = questionOrdinalsAsString.map { it.toIntOrNull() }

        val questions: MutableList<SecurityQuestion> = mutableListOf()
        questionOrdinals.forEach { ordinal ->
            if (ordinal != null && ordinal >= 0 && ordinal < SecurityQuestion.entries.size) {
                questions.add(SecurityQuestion.entries[ordinal])
            }
        }

        return questions
    }


    /**
     * Validates the specified security questions. If the number of correct questions is equal to
     * (or exceeds) the passed threshold, the validation succeeds. Otherwise, it fails.
     *
     * @param questions Answered security questions.
     * @param threshold Number of questions that need to be answered correctly to succeed.
     * @return          Whether the answers to the security questions are valid.
     */
    override fun validateSecurityQuestions(questions: Map<SecurityQuestion, String>, threshold: Int): Boolean {
        var validAnswers = 0
        questions.forEach { question, answer ->
            if (answer.isNotBlank()) {
                val hash: String? = sharedPreferences.getString("question_${question.ordinal}_hash", null)
                val salt: String? = sharedPreferences.getString("question_${question.ordinal}_salt", null)

                if (hash != null && salt != null) {
                    val hashedAnswer: String = hash(answer, stringToByteArray(salt))
                    if (hash == hashedAnswer) {
                        validAnswers++
                    }
                }
            }
        }

        return validAnswers >= threshold
    }


    /**
     * Hashes the specified plain text with the specified salt.
     *
     * @param plain Plain text to hash.
     * @param salt  Salt to use for hashing.
     */
    private fun hash(plain: String, salt: ByteArray): String {
        val plainAsCharArray: CharArray = plain.toCharArray()
        val keySpec = PBEKeySpec(plainAsCharArray, salt, 65536, 256)
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
