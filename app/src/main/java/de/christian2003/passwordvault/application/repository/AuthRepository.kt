package de.christian2003.passwordvault.application.repository


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

}
