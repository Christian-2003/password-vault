package de.christian2003.passwordvault.application.usecases.auth

import de.christian2003.passwordvault.application.repository.AuthRepository
import javax.inject.Inject


/**
 * Use case to verify a password.
 *
 * @param repository    Repository to access the passwords.
 */
class VerifyPasswordUseCase @Inject constructor(
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
