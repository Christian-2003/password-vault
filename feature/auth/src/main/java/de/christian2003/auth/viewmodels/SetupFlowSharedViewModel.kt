package de.christian2003.auth.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.auth.models.states.FinishScreenState
import de.christian2003.core.security.application.usecases.SaveChangePasswordSessionUseCase
import de.christian2003.core.security.application.usecases.SaveEnableBiometricsSessionUseCase
import de.christian2003.core.security.application.usecases.SaveFirstTimeSetupSessionUseCase
import de.christian2003.core.security.application.usecases.SaveGenerateNewRecoveryCodesSessionUseCase
import de.christian2003.core.security.application.usecases.SaveRecoverySessionUseCase
import de.christian2003.core.security.domain.entities.ChangePasswordSession
import de.christian2003.core.security.domain.entities.EnableBiometricsSession
import de.christian2003.core.security.domain.entities.FirstTimeSetupSession
import de.christian2003.core.security.domain.entities.GenerateNewRecoveryCodesSession
import de.christian2003.core.security.domain.entities.RecoverySession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * View model for the screen through which flows to change auth data are finished.
 *
 * @param saveFirstTimeSetupSessionUseCase              Use case to save the first-time setup data.
 * @param saveRecoverySessionUseCase                    Use case to save the data after master password recovery.
 * @param saveChangePasswordSession                     Use case to save the data after setting new master password.
 * @param saveGenerateNewRecoveryCodesSessionUseCase    Use case to save data after generating new recovery codes.
 * @param saveEnableBiometricsSessionUseCase            Use case to save data after enabling biometrics.
 */
@HiltViewModel
internal class SetupFlowSharedViewModel @Inject constructor(
    private val saveFirstTimeSetupSessionUseCase: SaveFirstTimeSetupSessionUseCase,
    private val saveRecoverySessionUseCase: SaveRecoverySessionUseCase,
    private val saveChangePasswordSession: SaveChangePasswordSessionUseCase,
    private val saveGenerateNewRecoveryCodesSessionUseCase: SaveGenerateNewRecoveryCodesSessionUseCase,
    private val saveEnableBiometricsSessionUseCase: SaveEnableBiometricsSessionUseCase
): ViewModel() {

    /**
     * Current master password entered by the user.
     */
    var currentMasterPassword: CharArray? = null

    /**
     * New master password entered by the user.
     */
    var newMasterPassword: CharArray? = null

    /**
     * Recovery code entered by the user.
     */
    var recoveryCode: CharArray? = null

    /**
     * List of generated recovery codes.
     */
    var recoveryCodes: List<CharArray>? = null

    /**
     * Whether to use biometrics.
     */
    var useBiometrics: Boolean? = null

    /**
     * Whether the session data is currently being saved.
     */
    var isSavingSession: Boolean by mutableStateOf(false)
        private set

    /**
     * Whether the session data is saved.
     */
    var isFinishedSavingSession: Boolean by mutableStateOf(false)
        private set


    /**
     * Based on the specified screen state, this method calls the corresponding use case. If some
     * data is missing, an IllegalArgumentException is thrown. Once all data is saved,
     * isFinishedSavingSession is set to true.
     *
     * @param state State of the finish-screen.
     */
    fun save(state: FinishScreenState) = viewModelScope.launch(Dispatchers.Default) {
        if (!isSavingSession && !isFinishedSavingSession) {
            isSavingSession = true

            try {
                when (state) {
                    FinishScreenState.FirstTimeSetup -> saveFirstTimeSetup()
                    FinishScreenState.RecoverPassword -> saveRecovery()
                    FinishScreenState.ChangePassword -> saveChangePassword()
                    FinishScreenState.GenerateNewRecoveryCodes -> saveGenerateNewRecoveryCodes()
                    FinishScreenState.EnableBiometrics -> saveEnableBiometrics()
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
        var session: FirstTimeSetupSession? = null

        try {
            session = FirstTimeSetupSession(
                masterPassword = newMasterPassword ?: CharArray(0),
                recoveryCodes = recoveryCodes ?: listOf(),
                useBiometrics = useBiometrics ?: false
            )
            saveFirstTimeSetupSessionUseCase.save(session)
        }
        finally {
            session?.clear()
        }
    }


    /**
     * Saves the data for the recovery.
     */
    private suspend fun saveRecovery() {
        var session: RecoverySession? = null

        try {
            session = RecoverySession(
                recoveryCode = recoveryCode ?: CharArray(0),
                newMasterPassword = newMasterPassword ?: CharArray(0),
                newRecoveryCodes = recoveryCodes ?: listOf()
            )
            saveRecoverySessionUseCase.save(session)
        }
        finally {
            session?.clear()
        }
    }


    /**
     * Saves the data to change the master password.
     */
    private suspend fun saveChangePassword() {
        var session: ChangePasswordSession? = null

        try {
            session = ChangePasswordSession(
                currentMasterPassword = currentMasterPassword ?: CharArray(0),
                newMasterPassword = newMasterPassword ?: CharArray(0)
            )
            saveChangePasswordSession.save(session)
        }
        finally {
            session?.clear()
        }
    }


    /**
     * Saves the data to generate new recovery codes.
     */
    private suspend fun saveGenerateNewRecoveryCodes() {
        var session: GenerateNewRecoveryCodesSession? = null

        try {
            session = GenerateNewRecoveryCodesSession(
                masterPassword = currentMasterPassword ?: CharArray(0),
                recoveryCodes = recoveryCodes ?: listOf()
            )
            saveGenerateNewRecoveryCodesSessionUseCase.save(session)
        }
        finally {
            session?.clear()
        }
    }


    /**
     * Saves the data to enable biometrics.
     */
    private suspend fun saveEnableBiometrics() {
        var session: EnableBiometricsSession? = null

        try {
            session = EnableBiometricsSession(
                masterPassword = currentMasterPassword ?: CharArray(0)
            )
            saveEnableBiometricsSessionUseCase.save(session)
        }
        finally {
            session?.clear()
        }
    }

}
