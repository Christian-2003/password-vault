package de.christian2003.passwordvault.plugin.presentation.view.recovery

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.passwordvault.application.usecases.auth.GetSecurityQuestionsUseCase
import de.christian2003.passwordvault.application.usecases.auth.VerifySecurityQuestionsUseCase
import de.christian2003.passwordvault.plugin.presentation.view.help.HelpCard
import de.christian2003.passwordvault.plugin.presentation.view.securityquestions.SecurityQuestionUiDto
import javax.inject.Inject


@HiltViewModel
class RecoveryViewModel @Inject constructor(
    application: Application,
    getSecurityQuestionsUseCase: GetSecurityQuestionsUseCase,
    private val verifySecurityQuestionsUseCase: VerifySecurityQuestionsUseCase
): AndroidViewModel(application) {

    val securityQuestions: MutableList<SecurityQuestionUiDto> = mutableStateListOf()

    var isHelpCardVisible: Boolean by mutableStateOf(HelpCard.MASTER_PASSWORD_RECOVERY.getVisible(application))
        private set

    init {
        securityQuestions.addAll(getSecurityQuestionsUseCase.getSecurityQuestions().map {
            SecurityQuestionUiDto(it, "")
        })
    }


    fun dismissHelpCard() {
        HelpCard.MASTER_PASSWORD_RECOVERY.setVisible(application, false)
        isHelpCardVisible = false
    }

}
