package de.christian2003.feature.export.presentation.viewmodels

import android.app.Application
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.core.common.domain.services.FilesystemService
import de.christian2003.core.ui.model.InputError
import de.christian2003.feature.export.application.usecases.DiscoverImportServicesUseCase
import de.christian2003.feature.export.application.usecases.GetImportPropertiesUseCase
import de.christian2003.feature.export.application.usecases.LaunchImportUseCase
import de.christian2003.feature.export.domain.entities.ImportConfig
import de.christian2003.feature.export.domain.entities.ImportDescriptor
import de.christian2003.feature.export.domain.entities.ImportOption
import de.christian2003.feature.export.domain.entities.ImportProperties
import de.christian2003.feature.export.domain.entities.ProgressErrorCode
import de.christian2003.feature.export.domain.entities.ProgressState
import de.christian2003.feature.export.domain.entities.ServiceProgress
import de.christian2003.feature.export.domain.exceptions.CorruptImportFileException
import de.christian2003.feature.export.domain.exceptions.UnsupportedFileFormatException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
internal class ImportViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    discoverImportServicesUseCase: DiscoverImportServicesUseCase,
    private val getImportPropertiesUseCase: GetImportPropertiesUseCase,
    private val launchImportUseCase: LaunchImportUseCase,
    private val filesystemService: FilesystemService
): AndroidViewModel(application) {

    private var importProperties: ImportProperties? by mutableStateOf(null)

    /**
     * Descriptor of the import service.
     */
    val importServiceDescriptor: ImportDescriptor

    /**
     * Array contains the mime types by which to filter the explorer through which users can select
     * a file to import.
     */
    val mimeTypeFilter: Array<String>

    /**
     * Option selected by user to decide what to do with existing data during the import.
     */
    var importOption: ImportOption by mutableStateOf(ImportOption.DeleteAllExistingData)

    /**
     * URI of the file to import. Null indicates that no file was selected so far.
     */
    var importFileUri: Uri? by mutableStateOf(null)
        private set

    /**
     * Name of the file to import. Null indicates that no file was selected so far.
     */
    val importFileName: State<String?> = derivedStateOf {
        val uri: Uri? = importFileUri
        var fileName: String? = null
        if (uri != null) {
            try {
                fileName = filesystemService.getNameWithExtension(uri)
            }
            catch (_: Exception) { }
        }

        return@derivedStateOf fileName
    }

    val isEncrypted: State<Boolean> = derivedStateOf {
        var isEncrypted = false

        val importProperties: ImportProperties? = this.importProperties
        if (importProperties != null) {
            isEncrypted = importProperties.isEncrypted
        }

        return@derivedStateOf isEncrypted
    }


    /**
     * Password entered for decryption (If import is encrypted).
     */
    var password: String by mutableStateOf("")

    /**
     * Progress during current import.
     */
    var importProgress: ServiceProgress? by mutableStateOf(null)
        private set


    var importFileUriError: InputError? by mutableStateOf(null)
        private set

    var passwordError: InputError? by mutableStateOf(null)

    /**
     * Indicates whether a user can try to start an import at the moment.
     */
    val canStartImport: State<Boolean> = derivedStateOf {
        importProgress == null
            || importProgress?.state == ProgressState.Finished
            || importProgress?.state == ProgressState.Failed
            || importProgress?.state == ProgressState.None
    }


    /**
     * Initializes the view model.
     */
    init {
        //Import descriptor:
        val importServiceId: String? = savedStateHandle["importServiceId"]
        val importServices: Set<ImportDescriptor> = discoverImportServicesUseCase.discoverImportServices()
        importServiceDescriptor = importServices.first { it.id == importServiceId }

        //Get mime type filter from service descriptor:
        val mimeTypeMap: MimeTypeMap = MimeTypeMap.getSingleton()
        var hasUnsupportedMimeTypes: Boolean = false
        importServiceDescriptor.importFileMimeTypes.forEach { mimeType ->
            if (!mimeTypeMap.hasMimeType(mimeType)) {
                hasUnsupportedMimeTypes = true
                return@forEach
            }
        }
        mimeTypeFilter = if (hasUnsupportedMimeTypes) {
            //If one of the MIME types is unsupported, we must always use "*/*", because it is
            //unknown which "fallback MIME type" the underlying DocumentsProvider uses. This is not
            //documented by Android, because every device vendor may implement their own
            //DocumentsProvider. Furthermore, multiple different DocumentsProvider may be used at
            //the same time on an Android device. Depending on the underlying filesystem, the MIME
            //types may or may not be stored. Therefore, making ALL files selectable is a suitable
            //fallback:
            arrayOf("*/*")
        } else  {
            importServiceDescriptor.importFileMimeTypes.toTypedArray()
        }
    }


    fun updateImportFileUri(uri: Uri?) = viewModelScope.launch(Dispatchers.IO) {
        if (uri != null) {
            this@ImportViewModel.importFileUri = uri

            var importProperties: ImportProperties? = null
            try {
                importProperties = getImportPropertiesUseCase.getProperties(importServiceDescriptor.id, uri)
                this@ImportViewModel.importFileUriError = null
            }
            catch (e: Exception) {
                when (e) {
                    is UnsupportedFileFormatException -> {
                        this@ImportViewModel.importFileUriError = InputError.UnsupportedFileFormat
                    }
                    is CorruptImportFileException -> {
                        this@ImportViewModel.importFileUriError = InputError.CorruptFileContent
                    }
                    else -> {
                        this@ImportViewModel.importFileUriError = null
                    }
                }
            }

            this@ImportViewModel.importProperties = importProperties
        }
    }


    /**
     * Tries to begin importing the selected file.
     * Starts the import only if all data provided is valid. Otherwise, the corresponding InputError
     * is set.
     */
    fun import() = viewModelScope.launch(Dispatchers.IO) {
        val importFileUri: Uri? = this@ImportViewModel.importFileUri
        val importOption: ImportOption = this@ImportViewModel.importOption
        val password: String = this@ImportViewModel.password
        val isEncrypted: Boolean = this@ImportViewModel.isEncrypted.value

        //Check data validity:
        val importFileUriError: InputError? = when {
            importFileUri == null -> InputError.Blank
            else -> null
        }
        val passwordError: InputError? = when {
            isEncrypted && password.isBlank() -> InputError.Blank
            else -> null
        }

        this@ImportViewModel.importFileUriError = importFileUriError
        this@ImportViewModel.passwordError = passwordError

        //Abort import if data is invalid:
        if (importFileUriError != null || passwordError != null) {
            return@launch
        }

        //Begin import:
        if (importFileUri != null) {
            val progress: Flow<ServiceProgress> = launchImportUseCase.launchImport(
                id = importServiceDescriptor.id,
                config = ImportConfig(
                    accountsImportOption = importOption,
                    importFile = importFileUri,
                    decryptionKeySeed = password.toCharArray()
                )
            )
            progress.collect { serviceProgress ->
                importProgress = serviceProgress
                when (serviceProgress.errorCode) {
                    ProgressErrorCode.InvalidKey -> {
                        this@ImportViewModel.passwordError = InputError.InvalidPassword
                    }
                    ProgressErrorCode.InvalidFileFormat -> {
                        this@ImportViewModel.importFileUriError = InputError.UnsupportedFileFormat
                    }
                    ProgressErrorCode.CorruptFile -> {
                        this@ImportViewModel.importFileUriError = InputError.CorruptFileContent
                    }
                    else -> { /* Nothing to do here... */ }
                }
            }
        }
    }

}
