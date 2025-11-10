package de.christian2003.passwordvault.application.usecases.auth

import de.christian2003.passwordvault.application.repository.AuthRepository
import de.christian2003.passwordvault.domain.security.auth.SecurityQuestion
import javax.inject.Inject


/**
 * Use case to add a new security question.
 *
 * @param repository    Repository to access auth information.
 */
class AddSecurityQuestionUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    /**
     * Adds the specified security question. If the question exists already, it will be replaced.
     *
     * @param question      New question to add.
     * @param newAnswer     New answer to add.
     */
    suspend fun addQuestion(question: SecurityQuestion, newAnswer: String) {
        if (repository.getConfiguredQuestions().contains(question)) {
            repository.removeSecurityQuestion(question)
        }
        repository.addSecurityQuestion(question, newAnswer)
    }

}
