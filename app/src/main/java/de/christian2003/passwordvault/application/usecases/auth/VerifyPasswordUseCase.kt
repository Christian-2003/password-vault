package de.christian2003.passwordvault.application.usecases.auth

import de.christian2003.passwordvault.application.repository.AuthRepository


/**
 * Use case to verify a password.
 *
 * @param repository    Repository to access the passwords.
 */
class VerifyPasswordUseCase(
    private val repository: AuthRepository
) {

    /**
     * Returns whether the specified password is valid.
     *
     * @param password  Password to verify.
     * @return          Whether the specified password is valid.
     */
    fun isPasswordValid(password: String): Boolean {
        return repository.isPasswordValid(password)
    }

}
