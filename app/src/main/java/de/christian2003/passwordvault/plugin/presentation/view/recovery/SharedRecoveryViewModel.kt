package de.christian2003.passwordvault.plugin.presentation.view.recovery

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.passwordvault.domain.security.auth.SecurityQuestion
import javax.inject.Inject


/**
 * View model that is shared between the RecoveryScreen and PasswordScreen in order to transmit the
 * answered security questions to the PasswordScreen for identification.
 */
@HiltViewModel
class SharedRecoveryViewModel @Inject constructor(): ViewModel() {

    /**
     * Map of the security questions and their answers that are provided by the user for identification.
     */
    var securityQuestions: Map<SecurityQuestion, CharArray>? = null


    /**
     * Clears the data stored. This must be called before the screen is dismissed so that sensitive
     * data does not remain in the heap.
     */
    fun clearData() {
        securityQuestions?.forEach { (_, answer) ->
            answer.fill('\u0000')
        }
    }

}
