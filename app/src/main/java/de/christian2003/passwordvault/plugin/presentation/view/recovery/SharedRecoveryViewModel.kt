package de.christian2003.passwordvault.plugin.presentation.view.recovery

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.passwordvault.domain.security.auth.SecurityQuestion
import javax.inject.Inject


@HiltViewModel
class SharedRecoveryViewModel @Inject constructor(): ViewModel() {

    var securityQuestions: Map<SecurityQuestion, String>? = null

}
