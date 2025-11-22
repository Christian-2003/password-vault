package de.christian2003.passwordvault.plugin.presentation.view.settings

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.passwordvault.application.usecases.auth.AreBiometricsAvailableUseCase
import de.christian2003.passwordvault.application.usecases.auth.AreBiometricsConfiguredUseCase
import javax.inject.Inject


/**
 * View model for the settings screen.
 *
 * @param application                       Application.
 * @param areBiometricsAvailableUseCase     Use case to get whether the device supports biometrics.
 * @param areBiometricsConfiguredUseCase    Use case to get whether biometrics are configured.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
    areBiometricsAvailableUseCase: AreBiometricsAvailableUseCase,
    private val areBiometricsConfiguredUseCase: AreBiometricsConfiguredUseCase
): AndroidViewModel(application) {

    /**
     * Shared preferences for regular settings.
     */
    private val preferences: SharedPreferences = application.getSharedPreferences("settings", Context.MODE_PRIVATE)


    /**
     * Indicates whether biometrics are supported on the device.
     */
    val areBiometricsAvailable: Boolean = areBiometricsAvailableUseCase.areBiometricsAvailable()

    /**
     * Indicates whether biometrics are configured for the app.
     */
    var areBiometricsConfigured: Boolean by mutableStateOf(areBiometricsConfiguredUseCase.areBiometricsConfigured())
        private set

    /**
     * Indicates whether to use global theme.
     */
    var useGlobalTheme: Boolean by mutableStateOf(preferences.getBoolean("global_theme", false))
        private set

    /**
     * Refreshes the property "areBiometricsConfigured".
     */
    fun refreshAreBiometricsConfigured() {
        areBiometricsConfigured = areBiometricsConfiguredUseCase.areBiometricsConfigured()
    }


    /**
     * Updates whether to use the global theme.
     *
     * @param useGlobalTheme    Whether to use global theme.
     */
    fun updateUseGlobalTheme(useGlobalTheme: Boolean) {
        preferences.edit {
            putBoolean("global_theme", useGlobalTheme)
        }
        this.useGlobalTheme = useGlobalTheme
    }

}
