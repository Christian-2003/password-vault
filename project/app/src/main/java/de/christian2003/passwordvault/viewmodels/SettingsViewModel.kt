package de.christian2003.passwordvault.viewmodels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.core.ui.theme.ThemeContrast
import de.christian2003.passwordvault.models.dialogs.SettingsScreenDialog
import javax.inject.Inject


/**
 * View model for the settings screen.
 *
 * @param application   Application.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
): AndroidViewModel(application) {

    /**
     * Shared preferences for regular settings.
     */
    private val preferences: SharedPreferences = application.getSharedPreferences("settings", Context.MODE_PRIVATE)

    var dialog: SettingsScreenDialog by mutableStateOf(SettingsScreenDialog.None)

    /**
     * Indicates whether to use global theme.
     */
    var useGlobalTheme: Boolean by mutableStateOf(preferences.getBoolean("global_theme", false))
        private set

    /**
     * Contrast for the theme colors.
     */
    var themeContrast: ThemeContrast by mutableStateOf(ThemeContrast.entries[preferences.getInt("theme_contrast", 0)])
        private set


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


    /**
     * Updates the theme contrast.
     *
     * @param themeContrast Theme contrast.
     */
    fun updateThemeContrast(themeContrast: ThemeContrast) {
        preferences.edit {
            putInt("theme_contrast", themeContrast.ordinal)
        }
        this.themeContrast = themeContrast
    }

}
