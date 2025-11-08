package de.christian2003.passwordvault.plugin.presentation.view.login

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.passwordvault.application.usecases.auth.AreBiometricsConfiguredUseCase
import de.christian2003.passwordvault.application.usecases.auth.AreSecurityQuestionsConfiguredUseCase
import de.christian2003.passwordvault.application.usecases.auth.BiometricAuthUseCase
import de.christian2003.passwordvault.application.usecases.auth.VerifyPasswordUseCase
import javax.inject.Inject


/**
 * View model for the screen through which the user confirms their identity before accessing app data.
 *
 * @param application                           Application.
 * @param areBiometricsConfiguredUseCase        Use case to determine whether biometric auth is
 *                                              configured.
 * @param areSecurityQuestionsConfiguredUseCase Use case to determine whether the security questions
 *                                              are configured and can be used to recover the master
 *                                              password.
 * @param verifyPasswordUseCase                 Use case to verify the master password.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    application: Application,
    areBiometricsConfiguredUseCase: AreBiometricsConfiguredUseCase,
    areSecurityQuestionsConfiguredUseCase: AreSecurityQuestionsConfiguredUseCase,
    private val verifyPasswordUseCase: VerifyPasswordUseCase,
): AndroidViewModel(application) {

    /**
     * Whether to skip the biometrics prompt when the screen is displayed.
     */
    var skipBiometricsPrompt: Boolean = application.getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("skip_biometrics", false)

    /**
     * Indicates whether the recovery is configured.
     */
    var isRecoveryConfigured: Boolean = areSecurityQuestionsConfiguredUseCase.areSecurityQuestionsConfigured()

    /**
     * Indicates whether biometrics are configured and supported.
     */
    var areBiometricsConfigured: Boolean = areBiometricsConfiguredUseCase.areBiometricsConfigured()

    /**
     * Password entered by the user.
     */
    var password: String by mutableStateOf("")

    /**
     * Indicates whether the password entered by the user is valid.
     */
    var isPasswordValid: Boolean by mutableStateOf(true)


    /**
     * Verifies the password entered by the user.
     */
    fun verifyPassword() {
        isPasswordValid = verifyPasswordUseCase.isPasswordValid(password)
    }

}
