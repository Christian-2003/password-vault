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
import de.christian2003.passwordvault.application.usecases.auth.SetupSecurityQuestionsUseCase
import de.christian2003.passwordvault.domain.security.auth.SecurityQuestion
import de.christian2003.passwordvault.plugin.presentation.view.help.HelpCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SecurityQuestionsViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var setupSecurityQuestionsUseCase: SetupSecurityQuestionsUseCase
    private lateinit var addSecurityQuestionUseCase: AddSecurityQuestionUseCase
    private lateinit var removeSecurityQuestionUseCase: RemoveSecurityQuestionUseCase

    private var isInitialized: Boolean = false

    var isSetup: Boolean = false
        private set

    var isHelpCardVisible: Boolean by mutableStateOf(HelpCard.SECURITY_QUESTION.getVisible(application))
        private set

    var isCreateDialogVisible: Boolean by mutableStateOf(false)

    var questionToEdit: SecurityQuestionUiDto? by mutableStateOf(null)

    var questionToDelete: SecurityQuestionUiDto? by mutableStateOf(null)

    val securityQuestions: MutableList<SecurityQuestionUiDto> = mutableStateListOf()


    fun init(
        setupSecurityQuestionsUseCase: SetupSecurityQuestionsUseCase,
        getSecurityQuestionsUseCase: GetSecurityQuestionsUseCase,
        addSecurityQuestionUseCase: AddSecurityQuestionUseCase,
        removeSecurityQuestionUseCase: RemoveSecurityQuestionUseCase,
        isSetup: Boolean
    ) {
        if (isInitialized) {
            return
        }

        this.setupSecurityQuestionsUseCase = setupSecurityQuestionsUseCase
        this.addSecurityQuestionUseCase = addSecurityQuestionUseCase
        this.removeSecurityQuestionUseCase = removeSecurityQuestionUseCase
        this.isSetup = isSetup
        isInitialized = true
        if (!isSetup) {
            viewModelScope.launch(Dispatchers.IO) {
                getSecurityQuestionsUseCase.getSecurityQuestions().forEach { question ->
                    securityQuestions.add(SecurityQuestionUiDto(
                        question = question,
                        answer = null,
                        hasAnswer = false
                    ))
                }
            }
        }
    }


    fun dismissCreateDialog(question: SecurityQuestion? = null, answer: String? = null) = viewModelScope.launch(Dispatchers.IO) {
        isCreateDialogVisible = false
        if (question != null && answer != null && answer.isNotBlank()) {
            val existingQuestion: SecurityQuestionUiDto? = securityQuestions.find { it.question == question }
            if (existingQuestion == null) {
                securityQuestions.add(SecurityQuestionUiDto(
                    question = question,
                    answer = answer,
                    hasAnswer = true
                ))
                addSecurityQuestionUseCase.addQuestion(question, answer)
            }
        }
    }

    fun dismissEditQuestionDialog(question: SecurityQuestion? = null, answer: String? = null) = viewModelScope.launch(Dispatchers.IO) {
        val existingQuestion: SecurityQuestionUiDto? = questionToEdit
        questionToEdit = null
        if (existingQuestion != null && question != null && answer != null) {
            val index: Int = securityQuestions.indexOf(existingQuestion)
            securityQuestions[index] = existingQuestion.copy(
                question = question,
                answer = answer,
                hasAnswer = true
            )
            addSecurityQuestionUseCase.addQuestion(question, answer)
        }
    }

    fun dismissDeleteQuestionDialog(question: SecurityQuestion? = null) = viewModelScope.launch(Dispatchers.IO) {
        questionToDelete = null
        if (question != null) {
            val existingQuestion: SecurityQuestionUiDto? = securityQuestions.find { it.question == question }
            securityQuestions.remove(existingQuestion)
            removeSecurityQuestionUseCase.removeQuestion(question)
        }
    }


    fun dismissHelpCard() {
        HelpCard.SECURITY_QUESTION.setVisible(application, false)
        isHelpCardVisible = false
    }

}
