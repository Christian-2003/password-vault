package de.christian2003.passwordvault.plugin.presentation.view.securityquestions

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import de.christian2003.passwordvault.application.usecases.auth.ChangeSecurityQuestionUseCase
import de.christian2003.passwordvault.application.usecases.auth.GetSecurityQuestionsUseCase
import de.christian2003.passwordvault.application.usecases.auth.SetupSecurityQuestionsUseCase
import de.christian2003.passwordvault.plugin.presentation.view.help.HelpCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SecurityQuestionsViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var setupSecurityQuestionsUseCase: SetupSecurityQuestionsUseCase
    private lateinit var changeSecurityQuestionUseCase: ChangeSecurityQuestionUseCase

    private var isInitialized: Boolean = false

    var isSetup: Boolean = false
        private set

    var isHelpCardVisible: Boolean by mutableStateOf(HelpCard.SECURITY_QUESTION.getVisible(application))
        private set

    val securityQuestions: MutableList<SecurityQuestionUiDto> = mutableStateListOf()


    fun init(
        setupSecurityQuestionsUseCase: SetupSecurityQuestionsUseCase,
        getSecurityQuestionsUseCase: GetSecurityQuestionsUseCase,
        changeSecurityQuestionUseCase: ChangeSecurityQuestionUseCase,
        isSetup: Boolean
    ) {
        if (isInitialized) {
            return
        }

        this.setupSecurityQuestionsUseCase = setupSecurityQuestionsUseCase
        this.changeSecurityQuestionUseCase = changeSecurityQuestionUseCase
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


    fun dismissHelpCard() {
        HelpCard.SECURITY_QUESTION.setVisible(application, false)
        isHelpCardVisible = false
    }

}
