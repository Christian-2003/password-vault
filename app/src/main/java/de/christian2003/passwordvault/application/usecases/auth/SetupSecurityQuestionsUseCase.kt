package de.christian2003.passwordvault.application.usecases.auth

import de.christian2003.passwordvault.application.repository.AuthRepository
import de.christian2003.passwordvault.domain.security.auth.SecurityQuestion


/**
 * Use case to setup the security questions when the user opens the app for the first time.
 *
 * @param repository    Repository to access auth information.
 */
class SetupSecurityQuestionsUseCase(
    private val repository: AuthRepository
) {

    /**
     * Sets the passed map of security questions. If security questions are already configured or any
     * of the provided answers is blank, the method returns false and does not setup the questions.
     *
     * @param questions Map of questions and answers.
     * @return          Whether the security questions and answers are set correctly.
     */
    fun setupSecurityQuestions(questions: Map<SecurityQuestion, String>): Boolean {
        if (!repository.hasSecurityQuestions()) {
            var answersValid = true
            questions.forEach { question, answer ->
                if (answer.isBlank()) {
                    answersValid = false
                    return@forEach
                }
            }
            if (answersValid) {
                questions.forEach { question, answer ->
                    repository.addSecurityQuestion(question, answer)
                }
                return true
            }
        }
        return false
    }

}
