package de.christian2003.passwordvault.plugin.presentation.view.recovery

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.passwordvault.application.usecases.auth.GetSecurityQuestionsUseCase
import de.christian2003.passwordvault.application.usecases.auth.VerifySecurityQuestionsUseCase
import de.christian2003.passwordvault.domain.security.auth.SecurityQuestion
import de.christian2003.passwordvault.plugin.presentation.view.help.HelpCard
import de.christian2003.passwordvault.plugin.presentation.view.securityquestions.SecurityQuestionUiDto
import javax.inject.Inject


/**
 * View model for the screen through which to recover the master password.
 *
 * @param application                       Application.
 * @param getSecurityQuestionsUseCase       Use case to get a list of configured security questions.
 * @param verifySecurityQuestionsUseCase    Use case to verify the answers to security questions.
 */
@HiltViewModel
class RecoveryViewModel @Inject constructor(
    application: Application,
    getSecurityQuestionsUseCase: GetSecurityQuestionsUseCase,
    private val verifySecurityQuestionsUseCase: VerifySecurityQuestionsUseCase
): AndroidViewModel(application) {

    /**
     * List of security questions that are being answered by the user.
     */
    val securityQuestions: MutableList<SecurityQuestionUiDto> = mutableStateListOf()

    /**
     * Indicates whether the help card is visible.
     */
    var isHelpCardVisible: Boolean by mutableStateOf(HelpCard.MASTER_PASSWORD_RECOVERY.getVisible(application))
        private set

    /**
     * Indicates whether the app is currently validating the answers to the security questions.
     */
    var isValidatingAnswers: Boolean by mutableStateOf(false)
        private set

    /**
     * Indicates whether the answers provided are valid. This is null until the first time they
     * are validated.
     */
    var answersValid: Boolean? by mutableStateOf(null)
        private set


    /**
     * Initializes the view model.
     */
    init {
        securityQuestions.addAll(getSecurityQuestionsUseCase.getSecurityQuestions().map {
            SecurityQuestionUiDto(it, "")
        })
    }


    /**
     * Verifies the answers to the security questions.
     *
     * @return  Whether the security questions are valid.
     */
    fun verifySecurityQuestions(): Boolean {
        isValidatingAnswers = true
        val answers: MutableMap<SecurityQuestion, String> = mutableMapOf()
        securityQuestions.forEach { question ->
            answers[question.question] = question.answer ?: ""
        }
        val result: Boolean = verifySecurityQuestionsUseCase.areSecurityQuestionsValid(answers)
        answersValid = result
        isValidatingAnswers = false
        return result
    }


    /**
     * Converts the list of security question DTOs to a map.
     *
     * @return  Map which maps the answers to the security questions.
     */
    fun securityQuestionsToMap(): Map<SecurityQuestion, String> {
        val map: MutableMap<SecurityQuestion, String> = mutableMapOf()
        securityQuestions.forEach { question ->
            map[question.question] = question.answer ?: ""
        }
        return map
    }


    /**
     * Dismisses the help card.
     */
    fun dismissHelpCard() {
        HelpCard.MASTER_PASSWORD_RECOVERY.setVisible(application, false)
        isHelpCardVisible = false
    }

}
