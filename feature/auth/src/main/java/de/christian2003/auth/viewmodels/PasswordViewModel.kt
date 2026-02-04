package de.christian2003.auth.viewmodels

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.application
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.auth.models.states.PasswordScreenState
import de.christian2003.auth.navigation.PasswordDestination
import de.christian2003.core.security.application.usecases.VerifyMasterPasswordUseCase
import de.christian2003.core.ui.model.HelpCard
import javax.inject.Inject


/**
 * View model for the screen through which the user can enter an existing master password or change
 * the master password.
 *
 * @param application                   Application.
 * @param savedStateHandle              Saved state handle.
 * @param verifyMasterPasswordUseCase   Use case to verify the validity of a master password.
 */
@HiltViewModel
internal class PasswordViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val verifyMasterPasswordUseCase: VerifyMasterPasswordUseCase
): AndroidViewModel(application) {

    /**
     * State of the screen.
     */
    val state: PasswordScreenState = savedStateHandle.toRoute<PasswordDestination>().state

    /**
     * Current password entered by the user.
     */
    var currentPassword: String by mutableStateOf("")

    /**
     * New password entered by the user.
     */
    var newPassword: String by mutableStateOf("")

    /**
     * Confirmed new password entered by the user.
     */
    var confirmNewPassword: String by mutableStateOf("")

    /**
     * Whether the current password is valid.
     */
    var isCurrentPasswordValid: Boolean by mutableStateOf(true)
        private set

    /**
     * Whether the new password is valid.
     */
    var isNewPasswordValid: Boolean by mutableStateOf(true)
        private set

    /**
     * Whether the confirmed new password is valid.
     */
    var isConfirmNewPasswordValid: Boolean by mutableStateOf(true)
        private set

    /**
     * Whether the current password is being verified currently.
     */
    var isVerifyingCurrentPassword: Boolean by mutableStateOf(false)
        private set

    /**
     * Indicates whether the button to continue is enabled.
     * If the button is enabled, this only means that all data required is entered, but it has not
     * been validated yet.
     */
    var isContinueButtonEnabled: State<Boolean> = derivedStateOf {
        return@derivedStateOf when(state) {
            PasswordScreenState.ChangePassword -> currentPassword.isNotBlank()
                    && newPassword.isNotBlank()
                    && confirmNewPassword.isNotBlank()
            PasswordScreenState.GenerateNewRecoveryCodes -> currentPassword.isNotBlank()
            PasswordScreenState.EnableBiometrics -> currentPassword.isNotBlank()
            else -> newPassword.isNotBlank()
                    && confirmNewPassword.isNotBlank()
        }
    }

    /**
     * Whether the help card is visible.
     */
    var isHelpCardVisible: Boolean by mutableStateOf(HelpCard.HelpSetupMasterPassword.getVisible(application))
        private set


    /**
     * Verifies the validity of the current password.
     *
     * @return  Whether the current password is valid.
     */
    suspend fun verifyCurrentPassword(): Boolean {
        if (!isVerifyingCurrentPassword) {
            isVerifyingCurrentPassword = true

            val isValid = verifyMasterPasswordUseCase.verify(currentPassword.toCharArray())

            isCurrentPasswordValid = isValid
            isVerifyingCurrentPassword = false

            return isValid
        }
        return false
    }


    /**
     * Dismisses the help card.
     */
    fun dismissHelpCard() {
        isHelpCardVisible = false
        HelpCard.HelpSetupMasterPassword.setVisible(application, false)
    }

}
