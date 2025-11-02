package de.christian2003.passwordvault.application.repository

import de.christian2003.passwordvault.domain.security.auth.SecurityQuestion


/**
 * Repository that can be used for authentication.
 */
interface AuthRepository {

    /**
     * Returns whether a password is set by the user.
     *
     * @return  Whether a password is set by the user.
     */
    fun hasPassword(): Boolean


    /**
     * Changes the password.
     *
     * @param newPassword   New password.
     */
    fun setPassword(newPassword: String)


    /**
     * Tests whether the specified password is valid.
     *
     * @param password  Password to test.
     * @return          Whether the password is valid.
     */
    fun isPasswordValid(password: String): Boolean


    /**
     * Returns whether the device supports biometric authentication.
     *
     * @return  Whether the device supports biometric authentication.
     */
    fun doesDeviceSupportBiometrics(): Boolean


    /**
     * Returns whether the app should use biometrics for authentication.
     *
     * @return  Whether to use biometrics for authentication.
     */
    fun hasBiometrics(): Boolean


    /**
     * Changes whether the app should use biometrics for authentication.
     *
     * @param biometrics    Whether to use biometrics for authentication.
     */
    fun setBiometrics(biometrics: Boolean)


    /**
     * Returns whether security questions are configured for the app.
     *
     * @return  Whether security questions are configured.
     */
    fun hasSecurityQuestions(): Boolean


    /**
     * Adds the specified combination of security question and answer to the configured questions.
     *
     * @param question  Security question.
     * @param answer    Answer to the security question.
     */
    fun addSecurityQuestion(question: SecurityQuestion, answer: String)


    /**
     * Removes the specified security question from the configured questions.
     *
     * @param question  Question to remove.
     */
    fun removeSecurityQuestion(question: SecurityQuestion)


    /**
     * Returns a list of the configured security questions.
     *
     * @return  List of configured security questions.
     */
    fun getConfiguredQuestions(): List<SecurityQuestion>


    /**
     * Validates the specified security questions. If the number of correct questions is equal to
     * (or exceeds) the passed threshold, the validation succeeds. Otherwise, it fails.
     *
     * @param questions Answered security questions.
     * @param threshold Number of questions that need to be answered correctly to succeed.
     * @return          Whether the answers to the security questions are valid.
     */
    fun validateSecurityQuestions(questions: Map<SecurityQuestion, String>, threshold: Int): Boolean

}
