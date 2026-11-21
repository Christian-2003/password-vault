package de.christian2003.passwordvault.presentation.viewmodels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.core.common.application.usecases.GetImageLoaderUseCase
import de.christian2003.core.common.application.usecases.RequestImageUseCase
import de.christian2003.core.ui.theme.ThemeContrast
import de.christian2003.passwordvault.application.GetOtherSoftwareUseCase
import de.christian2003.passwordvault.domain.entities.OtherSoftware
import de.christian2003.passwordvault.presentation.models.dialogs.SettingsScreenDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * View model for the settings screen.
 *
 * @param application               Application.
 * @param getOtherSoftwareUseCase   Use case to get a list of other software to advertise in the app.
 */
@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    application: Application,
    getOtherSoftwareUseCase: GetOtherSoftwareUseCase,
    getImageLoaderUseCase: GetImageLoaderUseCase,
    private val requestImageUseCase: RequestImageUseCase
): AndroidViewModel(application) {

    /**
     * Shared preferences for regular settings.
     */
    private val preferences: SharedPreferences = application.getSharedPreferences("settings", Context.MODE_PRIVATE)

    /**
     * Image loader used to load images from web.
     */
    val imageLoader: ImageLoader = getImageLoaderUseCase.getImageLoader()

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
     * Other software to advertise in Password Vault.
     */
    val otherSoftware: MutableList<OtherSoftware> = mutableStateListOf()


    /**
     * Initializes the view model.
     */
    init {
        viewModelScope.launch(Dispatchers.IO) {
            val otherSoftware: List<OtherSoftware> = getOtherSoftwareUseCase.getOtherSoftware()
            this@SettingsViewModel.otherSoftware.clear()
            this@SettingsViewModel.otherSoftware.addAll(otherSoftware)
        }
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


    /**
     * Requests the image at the specified URL.
     *
     * @param url   URL of the image to request.
     * @return      Image request.
     */
    fun requestImage(url: Uri): ImageRequest {
        return requestImageUseCase.requestImage(url)
    }

}
