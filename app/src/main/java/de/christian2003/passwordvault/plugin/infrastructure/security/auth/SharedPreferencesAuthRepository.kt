package de.christian2003.passwordvault.plugin.infrastructure.security.auth

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.biometric.BiometricManager
import de.christian2003.passwordvault.application.repository.AuthRepository
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.passwordvault.domain.security.auth.SecurityQuestion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.security.KeyStore
import java.util.Base64
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.inject.Inject


/**
 * Implementation of the AuthRepository that uses 'PBKDF2withHmacSHA512' and shared preferences.
 *
 * @param context   Android context.
 */
class SharedPreferencesAuthRepository @Inject constructor(
    @param:ApplicationContext private val context: Context
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
    override suspend fun setPassword(newPassword: CharArray) {
        if (newPassword.isEmpty()) {
            return
        }
        val salt: ByteArray = generateSalt()
        val hashedPassword: CharArray = hash(newPassword, salt)
        sharedPreferences.edit {
            putString("password_hash", byteArrayToBase64(charArrayToByteArray(hashedPassword)))
            putString("password_salt", byteArrayToBase64(salt))
        }

        salt.fill(0)
        hashedPassword.fill('\u0000')
    }

    /**
     * Tests whether the specified password is valid.
     *
     * @param password  Password to test.
     * @return          Whether the password is valid.
     */
    override suspend fun isPasswordValid(password: CharArray): Boolean {
        if (password.isEmpty()) {
            return false
        }
        val storedHash: String? = sharedPreferences.getString("password_hash", null)
        val storedSalt: String? = sharedPreferences.getString("password_salt", null)

        val passwordValid: Boolean = if (storedHash != null && storedSalt != null) {
            val saltAsByteArray: ByteArray = base64ToByteArray(storedSalt)
            val computedHash: CharArray = hash(password, saltAsByteArray)
            val valid: Boolean = contentEqualsHash(computedHash, storedHash)

            saltAsByteArray.fill(0)
            computedHash.fill('\u0000')

            valid
        } else {
            false
        }

        return passwordValid
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
        val questionList: String? = sharedPreferences.getString("question_list", null)
        return questionList != null && questionList.isNotBlank()
    }


    /**
     * Adds the specified combination of security question and answer to the configured questions.
     *
     * @param question  Security question.
     * @param answer    Answer to the security question.
     */
    override suspend fun addSecurityQuestion(question: SecurityQuestion, answer: CharArray) {
        if (answer.isEmpty()) {
            return
        }
        val salt: ByteArray = generateSalt()
        val hashedAnswer: CharArray = hash(answer, salt)

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
            putString("question_${question.ordinal}_hash", byteArrayToBase64(charArrayToByteArray(hashedAnswer)))
            putString("question_${question.ordinal}_salt", byteArrayToBase64(salt))
            if (questionListAsString != null) {
                putString("question_list", questionListAsString)
            }
        }

        salt.fill(0)
        hashedAnswer.fill('\u0000')
    }


    /**
     * Removes the specified security question from the configured questions.
     *
     * @param question  Question to remove.
     */
    override fun removeSecurityQuestion(question: SecurityQuestion) {
        val questionsList: String? = sharedPreferences.getString("question_list", null)
        val questionOrdinalsAsString: List<String> = questionsList?.split(",") ?: listOf()
        val questionOrdinals: List<Int?> = questionOrdinalsAsString.map { it.toIntOrNull() }

        if (questionOrdinals.contains(question.ordinal)) {
            val questionListStringBuilder = StringBuilder()
            questionOrdinals.forEach { existingQuestionOrdinal ->
                if (existingQuestionOrdinal != question.ordinal) {
                    questionListStringBuilder.append(existingQuestionOrdinal)
                    questionListStringBuilder.append(',')
                }
            }
            val questionListAsString: String = questionListStringBuilder.removeSuffix(",").toString()

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
    override suspend fun getConfiguredQuestions(): List<SecurityQuestion> {
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
     * Returns the number of security questions that are configured.
     *
     * @return  Number of security questions that are configured.
     */
    override fun getSecurityQuestionsCount(): Int {
        val questionsList: String? = sharedPreferences.getString("question_list", null)
        val questionOrdinalsAsString: List<String> = questionsList?.split(",") ?: listOf()
        val questionOrdinals: List<Int?> = questionOrdinalsAsString.map { it.toIntOrNull() }
        return questionOrdinals.size
    }


    /**
     * Validates the specified security questions. If the number of correct questions is equal to
     * (or exceeds) the passed threshold, the validation succeeds. Otherwise, it fails.
     *
     * @param questions Answered security questions.
     * @param threshold Number of questions that need to be answered correctly to succeed.
     * @return          Whether the answers to the security questions are valid.
     */
    override suspend fun validateSecurityQuestions(questions: Map<SecurityQuestion, CharArray>, threshold: Int): Boolean = coroutineScope {
        val deferredResults = questions.map { (question, answer) ->
            async(Dispatchers.Default) {
                if (answer.isNotEmpty()) {
                    val hash: String? = sharedPreferences.getString("question_${question.ordinal}_hash", null)
                    val salt: String? = sharedPreferences.getString("question_${question.ordinal}_salt", null)

                    if (hash != null && salt != null) {
                        val hashedAnswer: CharArray = hash(answer, base64ToByteArray(salt))
                        val valid: Boolean = contentEqualsHash(hashedAnswer, hash)

                        hashedAnswer.fill('\u0000')
                        return@async valid
                    }
                }
                return@async false
            }
        }

        val validAnswers: Int = deferredResults.count { it.await() }

        return@coroutineScope validAnswers >= threshold
    }


    /**
     * Hashes the specified plain text with the specified salt. The specified plain text is wiped
     * afterwards.
     *
     * @param plain Plain text to hash.
     * @param salt  Salt to use for hashing.
     * @return      Hashed result.
     */
    private suspend fun hash(plain: CharArray, salt: ByteArray): CharArray = coroutineScope {
        val keySpec = PBEKeySpec(plain, salt, 600_000, 256)
        val factory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2withHmacSHA512")
        val hash: ByteArray = factory.generateSecret(keySpec).encoded
        val final: ByteArray = hmacWithPepper(hash)
        hash.fill(0)
        return@coroutineScope byteArrayToCharArray(final)
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
     * Performs an HMAC operation on some plain text using a hardware-backed secret key.
     *
     * @param plain Plain text.
     * @return      Cipher text.
     */
    private fun hmacWithPepper(plain: ByteArray): ByteArray {
        val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }
        val alias = "AuthPepper"

        if (!keyStore.containsAlias(alias)) {
            val keyGen: KeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_HMAC_SHA512, "AndroidKeyStore")
            val spec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY)
                .setKeySize(256)
                .setDigests(KeyProperties.DIGEST_SHA512)
                .setUserAuthenticationRequired(false)
                .build()
            keyGen.init(spec)
            keyGen.generateKey()
        }

        val pepperKey = keyStore.getKey(alias, null) as SecretKey
        val hmac: Mac = Mac.getInstance("HmacSHA512")
        hmac.init(pepperKey)

        val final: ByteArray = hmac.doFinal(plain)
        return final
    }


    /**
     * Converts the specified char array to a byte array.
     *
     * @param chars Char array to convert to a byte array.
     * @return      Converted byte array.
     */
    private fun charArrayToByteArray(chars: CharArray): ByteArray {
        val bytes = ByteArray(chars.size) { i ->
            chars[i].code.toByte()
        }
        return bytes
    }


    /**
     * Converts the specified byte array to a char array.
     *
     * @param bytes Byte array to convert to a char array.
     * @return      Converted char array.
     */
    private fun byteArrayToCharArray(bytes: ByteArray): CharArray {
        val chars = CharArray(bytes.size) { i ->
            (bytes[i].toInt() and 0xFF).toChar()
        }
        return chars
    }


    /**
     * Converts the specified byte array to a base64-encoded string.
     *
     * @param bytes Byte array to convert to a base64-encoded string.
     * @return      Base64-encoded string.
     */
    private fun byteArrayToBase64(bytes: ByteArray): String {
        return Base64.getEncoder().encodeToString(bytes)
    }


    /**
     * Converts the specified base64-encoded string to a byte array.
     *
     * @param s Base64-encoded string to convert to a byte array.
     * @return  Converted byte array.
     */
    private fun base64ToByteArray(s: String): ByteArray {
        return Base64.getDecoder().decode(s)
    }


    /**
     * Test whether the specified computed hash content equals the stored hash. This method always
     * runs with complexity O(n), so that comparison takes the same time regardless of whether the
     * hashes are identical or not. This minimizes possibilities to guess partial correctness of
     * the hashes (i.e. the password or security answer).
     *
     * @param computedHash      Computed hash that was generated from the user input.
     * @param storedHashBase64  Base64-encoded stored hash that was retrieved from SharedPreferences.
     * @return                  Whether both hash are identical.
     */
    private fun contentEqualsHash(computedHash: CharArray, storedHashBase64: String): Boolean {
        val storedHash: CharArray = byteArrayToCharArray(base64ToByteArray(storedHashBase64))

        var equals = true
        for (i in 0 .. computedHash.size - 1) {
            if (i >= storedHash.size || computedHash[i] != storedHash[i]) {
                equals = false
            }
        }

        return equals
    }

}
