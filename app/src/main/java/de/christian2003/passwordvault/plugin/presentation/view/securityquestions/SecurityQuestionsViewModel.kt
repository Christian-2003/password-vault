package de.christian2003.passwordvault.plugin.presentation.view.securityquestions

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import de.christian2003.passwordvault.application.usecases.auth.AddSecurityQuestionUseCase
import de.christian2003.passwordvault.application.usecases.auth.GetSecurityQuestionsUseCase
import de.christian2003.passwordvault.application.usecases.auth.RemoveSecurityQuestionUseCase
import de.christian2003.passwordvault.domain.security.auth.SecurityQuestion
import de.christian2003.passwordvault.plugin.presentation.view.help.HelpCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * View model for the screen through which to configure security questions.
 */
class SecurityQuestionsViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var addSecurityQuestionUseCase: AddSecurityQuestionUseCase
    private lateinit var removeSecurityQuestionUseCase: RemoveSecurityQuestionUseCase

    /**
     * Indicates whether the view model is initialized.
     */
    private var isInitialized: Boolean = false

    /**
     * Indicates whether the screen is shown through the setup flow.
     */
    var isSetup: Boolean = false
        private set

    /**
     * Indicates whether the help card is visible.
     */
    var isHelpCardVisible: Boolean by mutableStateOf(HelpCard.SECURITY_QUESTION.getVisible(application))
        private set

    /**
     * Indicates whether the dialog to create a new security question is visible.
     */
    var isCreateDialogVisible: Boolean by mutableStateOf(false)

    /**
     * Indicates whether the dialog is visible, that shows the user why the answer to one of their
     * security questions cannot be displayed.
     */
    var isHelpDialogVisible: Boolean by mutableStateOf(false)

    /**
     * Security question that is currently being edited.
     */
    var questionToEdit: SecurityQuestionUiDto? by mutableStateOf(null)

    /**
     * Security question that is currently waiting for confirmation to be deleted.
     */
    var questionToDelete: SecurityQuestionUiDto? by mutableStateOf(null)

    /**
     * List of security questions that are configured by the user.
     */
    val securityQuestions: MutableList<SecurityQuestionUiDto> = mutableStateListOf()


    /**
     * Initializes the view model.
     *
     * @param getSecurityQuestionsUseCase   Use case to get a list of configured security questions.
     * @param addSecurityQuestionUseCase    Use case to add (or replace) a security question.
     * @param removeSecurityQuestionUseCase Use case to remove a security question.
     * @param isSetup                       Whether the screen is created for the setup flow.
     */
    fun init(
        getSecurityQuestionsUseCase: GetSecurityQuestionsUseCase,
        addSecurityQuestionUseCase: AddSecurityQuestionUseCase,
        removeSecurityQuestionUseCase: RemoveSecurityQuestionUseCase,
        isSetup: Boolean
    ) {
        if (isInitialized) {
            return
        }

        this.addSecurityQuestionUseCase = addSecurityQuestionUseCase
        this.removeSecurityQuestionUseCase = removeSecurityQuestionUseCase
        this.isSetup = isSetup
        isInitialized = true
        if (!isSetup) {
            viewModelScope.launch(Dispatchers.IO) {
                getSecurityQuestionsUseCase.getSecurityQuestions().forEach { question ->
                    securityQuestions.add(SecurityQuestionUiDto(
                        question = question,
                        answer = null
                    ))
                }
            }
        }
    }


    /**
     * Dismisses the dialog through which to create a new security question.
     *
     * @param question  Question to create.
     * @param answer    Answer for the question.
     */
    fun dismissCreateDialog(question: SecurityQuestion? = null, answer: String? = null) = viewModelScope.launch(Dispatchers.IO) {
        isCreateDialogVisible = false
        if (question != null && answer != null && answer.isNotBlank()) {
            val existingQuestion: SecurityQuestionUiDto? = securityQuestions.find { it.question == question }
            if (existingQuestion == null) {
                securityQuestions.add(SecurityQuestionUiDto(
                    question = question,
                    answer = answer
                ))
                addSecurityQuestionUseCase.addQuestion(question, answer)
            }
        }
    }


    /**
     * Dismisses the dialog through which to edit an existing security question.
     *
     * @param question  Question to edit.
     * @param answer    Answer to edit.
     */
    fun dismissEditQuestionDialog(question: SecurityQuestion? = null, answer: String? = null) = viewModelScope.launch(Dispatchers.IO) {
        val existingQuestion: SecurityQuestionUiDto? = questionToEdit
        questionToEdit = null
        if (existingQuestion != null && question != null && answer != null) {
            val index: Int = securityQuestions.indexOf(existingQuestion)
            securityQuestions[index] = existingQuestion.copy(
                question = question,
                answer = answer
            )
            addSecurityQuestionUseCase.addQuestion(question, answer)
        }
    }


    /**
     * Dismisses the dialog to confirm deletion of a security question.
     *
     * @param question  Question to delete or null to dismiss without deleting.
     */
    fun dismissDeleteQuestionDialog(question: SecurityQuestion? = null) = viewModelScope.launch(Dispatchers.IO) {
        questionToDelete = null
        if (question != null) {
            val existingQuestion: SecurityQuestionUiDto? = securityQuestions.find { it.question == question }
            securityQuestions.remove(existingQuestion)
            removeSecurityQuestionUseCase.removeQuestion(question)
        }
    }


    /**
     * Dismisses the help card.
     */
    fun dismissHelpCard() {
        HelpCard.SECURITY_QUESTION.setVisible(application, false)
        isHelpCardVisible = false
    }

}
