package de.christian2003.feature.files.viewmodels

import android.app.Application
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.core.common.application.services.DateTimeFormatterService
import de.christian2003.core.common.application.services.StorageUnitFormatterService
import de.christian2003.core.ui.model.ColorGenerator
import de.christian2003.data.files.application.services.InternalDirectoryNameValidatorService
import de.christian2003.data.files.application.services.InternalFileNameValidatorService
import de.christian2003.data.files.application.usecases.CreateInternalDirectoryUseCase
import de.christian2003.data.files.application.usecases.DeleteInternalDirectoryUseCase
import de.christian2003.data.files.application.usecases.DeleteInternalFileUseCase
import de.christian2003.data.files.application.usecases.GetInternalFilesInDirectoryUseCase
import de.christian2003.data.files.application.usecases.GetInternalSubDirectoriesUseCase
import de.christian2003.data.files.application.usecases.ImportExternalFileUseCase
import de.christian2003.data.files.application.usecases.IsFileSharedUseCase
import de.christian2003.data.files.application.usecases.PrepareFileForViewingUseCase
import de.christian2003.data.files.application.usecases.RenameInternalDirectoryUseCase
import de.christian2003.data.files.application.usecases.RenameInternalFileUseCase
import de.christian2003.data.files.domain.entities.InternalDirectory
import de.christian2003.data.files.domain.entities.InternalFile
import de.christian2003.data.files.domain.entities.SharedFile
import de.christian2003.feature.files.models.dialog.DirectoryScreenDialog
import de.christian2003.feature.files.models.other.FileType
import de.christian2003.feature.files.models.other.FileTypeMapper
import de.christian2003.feature.files.models.states.DirectoryScreenState
import de.christian2003.feature.files.ui.directory.DirectoryScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Stack
import javax.inject.Inject


