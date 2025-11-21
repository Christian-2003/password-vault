package de.christian2003.passwordvault.application.usecases.auth

import de.christian2003.passwordvault.application.repository.AuthRepository
import javax.inject.Inject


/**
 * Use case for the first time setup of the app authentication.
 *
 * @param repository    Repository to access auth information.
 */
class SetupAuthUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    /**
     * Creates the authentication for the app for the first time. This only works if no password is
     * set currently.
     *
     * @param password  Password for app authentication.
     * @return          Whether the setup was successful.
     */
    suspend fun setup(password: CharArray): Boolean {
        if (repository.hasPassword()) {
            return false
        }
        repository.setPassword(password)
        return true
    }

}
