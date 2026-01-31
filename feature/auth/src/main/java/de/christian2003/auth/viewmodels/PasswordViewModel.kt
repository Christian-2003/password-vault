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
import de.christian2003.auth.navigation.MasterPassword
import de.christian2003.security.application.usecases.SetupNewMasterPasswordUseCase
import de.christian2003.ui.model.HelpCard
import javax.inject.Inject


@HiltViewModel
class PasswordViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle
): AndroidViewModel(application) {

    val state: PasswordScreenState = savedStateHandle.toRoute<MasterPassword>().state

    var currentPassword: String by mutableStateOf("")

    var newPassword: String by mutableStateOf("")

    var confirmNewPassword: String by mutableStateOf("")

    var isCurrentPasswordValid: Boolean by mutableStateOf(true)
        private set

    var isNewPasswordValid: Boolean by mutableStateOf(true)
        private set

    var isConfirmNewPasswordValid: Boolean by mutableStateOf(true)
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
            else -> newPassword.isNotBlank()
                    && confirmNewPassword.isNotBlank()
        }
    }

    /**
     * Indicates whether all data is valid. This must be true before continuing to the next setup
     * screen.
     */
    var isAllDataValid: State<Boolean> = derivedStateOf {
        return@derivedStateOf isContinueButtonEnabled.value
                && isCurrentPasswordValid
                && isNewPasswordValid
                && isConfirmNewPasswordValid
    }

    var isHelpCardVisible: Boolean by mutableStateOf(HelpCard.HelpSetupMasterPassword.getVisible(application))
        private set


    fun dismissHelpCard() {
        isHelpCardVisible = false
        HelpCard.HelpSetupMasterPassword.setVisible(application, false)
    }

}
