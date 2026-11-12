package de.christian2003.feature.export.presentation.viewmodels

import android.app.Application
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
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
import de.christian2003.feature.export.application.usecases.DiscoverImportServicesUseCase
import de.christian2003.feature.export.domain.entities.ImportDescriptor
import de.christian2003.feature.export.domain.entities.ImportOptions
import de.christian2003.feature.export.domain.entities.ProgressState
import de.christian2003.feature.export.domain.entities.ServiceProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
internal class ImportViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    discoverImportServicesUseCase: DiscoverImportServicesUseCase
): AndroidViewModel(application) {

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
    var importOption: ImportOptions by mutableStateOf(ImportOptions.DeleteAllExistingData)

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
            if (uri.scheme == "content") {
                val cursor: Cursor? = application.contentResolver.query(uri, null, null, null, null)
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (index != -1) {
                            fileName = cursor.getString(index)
                        }
                    }
                    cursor.close()
                }
            }
            if (fileName == null) {
                fileName = uri.lastPathSegment
            }
        }

        return@derivedStateOf fileName
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


    fun updateImportFileUri(uri: Uri?) {
        if (uri != null) {
            this.importFileUri = uri
            //TODO: Start preflight!
        }
    }


    /**
     * Tries to begin importing the selected file.
     * Starts the import only if all data provided is valid. Otherwise, the corresponding InputError
     * is set.
     */
    fun import() = viewModelScope.launch(Dispatchers.IO) {

    }

}
