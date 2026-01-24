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
import de.christian2003.auth.models.password.PasswordScreenState
import de.christian2003.auth.navigation.MasterPassword
import de.christian2003.security.application.usecases.SetupNewMasterPasswordUseCase
import de.christian2003.ui.model.HelpCard
import javax.inject.Inject


@HiltViewModel
class PasswordViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val setupNewMasterPasswordUseCase: SetupNewMasterPasswordUseCase
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

    var isContinueButtonEnabled: State<Boolean> = derivedStateOf {
        return@derivedStateOf when(state) {
            PasswordScreenState.ChangePassword -> currentPassword.isNotEmpty()
                    && newPassword.isNotEmpty()
                    && confirmNewPassword.isNotEmpty()
            else -> newPassword.isNotEmpty()
                    && confirmNewPassword.isNotEmpty()
        }
    }

    var isSettingNewMasterPassword: Boolean by mutableStateOf(false)
        private set

    var isMasterPasswordSetSuccessfully: Boolean by mutableStateOf(false)
        private set

    var isHelpCardVisible: Boolean by mutableStateOf(HelpCard.HelpSetupMasterPassword.getVisible(application))
        private set


    suspend fun setNewMasterPassword() {
        if (!isSettingNewMasterPassword) {
            val newPassword: String = this@PasswordViewModel.newPassword
            val confirmNewPassword: String = this@PasswordViewModel.confirmNewPassword

            //Validate inputs:
            isNewPasswordValid = newPassword.isNotEmpty()
            isConfirmNewPasswordValid = confirmNewPassword.isNotEmpty() && newPassword == confirmNewPassword

            //Set new password:
            if (isContinueButtonEnabled.value) {
                isSettingNewMasterPassword = true

                if (isNewPasswordValid && isConfirmNewPasswordValid) {
                    isConfirmNewPasswordValid = true
                    setupNewMasterPasswordUseCase.setupMasterPassword(newPassword.toCharArray())
                    //TODO: When changing PW, we need to test whether the master password entered is correct!
                    isMasterPasswordSetSuccessfully = true
                }

                isSettingNewMasterPassword = false
            }
        }
    }


    fun dismissHelpCard() {
        isHelpCardVisible = false
        HelpCard.HelpSetupMasterPassword.setVisible(application, false)
    }

}
