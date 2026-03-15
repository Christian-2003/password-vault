package de.christian2003.feature.files.viewmodels

import android.app.Application
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.core.common.application.services.DateTimeFormatterService
import de.christian2003.core.common.application.services.StorageUnitFormatterService
import de.christian2003.data.files.application.services.InternalDirectoryNameValidatorService
import de.christian2003.data.files.application.usecases.CreateInternalDirectoryUseCase
import de.christian2003.data.files.application.usecases.DeleteInternalDirectoryUseCase
import de.christian2003.data.files.application.usecases.GetInternalFilesInDirectoryUseCase
import de.christian2003.data.files.application.usecases.GetInternalSubDirectoriesUseCase
import de.christian2003.data.files.application.usecases.ImportExternalFileUseCase
import de.christian2003.data.files.application.usecases.RenameInternalDirectoryUseCase
import de.christian2003.data.files.domain.entities.InternalDirectory
import de.christian2003.data.files.domain.entities.InternalFile
import de.christian2003.feature.files.models.dialog.DirectoryScreenDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject


@HiltViewModel
internal class DirectoryViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    getInternalSubDirectoriesUseCase: GetInternalSubDirectoriesUseCase,
    getInternalFilesInDirectoryUseCase: GetInternalFilesInDirectoryUseCase,
    private val createInternalDirectoryUseCase: CreateInternalDirectoryUseCase,
    private val renameInternalDirectoryUseCase: RenameInternalDirectoryUseCase,
    private val deleteInternalDirectoryUseCase: DeleteInternalDirectoryUseCase,
    private val importExternalFileUseCase: ImportExternalFileUseCase,
    private val directoryNameValidatorService: InternalDirectoryNameValidatorService,
    private val storageUnitFormatterService: StorageUnitFormatterService,
    private val dateTimeFormatterService: DateTimeFormatterService
): AndroidViewModel(application) {

    val directory: InternalDirectory = InternalDirectory(savedStateHandle["internalDirectoryPath"] ?: "") //TODO

    val subDirectories: Flow<List<InternalDirectory>> = getInternalSubDirectoriesUseCase.getSubDirectories(directory)

    val files: Flow<List<InternalFile>> = getInternalFilesInDirectoryUseCase.getInternalFiles(directory)

    var dialog: DirectoryScreenDialog by mutableStateOf(DirectoryScreenDialog.None)
        private set

    var directoryToEdit: InternalDirectory? = null
        private set


    fun importFile(result: ActivityResult) = viewModelScope.launch(Dispatchers.IO) {
        if (result.data != null && result.data!!.data != null) {
            val externalFileUri: Uri = result.data!!.data!!
            importExternalFileUseCase.importExternalFile(externalFileUri, directory)
        }
    }


    fun formatBytes(bytes: Long): String {
        return storageUnitFormatterService.formatSize(bytes)
    }

    fun formateDateTime(dateTime: LocalDateTime): String {
        return dateTimeFormatterService.format(dateTime)
    }


    fun createNewDirectory() {
        dialog = DirectoryScreenDialog.CreateSubDirectory
    }

    fun editDirectory(internalDirectory: InternalDirectory) {
        directoryToEdit = internalDirectory
        dialog = DirectoryScreenDialog.EditSubDirectory
    }

    fun dismissEditDirectoryDialog(directoryName: String? = null) {
        dialog = DirectoryScreenDialog.None
        val directoryToEdit: InternalDirectory? = this.directoryToEdit
        when {
            directoryName != null && directoryToEdit != null -> {
                renameInternalDirectoryUseCase.rename(directoryToEdit, directoryName)
            }
            directoryName != null -> {
                createInternalDirectoryUseCase.create(directoryName, directory)
            }
        }
        this.directoryToEdit = null
    }


    fun isDirectoryNameValid(directoryName: String): Boolean {
        return directoryNameValidatorService.isValid(directoryName)
    }


    fun deleteDirectory(directory: InternalDirectory) {
        deleteInternalDirectoryUseCase.delete(directory)
    }

}
