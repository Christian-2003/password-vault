package de.christian2003.passwordvault.plugin.presentation.view.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import de.christian2003.passwordvault.application.usecases.auth.VerifyPasswordUseCase


/**
 * View model for the screen through which the user confirms their identity before accessing app data.
 */
class LoginViewModel: ViewModel() {

    /**
     * Use case to verify the password entered by the user.
     */
    private lateinit var verifyPasswordUseCase: VerifyPasswordUseCase

    /**
     * Indicates whether the view model is initialized.
     */
    private var isInitialized: Boolean = false

    /**
     * Password entered by the user.
     */
    var password: String by mutableStateOf("")

    /**
     * Indicates whether the password entered by the user is valid.
     */
    var isPasswordValid: Boolean by mutableStateOf(true)


    /**
     * Initializes the view model.
     */
    fun init(
        verifyPasswordUseCase: VerifyPasswordUseCase
    ) {
        if (isInitialized) {
            return
        }

        this.verifyPasswordUseCase = verifyPasswordUseCase
        isInitialized = true
    }


    /**
     * Verifies the password entered by the user.
     */
    fun verifyPassword() {
        isPasswordValid = verifyPasswordUseCase.isPasswordValid(password)
    }

}
