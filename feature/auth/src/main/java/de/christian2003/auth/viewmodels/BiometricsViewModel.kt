package de.christian2003.auth.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.application
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.auth.models.states.BiometricsScreenState
import de.christian2003.auth.navigation.BiometricsDestination
import de.christian2003.ui.model.HelpCard
import javax.inject.Inject


@HiltViewModel
class BiometricsViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle
): AndroidViewModel(application) {

    val state: BiometricsScreenState = savedStateHandle.toRoute<BiometricsDestination>().state

    var isHelpCardVisible: Boolean by mutableStateOf(HelpCard.HelpBiometrics.getVisible(application))
        private set


    fun dismissHelpCard() {
        isHelpCardVisible = false
        HelpCard.HelpBiometrics.setVisible(application, false)
    }

}
