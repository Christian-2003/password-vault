package de.christian2003.auth.viewmodels

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.auth.models.recoverycodes.RecoveryCodesFormatter
import de.christian2003.auth.models.recoverycodes.RecoveryCodesScreenDialog
import de.christian2003.security.application.usecases.SetupNewRecoveryCodesUseCase
import de.christian2003.security.domain.entities.RecoveryCodes
import de.christian2003.ui.model.HelpCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * View model for the screen through which new recovery codes are displayed.
 *
 * @param application                   Application.
 * @param setupNewRecoveryCodesUseCase  Use case to generate new recovery codes.
 */
@HiltViewModel
class RecoveryCodesViewModel @Inject constructor(
    application: Application,
    private val setupNewRecoveryCodesUseCase: SetupNewRecoveryCodesUseCase
): AndroidViewModel(application) {

    /**
     * Formatted recovery codes. This is empty while new codes are generating.
     */
    var recoveryCodes: List<String> by mutableStateOf(emptyList())
        private set

    /**
     * Whether an error occurred during generation of new recovery codes.
     */
    var isError: Boolean by mutableStateOf(false)
        private set

    /**
     * Dialog that is currently displayed.
     */
    var dialog: RecoveryCodesScreenDialog by mutableStateOf(RecoveryCodesScreenDialog.None)

    /**
     * Indicates whether the user has downloaded recovery codes to their device.
     */
    var areRecoveryCodesDownloaded: Boolean = false
        private set

    /**
     * Indicates whether the help card is visible.
     */
    var isHelpCardVisible: Boolean by mutableStateOf(HelpCard.HelpRecoveryCodes.getVisible(application))
        private set


    /**
     * Initializes the view model.
     */
    init {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val recoveryCodes: RecoveryCodes = setupNewRecoveryCodesUseCase.setupRecoveryCodes()
                val formatter = RecoveryCodesFormatter()
                val formattedCodes: MutableList<String> = mutableListOf()
                recoveryCodes.codes.forEach { recoveryCode ->
                    formattedCodes.add(formatter.format(recoveryCode))
                }
                this@RecoveryCodesViewModel.recoveryCodes = formattedCodes
                isError = false
            }
            catch (e: Exception) {
                Log.e("RecoveryScreen", e.message ?: "Unknown exception")
                isError = true
            }
        }
    }


    /**
     * Downloads the recovery codes to the file of the designated URI.
     *
     * @param uri   URI of the file to which to download the codes.
     * @return      Whether the codes could be saved to the file successfully.
     */
    suspend fun downloadRecoveryCodesToFile(uri: Uri): Boolean = coroutineScope {
        try {
            if (recoveryCodes.isNotEmpty()) {
                val fileContentBuilder = StringBuilder()
                recoveryCodes.forEach { recoveryCode ->
                    fileContentBuilder.appendLine(recoveryCode)
                }

                application.contentResolver.openOutputStream(uri)?.use { stream ->
                    stream.write(fileContentBuilder.toString().toByteArray())
                    stream.flush()
                }

                areRecoveryCodesDownloaded = true
                return@coroutineScope true
            }
        }
        catch (_: Exception) { }
        return@coroutineScope false
    }


    /**
     * Dismisses the help card.
     */
    fun dismissHelpCard() {
        isHelpCardVisible = false
        HelpCard.HelpRecoveryCodes.setVisible(application, false)
    }

}
