package de.christian2003.passwordvault.application.usecases.auth

import de.christian2003.passwordvault.application.repository.AuthRepository


/**
 * Use case to update a password.
 *
 * @param repository    Repository to access the passwords.
 */
class UpdatePasswordUseCase(
    private val repository: AuthRepository
) {

    /**
     * Updates the password. The method returns 'true' if the password was successfully update.
     * If the old password does not match or if no password was set previously, the method returns
     * 'false'.
     *
     * @param newPassword   New password to set.
     * @param oldPassword   Old password to verify the user's identity.
     * @return              Whether the password was updated successfully.
     */
    fun updatePassword(newPassword: String, oldPassword: String): Boolean {
        if (repository.hasPassword() && repository.isPasswordValid(oldPassword)) {
            repository.setPassword(newPassword)
            return true
        }
        return false
    }

}
