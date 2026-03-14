package de.christian2003.feature.files.viewmodels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.data.files.application.usecases.CreateInternalDirectoryUseCase
import de.christian2003.data.files.application.usecases.DeleteInternalDirectoryUseCase
import de.christian2003.data.files.application.usecases.GetInternalSubDirectoriesUseCase
import de.christian2003.data.files.domain.entities.InternalDirectory
import de.christian2003.feature.files.models.dialog.DirectoryScreenDialog
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@HiltViewModel
internal class DirectoryViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    getInternalSubDirectoriesUseCase: GetInternalSubDirectoriesUseCase,
    private val createInternalDirectoryUseCase: CreateInternalDirectoryUseCase,
    private val deleteInternalDirectoryUseCase: DeleteInternalDirectoryUseCase
): AndroidViewModel(application) {

    val directory: InternalDirectory = InternalDirectory(savedStateHandle["internalDirectoryPath"] ?: "") //TODO

    val subDirectories: Flow<List<InternalDirectory>> = getInternalSubDirectoriesUseCase.getSubDirectories(directory)

    var dialog: DirectoryScreenDialog by mutableStateOf(DirectoryScreenDialog.None)


    fun dismissCreateDirectoryDialog(directoryName: String? = null) {
        dialog = DirectoryScreenDialog.None
        if (directoryName != null) {
            createInternalDirectoryUseCase.create(directoryName, directory)
            Log.d("Files", "Start creation of ${directoryName} from ViewModel")
        }
    }


    fun deleteDirectory(directory: InternalDirectory) {
        deleteInternalDirectoryUseCase.delete(directory)
    }

}
