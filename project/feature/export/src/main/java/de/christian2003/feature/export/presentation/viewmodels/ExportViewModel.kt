package de.christian2003.feature.export.presentation.viewmodels

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.data.accounts.application.usecases.GetAllAccountDescriptorsUseCase
import de.christian2003.data.accounts.domain.entities.AccountDescriptor
import de.christian2003.data.files.application.usecases.GetAllInternalFilesUseCase
import de.christian2003.data.files.domain.entities.InternalFile
import de.christian2003.feature.export.domain.entities.ExportConfig
import de.christian2003.feature.export.infrastructure.backup.v3.V3BackupService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.uuid.Uuid


@HiltViewModel
internal class ExportViewModel @Inject constructor(
    application: Application,
    private val getAllAccountDescriptorsUseCase: GetAllAccountDescriptorsUseCase,
    private val getAllInternalFilesUseCase: GetAllInternalFilesUseCase,
    private val backupService: V3BackupService //TODO: This is only temporary!
): AndroidViewModel(application) {

    var uri: Uri? by mutableStateOf(null)


    fun export() = viewModelScope.launch(Dispatchers.IO) {
        val uri: Uri? = this@ExportViewModel.uri
        if (uri != null) {
            val accountDescriptors: List<AccountDescriptor> = getAllAccountDescriptorsUseCase.getAllAccountDescriptors().first()
            val accountIds: Set<Uuid> = accountDescriptors.map { it.id }.toSet()

            val internalFiles: List<InternalFile> = getAllInternalFilesUseCase.getAllInternalFiles().first()
            val internalFileNames: Set<String> = internalFiles.map { it.internalName }.toSet()

            val progress: Flow<Float> = backupService.createExport(
                config = ExportConfig(
                    accounts = accountIds,
                    files = internalFileNames,
                    exportDestination = uri
                )
            )
            progress.collect { percentage ->
                //Starts export
            }
        }
    }

}
