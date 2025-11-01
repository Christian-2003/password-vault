package de.christian2003.passwordvault.plugin.presentation.view.password

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import de.christian2003.passwordvault.application.usecases.auth.SetupAuthUseCase
import de.christian2003.passwordvault.application.usecases.auth.UpdatePasswordUseCase
import de.christian2003.passwordvault.application.usecases.auth.VerifyPasswordUseCase
import de.christian2003.passwordvault.plugin.presentation.view.help.HelpCard


/**
 * View model for the screen through which the user can setup and change the master password.
 */
class PasswordViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var setupAuthUseCase: SetupAuthUseCase
    private lateinit var updatePasswordUseCase: UpdatePasswordUseCase
    private lateinit var verifyPasswordUseCase: VerifyPasswordUseCase

    /**
     * Indicates whether the view model is initialized.
     */
    private var isInitialized: Boolean = false

    /**
     * Indicates whether the screen is displayed for the initial setup (i.e. when the user configures
     * the master password for the first time).
     */
    var isSetup: Boolean by mutableStateOf(false)

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
        return@derivedStateOf (isSetup || oldPassword.isNotBlank())
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
     * Indicates whether the help card is visible.
     */
    var isHelpCardVisible: Boolean by mutableStateOf(HelpCard.PASSWORD.getVisible(application))
        private set


    /**
     * Initializes the view model.
     *
     * @param setupAuthUseCase      Use case to setup authentication.
     * @param updatePasswordUseCase Use case to update an existing master password.
     * @param verifyPasswordUseCase Use case to verify the validity of a password.
     * @param isSetup               Whether the screen is created for initial setup.
     */
    fun init(
        setupAuthUseCase: SetupAuthUseCase,
        updatePasswordUseCase: UpdatePasswordUseCase,
        verifyPasswordUseCase: VerifyPasswordUseCase,
        isSetup: Boolean
    ) {
        if (isInitialized) {
            return
        }

        this.setupAuthUseCase = setupAuthUseCase
        this.updatePasswordUseCase = updatePasswordUseCase
        this.verifyPasswordUseCase = verifyPasswordUseCase
        this.isSetup = isSetup
        isInitialized = true
    }


    /**
     * Saves the password entered by the user. If the data is not valid, "isOldPasswordValid",
     * "isRepeatNewPasswordValid" and "isDataValid" are updated accordingly.
     */
    fun save() {
        val isOldPasswordValid: Boolean = verifyPasswordUseCase.isPasswordValid(oldPassword) || isSetup
        val areNewPasswordsIdentical: Boolean = newPassword == repeatNewPassword && newPassword.isNotBlank()
        this@PasswordViewModel.isOldPasswordValid = isOldPasswordValid
        this@PasswordViewModel.isRepeatNewPasswordValid = areNewPasswordsIdentical

        if (isOldPasswordValid && areNewPasswordsIdentical) {
            if (isSetup) {
                setupAuthUseCase.setup(newPassword)
            }
            else {
                updatePasswordUseCase.updatePassword(newPassword, oldPassword)
            }
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
