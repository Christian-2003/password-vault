package de.christian2003.passwordvault.plugin.presentation.view.login

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import de.christian2003.passwordvault.application.usecases.auth.AreBiometricsConfiguredUseCase
import de.christian2003.passwordvault.application.usecases.auth.BiometricAuthUseCase
import de.christian2003.passwordvault.application.usecases.auth.VerifyPasswordUseCase


/**
 * View model for the screen through which the user confirms their identity before accessing app data.
 */
class LoginViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var verifyPasswordUseCase: VerifyPasswordUseCase
    private lateinit var biometricAuthUseCase: BiometricAuthUseCase

    /**
     * Indicates whether the view model is initialized.
     */
    private var isInitialized: Boolean = false

    /**
     * Whether to skip the biometrics prompt when the screen is displayed.
     */
    var skipBiometricsPrompt: Boolean = application.getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("skip_biometrics", false)

    /**
     * Indicates whether biometrics are configured and supported.
     */
    var areBiometricsConfigured: Boolean = false

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
     *
     * @param verifyPasswordUseCase             Use case to verify a password.
     * @param areBiometricsConfiguredUseCase    Use case to determine whether biometrics are configured.
     * @param biometricAuthUseCase              Use case to facilitate biometric authentication.
     */
    fun init(
        verifyPasswordUseCase: VerifyPasswordUseCase,
        areBiometricsConfiguredUseCase: AreBiometricsConfiguredUseCase,
        biometricAuthUseCase: BiometricAuthUseCase
    ) {
        if (isInitialized) {
            return
        }

        this.verifyPasswordUseCase = verifyPasswordUseCase
        this.biometricAuthUseCase = biometricAuthUseCase
        areBiometricsConfigured = areBiometricsConfiguredUseCase.areBiometricsConfigured()
        isInitialized = true
    }


    /**
     * Verifies the password entered by the user.
     */
    fun verifyPassword() {
        isPasswordValid = verifyPasswordUseCase.isPasswordValid(password)
    }


    /**
     * Performs biometric authentication.
     *
     * @return  Whether authentication was successful.
     */
    suspend fun biometricAuthentication(): Boolean {
        return if (areBiometricsConfigured) {
            biometricAuthUseCase.authenticate()
        } else {
            false
        }
    }

}
