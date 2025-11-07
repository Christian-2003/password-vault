package de.christian2003.passwordvault.application.usecases.auth

import de.christian2003.passwordvault.application.repository.AuthRepository


/**
 * Use case to get whether security questions are configured and can be used for recovery.
 *
 * @param repository    Repository to get auth information.
 */
class AreSecurityQuestionsConfiguredUseCase(
    private val repository: AuthRepository
) {

    /**
     * Returns whether the security questions are configured AND can be used for recovery.
     *
     * @return  Whether security questions are configured and can be used for recovery.
     */
    fun areSecurityQuestionsConfigured(): Boolean {
        return repository.hasSecurityQuestions() && repository.getConfiguredQuestions().size >= 5
    }

}
