package de.christian2003.passwordvault.application.usecases.auth

import de.christian2003.passwordvault.application.repository.AuthRepository
import de.christian2003.passwordvault.domain.security.auth.SecurityQuestion
import javax.inject.Inject


/**
 * Use case to update a password.
 *
 * @param repository    Repository to access the passwords.
 */
class UpdatePasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    /**
     * Updates the password. The method returns 'true' if the password was successfully updated.
     * If the old password does not match or if no password was set previously, the method returns
     * 'false'.
     *
     * @param newPassword   New password to set.
     * @param oldPassword   Old password to verify the user's identity.
     * @return              Whether the password was updated successfully.
     */
    suspend fun updatePassword(newPassword: CharArray, oldPassword: CharArray): Boolean {
        if (repository.hasPassword() && repository.isPasswordValid(oldPassword)) {
            repository.setPassword(newPassword)
            return true
        }
        return false
    }


    /**
     * Updates the password. The method returns 'true' if the password was successfully updated.
     * If the security questions are invalid or if no password was set previously, the method returns
     * 'false'.
     *
     * @param newPassword       New password to set.
     * @param securityQuestions Security questions to verify the user's identity.
     * @return                  Whether the password was updated successfully.
     */
    suspend fun updatePassword(newPassword: CharArray, securityQuestions: Map<SecurityQuestion, CharArray>): Boolean {
        if (repository.hasPassword()
            && securityQuestions.size >= 4
            && repository.hasSecurityQuestions()
            && repository.validateSecurityQuestions(securityQuestions, 4)
        ) {
            repository.setPassword(newPassword)
            return true
        }
        return false
    }

}
