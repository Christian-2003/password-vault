package de.christian2003.passwordvault.application.usecases.auth

import de.christian2003.passwordvault.application.repository.AuthRepository
import de.christian2003.passwordvault.domain.security.auth.SecurityQuestion


/**
 * Use case to change an existing question and replace it with a new one.
 *
 * @param repository    Repository to access auth information.
 */
class ChangeSecurityQuestionUseCase(
    private val repository: AuthRepository
) {

    /**
     * Changes the old security question that is specified with the passed new question and answer.
     * If the old question is not currently configured, nothing happens and false is returned.
     *
     * @param oldQuestion   Old question to replace.
     * @param newQuestion   New question.
     * @param newAnswer     New answer.
     */
    fun changeQuestion(oldQuestion: SecurityQuestion, newQuestion: SecurityQuestion, newAnswer: String): Boolean {
        if (repository.getConfiguredQuestions().contains(oldQuestion)) {
            repository.removeSecurityQuestion(oldQuestion)
            repository.addSecurityQuestion(newQuestion, newAnswer)
            return true
        }
        return false
    }

}
