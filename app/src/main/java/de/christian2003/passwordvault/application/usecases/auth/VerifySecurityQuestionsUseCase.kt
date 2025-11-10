package de.christian2003.passwordvault.application.usecases.auth

import de.christian2003.passwordvault.application.repository.AuthRepository
import de.christian2003.passwordvault.domain.security.auth.SecurityQuestion
import javax.inject.Inject


/**
 * Use case to verify whether the answers for security questions are valid.
 *
 * @param repository    Repository to access auth data.
 */
class VerifySecurityQuestionsUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    /**
     * Verifies whether the specified security question answers are valid.
     *
     * @param questions Questions and answers to verify.
     */
    suspend fun areSecurityQuestionsValid(questions: Map<SecurityQuestion, String>): Boolean {
        if (questions.size >= 4) {
            return repository.validateSecurityQuestions(questions, 4)
        }
        return false
    }

}
