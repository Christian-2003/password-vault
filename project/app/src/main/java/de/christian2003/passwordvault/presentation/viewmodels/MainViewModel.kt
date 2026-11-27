package de.christian2003.passwordvault.presentation.viewmodels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.core.ui.theme.ThemeContrast
import de.christian2003.passwordvault.application.IsNewLegalPageAvailableUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * View model for the MainActivity.
 *
 * @param application                       Application.
 * @param isNewLegalPageAvailableUseCase    Use case to check whether a new legal page is available.
 */
@HiltViewModel
internal class MainViewModel @Inject constructor(
    application: Application,
    isNewLegalPageAvailableUseCase: IsNewLegalPageAvailableUseCase
): AndroidViewModel(application) {

    /**
     * Shared preferences.
     */
    private val preferences: SharedPreferences = application.getSharedPreferences("settings", Context.MODE_PRIVATE)

    /**
     * Whether screenshots are allowed / enabled.
     */
    var enableScreenshots: Boolean by mutableStateOf(preferences.getBoolean("enable_screenshots", false))
        private set

    /**
     * Whether the app shall use global theme colors.
     */
    var useGlobalTheme: Boolean by mutableStateOf(preferences.getBoolean("global_theme", false))
        private set

    /**
     * App's theme contrast.
     */
    var themeContrast: ThemeContrast by mutableStateOf(ThemeContrast.entries[preferences.getInt("theme_contrast", 0)])
            private set

    /**
     * Whether an updated legal page is available which users need to accept.
     */
    var isNewLegalPageAvailable: Boolean by mutableStateOf(false)
        private set


    /**
     * Initializes the view model.
     */
    init {
        viewModelScope.launch(Dispatchers.IO) {
            //Check for updated legal pages:
            val isPrivacyPolicyUpdated: Boolean = isNewLegalPageAvailableUseCase.isNewPrivacyPolicyAvailable()
            val isTermsOfServiceUpdated: Boolean = isNewLegalPageAvailableUseCase.isNewTermsOfServiceAvailable()
            isNewLegalPageAvailable = isPrivacyPolicyUpdated || isTermsOfServiceUpdated
        }
    }


    /**
     * Informs the VM about an updated "enable screenshots".
     */
    fun onEnableScreenshotsUpdated() {
        enableScreenshots = preferences.getBoolean("enable_screenshots", false)
    }


    /**
     * Informs the VM about an updated global theme.
     */
    fun onUseGlobalThemeUpdated() {
        useGlobalTheme = preferences.getBoolean("global_theme", false)
    }


    /**
     * Informs the VM about an updated theme contrast.
     */
    fun onThemeContrastUpdated() {
        themeContrast = ThemeContrast.entries[preferences.getInt("theme_contrast", 0)]
    }

}
