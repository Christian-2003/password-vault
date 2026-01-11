package de.christian2003.auth.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import de.christian2003.auth.models.password.PasswordScreenState
import de.christian2003.ui.model.HelpCard
import javax.inject.Inject


class PasswordViewModel @Inject constructor(
    application: Application
): AndroidViewModel(application) {

    val state: PasswordScreenState = PasswordScreenState.FirstTimeSetup

    var isHelpCardVisible: Boolean by mutableStateOf(HelpCard.HelpSetupMasterPassword.getVisible(application))
        private set


    fun dismissHelpCard() {
        isHelpCardVisible = false
        HelpCard.HelpSetupMasterPassword.setVisible(application, false)
    }

}