@HiltViewModel
internal class DirectoryViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val getInternalSubDirectoriesUseCase: GetInternalSubDirectoriesUseCase,
    private val getInternalFilesInDirectoryUseCase: GetInternalFilesInDirectoryUseCase,
    private val createInternalDirectoryUseCase: CreateInternalDirectoryUseCase,
    private val renameInternalDirectoryUseCase: RenameInternalDirectoryUseCase,
    private val deleteInternalDirectoryUseCase: DeleteInternalDirectoryUseCase,
    private val importExternalFileUseCase: ImportExternalFileUseCase,
    private val deleteInternalFileUseCase: DeleteInternalFileUseCase,
    private val renameInternalFileUseCase: RenameInternalFileUseCase,
    private val prepareFileForViewingUseCase: PrepareFileForViewingUseCase,
    private val isFileSharedUseCase: IsFileSharedUseCase,
    private val directoryNameValidatorService: InternalDirectoryNameValidatorService,
    private val fileNameValidatorService: InternalFileNameValidatorService,
    private val storageUnitFormatterService: StorageUnitFormatterService,
    private val dateTimeFormatterService: DateTimeFormatterService,
    private val fileTypeMapper: FileTypeMapper,
    private val colorGenerator: ColorGenerator
): AndroidViewModel(application) {

    private val navigationStack: Stack<InternalDirectory> = Stack()

    var directory: InternalDirectory by mutableStateOf(InternalDirectory(""))
        private set


    val subDirectories: MutableStateFlow<List<InternalDirectory>> = MutableStateFlow(emptyList())

    val files: MutableStateFlow<List<InternalFile>> = MutableStateFlow(emptyList())

    var dialog: DirectoryScreenDialog by mutableStateOf(DirectoryScreenDialog.None)
        private set

    var state: DirectoryScreenState by mutableStateOf(DirectoryScreenState.Default)
        private set

    val selectedFiles: MutableSet<String> = mutableStateSetOf()

    val selectedSubdirectories: MutableSet<String> = mutableStateSetOf()

    var directoryToEdit: InternalDirectory? = null
        private set

    var directoryToDelete: InternalDirectory? = null
        private set

    var fileToDelete: InternalFile? = null
        private set

    var fileToEdit: InternalFile? = null
        private set

    var fileForDetails: InternalFile? = null
        private set


    init {
        val path: String = savedStateHandle["internalDirectoryPath"] ?: ""
        val directory = InternalDirectory(path)
        navigateToDirectory(directory)
    }


    fun navigateToDirectory(directory: InternalDirectory) {
        navigationStack.push(directory)
        this.directory = directory

        //The use cases need to be called in two separate coroutines. Otherwise, the second flow
        //will not be collected!

        viewModelScope.launch(Dispatchers.IO) {
            getInternalSubDirectoriesUseCase.getSubDirectories(directory).collect {
                subDirectories.value = it
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            getInternalFilesInDirectoryUseCase.getInternalFiles(directory).collect {
                files.value = it
            }
        }
    }


    fun navigateUp(): Boolean {
        if (navigationStack.size < 2) {
            return false //Top page reached
        }
        navigationStack.pop()
        val directory: InternalDirectory = navigationStack.pop() //Pop the page here, because it is pushed in navigateToDirectory()
        navigateToDirectory(directory)
        return true
    }


    fun navigateUpToDirectory(directory: InternalDirectory) {
        if (navigationStack.size < 2) {
            return //Top page reached
        }
        if (navigationStack.contains(directory)) {
            var topDirectory: InternalDirectory

            do {
                topDirectory = navigationStack.pop()
            } while (topDirectory != directory)

            navigateToDirectory(directory)
        }
    }


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


    fun queryFileType(mimeType: String): FileType {
        return fileTypeMapper.mapMimeTypeToFileType(mimeType)
    }

    fun generatePositiveColor(negativeColor: Color, darkTheme: Boolean): Color {
        return colorGenerator.generatePositiveColorFromNegativeColor(negativeColor, darkTheme)
    }


    suspend fun prepareFileForViewing(file: InternalFile): SharedFile? {
        return prepareFileForViewingUseCase.prepare(file, directory)
    }


    fun createNewDirectory() {
        dialog = DirectoryScreenDialog.CreateSubDirectory
    }

    fun deleteDirectory(internalDirectory: InternalDirectory) {
        directoryToDelete = internalDirectory
        dialog = DirectoryScreenDialog.ConfirmDeleteDirectory
    }

    fun editDirectory(internalDirectory: InternalDirectory) {
        directoryToEdit = internalDirectory
        dialog = DirectoryScreenDialog.EditSubDirectory
    }

    fun deleteFile(file: InternalFile) {
        fileToDelete = file
        dialog = DirectoryScreenDialog.ConfirmDeleteFile
    }

    fun renameFile(file: InternalFile) {
        fileToEdit = file
        dialog = DirectoryScreenDialog.RenameFile
    }

    fun moreInfoForFile(file: InternalFile) {
        fileForDetails = file
        dialog = DirectoryScreenDialog.FileDetails
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


    fun dismissDeleteDirectoryDialog(delete: Boolean = false) {
        dialog = DirectoryScreenDialog.None

        val directoryToDelete: InternalDirectory? = this.directoryToDelete
        this.directoryToDelete = null
        if (delete && directoryToDelete != null) {
            deleteInternalDirectoryUseCase.delete(directoryToDelete)
        }
    }


    fun dismissConfirmDeleteFileDialog(delete: Boolean = false) = viewModelScope.launch {
        dialog = DirectoryScreenDialog.None

        val fileToDelete: InternalFile? = this@DirectoryViewModel.fileToDelete
        this@DirectoryViewModel.fileToDelete = null

        if (delete && fileToDelete != null) {
            deleteInternalFileUseCase.delete(fileToDelete, directory)
        }
    }


    fun dismissRenameFileDialog(newFileName: String? = null) = viewModelScope.launch {
        dialog = DirectoryScreenDialog.None

        val fileToEdit: InternalFile? = this@DirectoryViewModel.fileToEdit
        this@DirectoryViewModel.fileToEdit = null

        if (fileToEdit != null && newFileName != null) {
            renameInternalFileUseCase.renameFile(fileToEdit, newFileName)
        }
    }

    fun dismissFileDetailsDialog() {
        dialog = DirectoryScreenDialog.None
        fileForDetails = null
    }

    fun isDirectoryNameValid(directoryName: String): Boolean {
        return directoryNameValidatorService.isValid(directoryName)
    }

    fun isFileNameValid(fileName: String): Boolean {
        return fileNameValidatorService.isValid(fileName)
    }

    fun isFileShared(file: InternalFile): Boolean {
        return isFileSharedUseCase.isShared(file)
    }


    fun startMultiselect(subDirectory: InternalDirectory? = null, file: InternalFile? = null) {
        if (state != DirectoryScreenState.Multiselect) {
            selectedFiles.clear()
            selectedSubdirectories.clear()
            if (subDirectory != null) {
                selectedSubdirectories.add(subDirectory.internalName)
            }
            if (file != null) {
                selectedFiles.add(file.internalName)
            }
            state = DirectoryScreenState.Multiselect
        }
    }

    fun dismissMultiselect() {
        state = DirectoryScreenState.Default
        selectedFiles.clear()
        selectedSubdirectories.clear()
    }

    fun multiselectSelectAll(subDirectories: List<InternalDirectory>, files: List<InternalFile>) {
        subDirectories.forEach { directory ->
            if (!selectedSubdirectories.contains(directory.internalName)) {
                selectedSubdirectories.add(directory.internalName)
            }
        }
        files.forEach { file ->
            if (!selectedFiles.contains(file.internalName)) {
                selectedFiles.add(file.internalName)
            }
        }
    }

    fun multiselectToggleDirectorySelected(directory: InternalDirectory) {
        if (selectedSubdirectories.contains(directory.internalName)) {
            selectedSubdirectories.remove(directory.internalName)
        }
        else {
            selectedSubdirectories.add(directory.internalName)
        }
    }

    fun multiselectToggleFileSelected(file: InternalFile) {
        if (selectedFiles.contains(file.internalName)) {
            selectedFiles.remove(file.internalName)
        }
        else {
            selectedFiles.add(file.internalName)
        }
    }

}
