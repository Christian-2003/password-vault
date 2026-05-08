package de.christian2003.feature.export.presentation.viewmodels

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.core.common.application.services.DateTimeFormatterService
import de.christian2003.core.common.application.services.FileNameValidatorService
import de.christian2003.core.ui.model.InputError
import de.christian2003.data.accounts.application.usecases.GetAccountIconUseCase
import de.christian2003.data.accounts.application.usecases.GetAllAccountDescriptorsUseCase
import de.christian2003.data.accounts.domain.entities.AccountDescriptor
import de.christian2003.data.files.application.usecases.GetAllInternalFilesUseCase
import de.christian2003.data.files.domain.entities.InternalFile
import de.christian2003.feature.export.application.usecases.DiscoverExportServicesUseCase
import de.christian2003.feature.export.application.usecases.LaunchExportUseCase
import de.christian2003.feature.export.domain.entities.ExportConfig
import de.christian2003.feature.export.domain.entities.ExportDescriptor
import de.christian2003.feature.export.domain.entities.ExportProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import kotlin.uuid.Uuid
import de.christian2003.feature.export.R
import de.christian2003.feature.export.application.usecases.ObserveExportProgressUseCase
import de.christian2003.feature.export.domain.entities.ProgressState
import de.christian2003.feature.export.presentation.model.dialogs.ExportScreenDialog


@HiltViewModel
internal class ExportViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    discoverExportServicesUseCase: DiscoverExportServicesUseCase,
    getAllAccountDescriptorsUseCase: GetAllAccountDescriptorsUseCase,
    private val getAllInternalFilesUseCase: GetAllInternalFilesUseCase,
    private val launchExportUseCase: LaunchExportUseCase,
    private val observeExportProgressUseCase: ObserveExportProgressUseCase,
    private val getAccountIconUseCase: GetAccountIconUseCase,
    private val dateTimeFormatterService: DateTimeFormatterService,
    private val fileNameValidatorService: FileNameValidatorService
): AndroidViewModel(application) {

    var accounts: List<AccountDescriptor> by mutableStateOf(emptyList())
        private set

    val exportServiceDescriptor: ExportDescriptor

    var directoryUri: Uri? by mutableStateOf(null)

    var fileName: String by mutableStateOf("")

    var password: String by mutableStateOf("")

    var repeatPassword: String by mutableStateOf("")

    var directoryUriError: InputError? by mutableStateOf(null)
        private set

    var fileNameError: InputError? by mutableStateOf(null)
        private set

    var passwordError: InputError? by mutableStateOf(null)
        private set

    var repeatPasswordError: InputError? by mutableStateOf(null)
        private set

    var exportProgress: ExportProgress? by mutableStateOf(null)

    var canStartExport: State<Boolean> = derivedStateOf {
        exportProgress == null
            || exportProgress?.state == ProgressState.Finished
            || exportProgress?.state == ProgressState.Failed
            || exportProgress?.state == ProgressState.None
    }

    val selectedAccountIds: MutableSet<Uuid> = mutableSetOf()

    var dialog: ExportScreenDialog by mutableStateOf(ExportScreenDialog.None)
        private set


    init {
        //Export descriptor:
        val exportServiceId: String? = savedStateHandle["exportServiceId"]
        val exportServices: Set<ExportDescriptor> = discoverExportServicesUseCase.discoverExportServices()
        exportServiceDescriptor = exportServices.first { it.id == exportServiceId }

        //Export file name:
        val formattedDate: String = dateTimeFormatterService.format(LocalDate.now())
        val fileExtension: String = exportServiceDescriptor.exportFileExtension
        fileName = application.getString(R.string.export_templateFilename, formattedDate, fileExtension)

        //Observe current worker (if any are running):
        if (exportServiceId != null) {
            val currentProgress: Flow<ExportProgress>? = observeExportProgressUseCase.observeProgress(exportServiceId)
            viewModelScope.launch {
                currentProgress?.collect { exportProgress = it }
            }
        }

        //Get all relevant accounts and files:
        viewModelScope.launch(Dispatchers.IO) {
            if (exportServiceDescriptor.canExportAccounts) {
                val accounts: List<AccountDescriptor> = getAllAccountDescriptorsUseCase.getAllAccountDescriptors().first()
                selectedAccountIds.addAll(accounts.map { it.id })
                this@ExportViewModel.accounts = accounts
            }
        }
    }


    fun queryAccountIcon(account: AccountDescriptor): Drawable? {
        return getAccountIconUseCase.getAccountIcon(account)
    }


    fun showSelectAccountsDialog() {
        dialog = ExportScreenDialog.SelectAccounts
    }

    fun dismissSelectAccountsDialog(selectedAccounts: Set<Uuid>? = null) {
        dialog = ExportScreenDialog.None
        if (selectedAccounts != null) {
            selectedAccountIds.clear()
            selectedAccountIds.addAll(selectedAccounts)
        }
    }


    fun showSelectFilesDialog() {
        dialog = ExportScreenDialog.SelectFiles
    }

    fun dismissSelectFilesDialog(selectedFiles: Set<String>? = null) {
        dialog = ExportScreenDialog.None
    }


    fun export() = viewModelScope.launch(Dispatchers.IO) {
        val directoryUri: Uri? = this@ExportViewModel.directoryUri
        val fileName: String = this@ExportViewModel.fileName
        val password: String = this@ExportViewModel.password
        val repeatPassword: String = this@ExportViewModel.repeatPassword

        //Check data validity:
        directoryUriError = when {
            directoryUri == null -> InputError.Blank
            else -> null
        }
        fileNameError = when {
            fileName.isBlank() -> InputError.Blank
            !fileNameValidatorService.isValid(fileName) -> InputError.IllegalFilename
            else -> null
        }
        passwordError = when {
            password.isBlank() -> InputError.Blank
            else -> null
        }
        repeatPasswordError = when {
            repeatPassword.isBlank() -> InputError.Blank
            password != repeatPassword -> InputError.PasswordsNotMatching
            else -> null
        }

        //Abort export if data is invalid:
        if (directoryUriError != null || fileNameError != null || (exportServiceDescriptor.isExportEncrypted && (passwordError != null || repeatPasswordError != null))) {
            return@launch
        }

        //Begin export
        val uri: Uri? = getFileUri(directoryUri!!, fileName)
        if (uri != null) {
            val accountIds: Set<Uuid> = accounts.map { it.id }.toSet()

            val internalFiles: List<InternalFile> = getAllInternalFilesUseCase.getAllInternalFiles().first()
            val internalFileNames: Set<String> = internalFiles.map { it.internalName }.toSet()

            val progress: Flow<ExportProgress> = launchExportUseCase.launchExport(
                id = exportServiceDescriptor.id,
                config = ExportConfig(
                    accounts = accountIds,
                    files = internalFileNames,
                    exportDestination = uri,
                    encryptionKeySeed = password.toCharArray()
                )
            )
            progress.collect { exportProgress = it }
        }
    }


    private fun getFileUri(directoryUri: Uri?, fileName: String): Uri? {
        val context: Context = application
        val fileMimeType: String = exportServiceDescriptor.exportFileMimeType

        if (directoryUri != null && fileName.isNotBlank()) {
            val dir: DocumentFile? = DocumentFile.fromTreeUri(context, directoryUri)
            val file: DocumentFile? = dir?.createFile(fileMimeType, fileName)

            return file?.uri
        }
        return null
    }

}
