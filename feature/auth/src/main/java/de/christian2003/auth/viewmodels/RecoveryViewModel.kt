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
import de.christian2003.auth.models.formatters.RecoveryCodesFormatter
import de.christian2003.auth.ui.recovery.RecoveryCodeVisualTransformation
import de.christian2003.core.security.application.usecases.VerifyRecoveryCodeUseCase
import de.christian2003.core.ui.model.HelpCard
import javax.inject.Inject


/**
 * View model for the screen through which the user can enter a recovery code in order to recover
 * a master password.
 *
 * @param application               Application.
 * @param verifyRecoveryCodeUseCase Use case to verify the validity of a recovery code.
 */
@HiltViewModel
internal class RecoveryViewModel @Inject constructor(
    application: Application,
    private val verifyRecoveryCodeUseCase: VerifyRecoveryCodeUseCase
): AndroidViewModel(application) {

    /**
     * Formatter used for formatting recovery codes.
     */
    private val recoveryCodeFormatter: RecoveryCodesFormatter = RecoveryCodesFormatter()

    /**
     * Recovery code entered by the user converted to a char array.
     */
    var recoveryCodeAsCharArray: CharArray = CharArray(0)
        private set

    /**
     * Whether the help card is visible.
     */
    var isHelpCardVisible: Boolean by mutableStateOf(HelpCard.HelpRecovery.getVisible(application))
        private set

    /**
     * Recovery code entered by the user - without any formatting.
     */
    var recoveryCode: String by mutableStateOf("")

    /**
     * Whether the recovery code entered by the user is valid.
     */
    var isRecoveryCodeValid: Boolean by mutableStateOf(true)
        private set

    /**
     * Whether the recovery code entered is currently being verified.
     */
    var isVerifyingRecoveryCode: Boolean by mutableStateOf(false)
        private set

    /**
     * Whether the "continue" button is enabled.
     */
    val canContinue: State<Boolean> = derivedStateOf { recoveryCode.length == 24 }

    /**
     * Visual transformation transforms a recovery code from "ABCD1234ABCD1234" to "ABCD-1234-ABCD-1234".
     */
    val visualTransformation: VisualTransformation = RecoveryCodeVisualTransformation()


    /**
     * Verifies whether the entered recovery code is valid.
     *
     * @return  Whether the recovery code is valid.
     */
    suspend fun verifyRecoveryCode(): Boolean {
        if (!isVerifyingRecoveryCode) {
            isVerifyingRecoveryCode = true

            val convertedRecoveryCode: CharArray = recoveryCodeFormatter.convertBack(recoveryCode)
            val result: Boolean = try {
                verifyRecoveryCodeUseCase.verify(convertedRecoveryCode)
            } catch (_: Exception) {
                false
            }
            recoveryCodeAsCharArray = convertedRecoveryCode
            isRecoveryCodeValid = result

            isVerifyingRecoveryCode = false
            return result
        }
        return false
    }


    /**
     * Dismisses the help card.
     */
    fun dismissHelpCard() {
        HelpCard.HelpRecovery.setVisible(application, false)
        isHelpCardVisible = false
    }

}
