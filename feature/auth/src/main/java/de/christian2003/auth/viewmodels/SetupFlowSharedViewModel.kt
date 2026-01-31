package de.christian2003.auth.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.auth.models.states.FinishScreenState
import de.christian2003.security.application.usecases.SaveFirstTimeSetupSessionUseCase
import de.christian2003.security.domain.entities.FirstTimeSetupSession
import kotlinx.serialization.builtins.CharArraySerializer
import javax.inject.Inject


@HiltViewModel
class SetupFlowSharedViewModel @Inject constructor(
    private val saveFirstTimeSetupSessionUseCase: SaveFirstTimeSetupSessionUseCase
): ViewModel() {

    var newMasterPassword: CharArray? = null

    var recoveryCode: CharArray? = null

    var recoveryCodes: List<CharArray>? = null

    var useBiometrics: Boolean? = null

    var isSavingSession: Boolean = false
        private set

    var isFinishedSavingSession: Boolean = false
        private set


    suspend fun save(state: FinishScreenState): Boolean {
        if (!isSavingSession && !isFinishedSavingSession) {
            isSavingSession = true

            val result: Boolean = try {
                when (state) {
                    FinishScreenState.FirstTimeSetup -> saveFirstTimeSetup()
                    else -> { /* TODO: Add saving for other states */ }
                }
                true
            }
            catch (_: Exception) {
                false
            }
            isFinishedSavingSession = true
            isSavingSession = false

            return result
        }
        return false
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
