package de.christian2003.auth.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.security.application.usecases.AreBiometricsConfiguredUseCase
import de.christian2003.security.application.usecases.UnlockWithMasterPasswordUseCase
import javax.inject.Inject


/**
 * View model for the screen through which to login to the app.
 *
 * @param application                       Application.
 * @param areBiometricsConfiguredUseCase    Use case to check whether biometrics are configured.
 * @param unlockWithMasterPasswordUseCase   Use case to unlock the master key with the master password.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    application: Application,
    areBiometricsConfiguredUseCase: AreBiometricsConfiguredUseCase,
    private val unlockWithMasterPasswordUseCase: UnlockWithMasterPasswordUseCase
): AndroidViewModel(application) {

    /**
     * Whether to skip the biometrics prompt when the screen is displayed.
     */
    var skipBiometricsPrompt: Boolean = application.getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("skip_biometrics", false)

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
     * Indicates whether the master key is currently being unlocked.
     */
    var isUnlockingMasterKey: Boolean by mutableStateOf(false)


    /**
     * Verifies the password entered by the user.
     */
    suspend fun unlockMasterKey() {
        if (!isUnlockingMasterKey) {
            isUnlockingMasterKey = true
            isPasswordValid = try {
                unlockWithMasterPasswordUseCase.unlock(password.toCharArray())
            } catch (e: Exception) {
                Log.e("Login", "Cannot unlock master key: ${e.message ?: "Unknown error"}")
                false
            }
            isUnlockingMasterKey = false
        }
    }

}
