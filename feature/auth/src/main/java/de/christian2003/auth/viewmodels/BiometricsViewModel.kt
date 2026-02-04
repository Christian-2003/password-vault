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
import de.christian2003.core.ui.model.HelpCard
import javax.inject.Inject


/**
 * View model for the screen through which the user can enable biometrics for authentication.
 *
 * @param application       Application.
 * @param savedStateHandle  Saved state handle.
 */
@HiltViewModel
internal class BiometricsViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle
): AndroidViewModel(application) {

    /**
     * State for the screen.
     */
    val state: BiometricsScreenState = savedStateHandle.toRoute<BiometricsDestination>().state

    /**
     * Whether the help card is visible.
     */
    var isHelpCardVisible: Boolean by mutableStateOf(HelpCard.HelpBiometrics.getVisible(application))
        private set


    /**
     * Dismisses the help card.
     */
    fun dismissHelpCard() {
        isHelpCardVisible = false
        HelpCard.HelpBiometrics.setVisible(application, false)
    }

}
