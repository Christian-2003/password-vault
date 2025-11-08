package de.christian2003.passwordvault.plugin.presentation.view.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.passwordvault.application.usecases.auth.AreBiometricsAvailableUseCase
import de.christian2003.passwordvault.application.usecases.auth.AreBiometricsConfiguredUseCase
import de.christian2003.passwordvault.application.usecases.auth.ToggleBiometricsUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * View model for the settings screen.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    areBiometricsAvailableUseCase: AreBiometricsAvailableUseCase,
    private val areBiometricsConfiguredUseCase: AreBiometricsConfiguredUseCase
): ViewModel() {

    /**
     * Indicates whether the view model is initialized.
     */
    private var isInitialized: Boolean = false

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
     * Refreshes the property "areBiometricsConfigured".
     */
    fun refreshAreBiometricsConfigured() {
        areBiometricsConfigured = areBiometricsConfiguredUseCase.areBiometricsConfigured()
    }

}
