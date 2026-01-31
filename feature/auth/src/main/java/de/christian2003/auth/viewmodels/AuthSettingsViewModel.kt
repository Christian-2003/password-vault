package de.christian2003.auth.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.security.application.usecases.AreBiometricsConfiguredUseCase
import de.christian2003.ui.model.ColorGenerator
import javax.inject.Inject


@HiltViewModel
class AuthSettingsViewModel @Inject constructor(
    application: Application,
    areBiometricsConfiguredUseCase: AreBiometricsConfiguredUseCase,
    private val colorGenerator: ColorGenerator
): AndroidViewModel(application) {

    var areBiometricsConfigured: Boolean by mutableStateOf(areBiometricsConfiguredUseCase.areBiometricsConfigured())
        private set


    fun generatePositiveColorFromNegativeColor(negative: Color, darkTheme: Boolean): Color {
        return colorGenerator.generatePositiveColorFromNegativeColor(negative, darkTheme)
    }

}
