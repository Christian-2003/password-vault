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

}
