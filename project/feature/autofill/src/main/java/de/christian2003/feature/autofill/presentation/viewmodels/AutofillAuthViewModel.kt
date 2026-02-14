package de.christian2003.feature.autofill.presentation.viewmodels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.core.security.application.usecases.AreBiometricsConfiguredUseCase
import de.christian2003.core.security.application.usecases.UnlockWithMasterPasswordUseCase
import de.christian2003.core.ui.theme.ThemeContrast
import javax.inject.Inject


@HiltViewModel
internal class AutofillAuthViewModel @Inject constructor(
    application: Application,
    areBiometricsConfiguredUseCase: AreBiometricsConfiguredUseCase,
    private val unlockWithMasterPasswordUseCase: UnlockWithMasterPasswordUseCase
) : AndroidViewModel(application) {

    private val preferences: SharedPreferences = application.getSharedPreferences("settings", Context.MODE_PRIVATE)

    val useGlobalTheme: Boolean = preferences.getBoolean("global_theme", false)

    val themeContrast: ThemeContrast = ThemeContrast.entries[preferences.getInt("theme_contrast", 0)]

    val areBiometricsConfigured: Boolean = areBiometricsConfiguredUseCase.areBiometricsConfigured()

    var password: String by mutableStateOf("")

    /**
     * Indicates whether the password entered by the user is valid.
     */
    var isPasswordValid: Boolean by mutableStateOf(true)
        private set

    /**
     * Indicates whether the master key is currently being unlocked.
     */
    var isUnlockingMasterKey: Boolean by mutableStateOf(false)
        private set

    /**
     * Verifies the password entered by the user.
     */
    suspend fun unlockMasterKey() {
        if (!isUnlockingMasterKey) {
            isUnlockingMasterKey = true
            isPasswordValid = try {
                unlockWithMasterPasswordUseCase.unlock(password.toCharArray())
            } catch (_: Exception) {
                false
            }
            isUnlockingMasterKey = false
        }
    }

}
