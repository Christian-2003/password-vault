package de.christian2003.passwordvault.plugin.presentation.view.recovery

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import de.christian2003.passwordvault.application.usecases.auth.GetSecurityQuestionsUseCase
import de.christian2003.passwordvault.application.usecases.auth.VerifySecurityQuestionsUseCase
import de.christian2003.passwordvault.plugin.presentation.view.help.HelpCard
import de.christian2003.passwordvault.plugin.presentation.view.securityquestions.SecurityQuestionUiDto

class RecoveryViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var verifySecurityQuestionsUseCase: VerifySecurityQuestionsUseCase

    private var isInitialized: Boolean = false

    var securityQuestions: MutableList<SecurityQuestionUiDto> = mutableStateListOf()
        private set

    var isHelpCardVisible: Boolean by mutableStateOf(HelpCard.MASTER_PASSWORD_RECOVERY.getVisible(application))
        private set


    fun init(
        getSecurityQuestionsUseCase: GetSecurityQuestionsUseCase,
        verifySecurityQuestionsUseCase: VerifySecurityQuestionsUseCase
    ) {
        if (isInitialized) {
            return
        }

        this.verifySecurityQuestionsUseCase = verifySecurityQuestionsUseCase
        securityQuestions.addAll(getSecurityQuestionsUseCase.getSecurityQuestions().map {
            SecurityQuestionUiDto(it, "")
        })
        isInitialized = true
    }


    fun dismissHelpCard() {
        HelpCard.MASTER_PASSWORD_RECOVERY.setVisible(application, false)
        isHelpCardVisible = false
    }

}
