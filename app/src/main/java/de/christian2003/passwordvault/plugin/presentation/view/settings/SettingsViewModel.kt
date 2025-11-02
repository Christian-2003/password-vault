package de.christian2003.passwordvault.plugin.presentation.view.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.passwordvault.application.usecases.auth.AreBiometricsAvailableUseCase
import de.christian2003.passwordvault.application.usecases.auth.AreBiometricsConfiguredUseCase
import de.christian2003.passwordvault.application.usecases.auth.SetupBiometricsUseCase
import kotlinx.coroutines.launch


/**
 * View model for the settings screen.
 */
class SettingsViewModel: ViewModel() {

    private lateinit var areBiometricsConfiguredUseCase: AreBiometricsConfiguredUseCase
    private lateinit var setupBiometricsUseCase: SetupBiometricsUseCase

    /**
     * Indicates whether the view model is initialized.
     */
    private var isInitialized: Boolean = false

    /**
     * Indicates whether biometrics are supported on the device.
     */
    var areBiometricsAvailable: Boolean = false
        private set

    /**
     * Indicates whether biometrics are configured for the app.
     */
    var areBiometricsConfigured: Boolean by mutableStateOf(false)
        private set


    /**
     * Initializes the view model.
     *
     * @param areBiometricsAvailableUseCase     Use case to determine whether biometrics are supported
     *                                          on the device.
     * @param areBiometricsConfiguredUseCase    Use case to determine whether biometrics are configured
     *                                          for the app.
     * @param setupBiometricsUseCase            Use case to enable / disable biometrics.
     */
    fun init(
        areBiometricsAvailableUseCase: AreBiometricsAvailableUseCase,
        areBiometricsConfiguredUseCase: AreBiometricsConfiguredUseCase,
        setupBiometricsUseCase: SetupBiometricsUseCase
    ) {
        if (isInitialized) {
            return
        }

        this.areBiometricsConfiguredUseCase = areBiometricsConfiguredUseCase
        this.setupBiometricsUseCase = setupBiometricsUseCase
        areBiometricsAvailable = areBiometricsAvailableUseCase.areBiometricsAvailable()
        areBiometricsConfigured = areBiometricsConfiguredUseCase.areBiometricsConfigured()
        isInitialized = true
    }


    /**
     * Enables or disables the biometrics.
     *
     * @param enabled   Whether to enable (= true) or disable (= false) the biometrics.
     */
    fun setBiometrics(enabled: Boolean) = viewModelScope.launch {
        val result: Boolean = if (enabled && !areBiometricsConfigured) {
            //Enable biometrics:
            setupBiometricsUseCase.enableBiometrics()
        } else if (!enabled && areBiometricsConfigured) {
            //Disable biometrics:
            setupBiometricsUseCase.disableBiometrics()
        } else {
            false
        }

        if (result) {
            areBiometricsConfigured = areBiometricsConfiguredUseCase.areBiometricsConfigured()
        }
    }

}
