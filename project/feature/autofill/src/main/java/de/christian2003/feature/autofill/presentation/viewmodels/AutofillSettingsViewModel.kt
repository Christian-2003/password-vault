package de.christian2003.feature.autofill.presentation.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.feature.autofill.application.usecases.IsAutofillEnabledUseCase
import de.christian2003.feature.autofill.application.usecases.IsGeocoderEnabledUseCase
import de.christian2003.feature.autofill.application.usecases.IsServiceSelectedUseCase
import de.christian2003.feature.autofill.application.usecases.SetAutofillEnabledUseCase
import de.christian2003.feature.autofill.application.usecases.SetGeocoderEnabledUseCase
import javax.inject.Inject


/**
 * View model for the screen through which autofill settings are displayed.
 *
 * @param application           Application.
 * @param isServiceSelectedUseCase  Use case to determine whether the autofill service is selected by
 *                                  the Android system.
 * @param isAutofillEnabledUseCase  Use case to determine whether the autofill service is enabled.
 * @param setAutofillEnabledUseCase Use case to change whether the autofill service is enabled.
 * @param isGeocoderEnabledUseCase  Use case to determine whether the geocoder is enabled.
 * @param setGeocoderEnabledUseCase Use case to change whether the geocoder is enabled.
 */
@HiltViewModel
internal class AutofillSettingsViewModel @Inject constructor(
    application: Application,
    private val isServiceSelectedUseCase: IsServiceSelectedUseCase,
    private val isAutofillEnabledUseCase: IsAutofillEnabledUseCase,
    private val setAutofillEnabledUseCase: SetAutofillEnabledUseCase,
    private val isGeocoderEnabledUseCase: IsGeocoderEnabledUseCase,
    private val setGeocoderEnabledUseCase: SetGeocoderEnabledUseCase
): AndroidViewModel(application) {

    /**
     * Whether the autofill service is enabled by the user.
     */
    var isAutofillEnabled: Boolean by mutableStateOf(isAutofillEnabledUseCase.isEnabled())
        private set

    /**
     * Whether the autofill service is selected by the Android system.
     */
    var isAutofillSelected: Boolean by mutableStateOf(isServiceSelectedUseCase.isSelected())
        private set

    /**
     * Whether the geocoder is enabled by the user.
     */
    var isGeocoderEnabled: Boolean by mutableStateOf(isGeocoderEnabledUseCase.isEnabled())
        private set


    /**
     * Changes whether the autofill service is enabled by the user.
     *
     * @param isAutofillEnabled Whether the service is enabled.
     */
    fun setIsAutofillEnabled(isAutofillEnabled: Boolean) {
        setAutofillEnabledUseCase.setEnabled(isAutofillEnabled)
        this.isAutofillEnabled = isAutofillEnabledUseCase.isEnabled()
        this.isAutofillSelected = isServiceSelectedUseCase.isSelected()
    }


    /**
     * Changes whether the geocoder is enabled by the user.
     *
     * @param isGeocoderEnabled Whether the geocoder is enabled.
     */
    fun setIsGeocoderEnabled(isGeocoderEnabled: Boolean) {
        setGeocoderEnabledUseCase.setEnabled(isGeocoderEnabled)
        this.isGeocoderEnabled = isGeocoderEnabledUseCase.isEnabled()
    }

}
