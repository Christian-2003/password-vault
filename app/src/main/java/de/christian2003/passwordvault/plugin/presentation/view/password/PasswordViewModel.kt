package de.christian2003.passwordvault.plugin.presentation.view.password

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.passwordvault.application.usecases.auth.SetupAuthUseCase
import de.christian2003.passwordvault.application.usecases.auth.UpdatePasswordUseCase
import de.christian2003.passwordvault.application.usecases.auth.VerifyPasswordUseCase
import de.christian2003.passwordvault.domain.security.auth.SecurityQuestion
import de.christian2003.passwordvault.plugin.presentation.view.help.HelpCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * View model for the screen through which the user can setup and change the master password.
 *
 * @param application           Application.
 * @param savedStateHandle      Saved state handle.
 * @param setupAuthUseCase      Use case to setup authentication.
 * @param updatePasswordUseCase Use case to update an existing password.
 * @param verifyPasswordUseCase Use case to verify whether a password entered is the master password.
 */
@HiltViewModel
class PasswordViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val setupAuthUseCase: SetupAuthUseCase,
    private val updatePasswordUseCase: UpdatePasswordUseCase,
    private val verifyPasswordUseCase: VerifyPasswordUseCase
): AndroidViewModel(application) {

    /**
     * Indicates whether the screen is displayed for the initial setup (i.e. when the user configures
     * the master password for the first time) or for the recovery through security questions.
     */
    lateinit var flow: PasswordScreenFlow

    /**
     * Old password entered by the user.
     */
    var oldPassword: String by mutableStateOf("")

    /**
     * New password entered by the user.
     */
    var newPassword: String by mutableStateOf("")

    /**
     * Repeated new password entered by the user.
     */
    var repeatNewPassword: String by mutableStateOf("")

    /**
     * Indicates whether the data entered is valid.
     */
    var isDataValid: State<Boolean> = derivedStateOf {
        return@derivedStateOf (flow != PasswordScreenFlow.None || oldPassword.isNotBlank())
                && newPassword.isNotBlank()
                && repeatNewPassword.isNotBlank()
    }

    /**
     * Indicates whether the old password is correct.
     */
    var isOldPasswordValid: Boolean by mutableStateOf(true)

    /**
     * Indicates whether the repeated new password is valid.
     */
    var isRepeatNewPasswordValid: Boolean by mutableStateOf(true)

    /**
     * Whether the password is currently being set.
     */
    var isSettingPassword: Boolean by mutableStateOf(false)

    /**
     * Indicates whether the help card is visible.
     */
    var isHelpCardVisible: Boolean by mutableStateOf(HelpCard.PASSWORD.getVisible(application))
        private set

    /**
     * Saves the password entered by the user. If the data is not valid, "isOldPasswordValid",
     * "isRepeatNewPasswordValid" and "isDataValid" are updated accordingly.
     *
     * @param securityQuestions Answers to the security questions used for identity verification if
     *                          the password should be recovered. In any other case (e.g. during the
     *                          initial setup), pass null.
     */
    suspend fun save(securityQuestions: Map<SecurityQuestion, String>? = null) {
        if (!isSettingPassword) {
            isSettingPassword = true
            val areNewPasswordsValid: Boolean = newPassword == repeatNewPassword && newPassword.isNotBlank()
            this@PasswordViewModel.isRepeatNewPasswordValid = areNewPasswordsValid
            if (areNewPasswordsValid) {
                when (flow) {
                    PasswordScreenFlow.Setup -> {
                        //Set master password for the first time:
                        setupAuthUseCase.setup(newPassword)
                    }
                    PasswordScreenFlow.Recovery -> {
                        //Recover existing master password:
                        if (securityQuestions != null) {
                            updatePasswordUseCase.updatePassword(newPassword, securityQuestions)
                        }
                    }
                    PasswordScreenFlow.None -> {
                        //Change existing master password:
                        val isOldPasswordValid: Boolean = verifyPasswordUseCase.isPasswordValid(oldPassword)
                        this@PasswordViewModel.isOldPasswordValid = isOldPasswordValid
                        if (isOldPasswordValid) {
                            updatePasswordUseCase.updatePassword(newPassword, oldPassword)
                        }
                    }
                }
            }
            isSettingPassword = false
        }
    }



    /**
     * Dismisses the help card.
     */
    fun dismissHelpCard() {
        HelpCard.PASSWORD.setVisible(application, false)
        isHelpCardVisible = false
    }

}
