package de.christian2003.passwordvault.plugin.presentation.view.devsettings

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


/**
 * View model for the developer settings.
 *
 * @param application   Application.
 */
@HiltViewModel
class DevSettingsViewModel @Inject constructor(
    application: Application
): AndroidViewModel(application) {

    /**
     * Shared preferences for regular settings.
     */
    private val preferences: SharedPreferences = application.getSharedPreferences("settings", Context.MODE_PRIVATE)


    /**
     * Whether to skip the biometrics dialog when the app opens.
     */
    var isSkipBiometrics: Boolean by mutableStateOf(preferences.getBoolean("skip_biometrics", false))
        private set


    /**
     * Deletes the master password.
     */
    fun deleteMasterPassword() {
        val securityPreferences: SharedPreferences = application.getSharedPreferences("security", Context.MODE_PRIVATE)
        securityPreferences.edit {
            remove("password_hash")
            remove("password_salt")
            remove("use_biometrics")
        }
    }


    /**
     * Sets whether to skip the biometrics dialog when the app opens.
     */
    fun setIsSkipBiometrics(isSkipBiometrics: Boolean) {
        preferences.edit {
            putBoolean("skip_biometrics", isSkipBiometrics)
        }
        this.isSkipBiometrics = isSkipBiometrics
    }

}
