package de.christian2003.auth.viewmodels

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.auth.models.recoverycodes.RecoveryCodesFormatter
import de.christian2003.auth.ui.recovery.RecoveryCodeVisualTransformation
import de.christian2003.security.application.usecases.UnlockWithRecoveryCodesUseCase
import de.christian2003.ui.model.HelpCard
import javax.inject.Inject


@HiltViewModel
class RecoveryViewModel @Inject constructor(
    application: Application,
    private val unlockWithRecoveryCodesUseCase: UnlockWithRecoveryCodesUseCase
): AndroidViewModel(application) {

    private val recoveryCodeFormatter: RecoveryCodesFormatter = RecoveryCodesFormatter()

    var isHelpCardVisible: Boolean by mutableStateOf(HelpCard.HelpRecovery.getVisible(application))
        private set

    var recoveryCode: String by mutableStateOf("")

    var isRecoveryCodeValid: Boolean by mutableStateOf(true)
        private set

    var isVerifyingRecoveryCode: Boolean by mutableStateOf(false)
        private set

    val canContinue: State<Boolean> = derivedStateOf { recoveryCode.length == 24 }

    val visualTransformation: VisualTransformation = RecoveryCodeVisualTransformation()


    suspend fun verifyRecoveryCode(): Boolean {
        if (!isVerifyingRecoveryCode) {
            isVerifyingRecoveryCode = true

            val convertedRecoveryCode: CharArray = recoveryCodeFormatter.convertBack(recoveryCode)
            val result = unlockWithRecoveryCodesUseCase.unlock(convertedRecoveryCode)
            isRecoveryCodeValid = result

            isVerifyingRecoveryCode = false
            return result
        }
        return false
    }


    fun dismissHelpCard() {
        HelpCard.HelpRecovery.setVisible(application, false)
        isHelpCardVisible = false
    }

}
