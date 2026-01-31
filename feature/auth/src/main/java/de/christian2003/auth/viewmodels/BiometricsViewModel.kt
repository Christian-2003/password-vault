package de.christian2003.auth.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.ui.model.HelpCard
import javax.inject.Inject


@HiltViewModel
class BiometricsViewModel @Inject constructor(
    application: Application
): AndroidViewModel(application) {

    var isHelpCardVisible: Boolean by mutableStateOf(HelpCard.HelpBiometrics.getVisible(application))
        private set


    fun dismissHelpCard() {
        isHelpCardVisible = false
        HelpCard.HelpBiometrics.setVisible(application, false)
    }

}
