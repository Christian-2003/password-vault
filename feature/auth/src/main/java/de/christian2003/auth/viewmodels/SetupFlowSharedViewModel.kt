package de.christian2003.auth.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.auth.models.states.FinishScreenState
import de.christian2003.security.application.usecases.SaveFirstTimeSetupSessionUseCase
import de.christian2003.security.domain.entities.FirstTimeSetupSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SetupFlowSharedViewModel @Inject constructor(
    private val saveFirstTimeSetupSessionUseCase: SaveFirstTimeSetupSessionUseCase
): ViewModel() {

    var currentMasterPassword: CharArray? = null

    var newMasterPassword: CharArray? = null

    var recoveryCode: CharArray? = null

    var recoveryCodes: List<CharArray>? = null

    var useBiometrics: Boolean? = null

    var isSavingSession: Boolean by mutableStateOf(false)
        private set

    var isFinishedSavingSession: Boolean by mutableStateOf(false)
        private set


    fun save(state: FinishScreenState) = viewModelScope.launch(Dispatchers.Default) {
        if (!isSavingSession && !isFinishedSavingSession) {
            isSavingSession = true

            try {
                when (state) {
                    FinishScreenState.FirstTimeSetup -> saveFirstTimeSetup()
                    else -> { /* TODO: Add saving for other states */ }
                }
            }
            catch (_: Exception) {
            }
            isFinishedSavingSession = true
            isSavingSession = false
        }
    }


    /**
     * Saves the data for the first-time app setup.
     */
    private suspend fun saveFirstTimeSetup() {
        val session = FirstTimeSetupSession(
            masterPassword = newMasterPassword ?: CharArray(0),
            recoveryCodes = recoveryCodes ?: listOf(),
            useBiometrics = useBiometrics ?: false
        )

        try {
            saveFirstTimeSetupSessionUseCase.save(session)
        }
        finally {
            session.clear()
        }
    }

}
