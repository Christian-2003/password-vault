package de.christian2003.passwordvault.application.usecases.auth

import de.christian2003.passwordvault.application.repository.AuthRepository
import de.christian2003.passwordvault.domain.security.auth.SecurityQuestion
import javax.inject.Inject


/**
 * Use case to get a list of all configured security questions.
 *
 * @param repository    Repository to access auth information.
 */
class GetSecurityQuestionsUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    /**
     * Returns the list of configured security questions.
     *
     * @return  List of configured security questions.
     */
    suspend fun getSecurityQuestions(): List<SecurityQuestion> {
        return repository.getConfiguredQuestions()
    }

}
