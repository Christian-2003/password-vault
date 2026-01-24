package de.christian2003.auth.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.security.application.usecases.AreBiometricsConfiguredUseCase
import javax.inject.Inject


@HiltViewModel
class AuthSettingsViewModel @Inject constructor(
    application: Application,
    private val biometricsConfiguredUseCase: AreBiometricsConfiguredUseCase
): AndroidViewModel(application) {

    var areBiometricsConfigured: Boolean by mutableStateOf(biometricsConfiguredUseCase.areBiometricsConfigured())
        private set

}
