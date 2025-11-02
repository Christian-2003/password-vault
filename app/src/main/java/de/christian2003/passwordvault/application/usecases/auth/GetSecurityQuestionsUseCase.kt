package de.christian2003.passwordvault.application.usecases.auth

import de.christian2003.passwordvault.application.repository.AuthRepository
import de.christian2003.passwordvault.domain.security.auth.SecurityQuestion


/**
 * Use case to get a list of all configured security questions.
 *
 * @param repository    Repository to access auth information.
 */
class GetSecurityQuestionsUseCase(
    private val repository: AuthRepository
) {

    /**
     * Returns the list of configured security questions.
     *
     * @return  List of configured security questions.
     */
    fun getSecurityQuestions(): List<SecurityQuestion> {
        return repository.getConfiguredQuestions()
    }

}
