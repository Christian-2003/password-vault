package de.christian2003.passwordvault.application.usecases.auth

import de.christian2003.passwordvault.application.repository.AuthRepository
import de.christian2003.passwordvault.domain.security.auth.SecurityQuestion
import javax.inject.Inject


/**
 * Use case to remove a security question.
 *
 * @param repository    Repository to access auth information.
 */
class RemoveSecurityQuestionUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    /**
     * Removes the specified security question.
     *
     * @param question  Question to remove.
     */
    fun removeQuestion(question: SecurityQuestion) {
        repository.removeSecurityQuestion(question)
    }

}
