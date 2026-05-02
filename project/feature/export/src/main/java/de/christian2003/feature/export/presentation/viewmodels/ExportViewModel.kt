package de.christian2003.feature.export.presentation.viewmodels

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.data.accounts.application.usecases.GetAllAccountDescriptorsUseCase
import de.christian2003.data.accounts.domain.entities.AccountDescriptor
import de.christian2003.data.files.application.usecases.GetAllInternalFilesUseCase
import de.christian2003.data.files.domain.entities.InternalFile
import de.christian2003.feature.export.application.usecases.DiscoverExportServicesUseCase
import de.christian2003.feature.export.application.usecases.LaunchExportUseCase
import de.christian2003.feature.export.domain.entities.ExportConfig
import de.christian2003.feature.export.domain.entities.ExportDescriptor
import de.christian2003.feature.export.domain.entities.ExportProgress
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
    discoverExportServicesUseCase: DiscoverExportServicesUseCase,
    private val getAllAccountDescriptorsUseCase: GetAllAccountDescriptorsUseCase,
    private val getAllInternalFilesUseCase: GetAllInternalFilesUseCase,
    private val launchExportUseCase: LaunchExportUseCase
): AndroidViewModel(application) {

    var exportProgress: Float by mutableFloatStateOf(0.0f)

    var uri: Uri? by mutableStateOf(null)

    var password: String by mutableStateOf("")

    var exportServiceDescriptor: ExportDescriptor = discoverExportServicesUseCase.discoverExportServices().first { it.id == "V3Backup" }


    fun export() = viewModelScope.launch(Dispatchers.IO) {
        val uri: Uri? = this@ExportViewModel.uri
        if (uri != null) {
            val accountDescriptors: List<AccountDescriptor> = getAllAccountDescriptorsUseCase.getAllAccountDescriptors().first()
            val accountIds: Set<Uuid> = accountDescriptors.map { it.id }.toSet()

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
            progress.collect { (progress, state) -> exportProgress = progress }
        }
    }

}
