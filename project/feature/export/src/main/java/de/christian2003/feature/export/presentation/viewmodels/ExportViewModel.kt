package de.christian2003.feature.export.presentation.viewmodels

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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


@HiltViewModel
internal class ExportViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    discoverExportServicesUseCase: DiscoverExportServicesUseCase,
    private val getAllAccountDescriptorsUseCase: GetAllAccountDescriptorsUseCase,
    private val getAllInternalFilesUseCase: GetAllInternalFilesUseCase,
    private val launchExportUseCase: LaunchExportUseCase,
    private val dateTimeFormatterService: DateTimeFormatterService,
    private val fileNameValidatorService: FileNameValidatorService
): AndroidViewModel(application) {

    val exportServiceDescriptor: ExportDescriptor

    var directoryUri: Uri? by mutableStateOf(null)

    var fileName: String by mutableStateOf("")

    var password: String by mutableStateOf("")

    var repeatPassword: String by mutableStateOf("")

    var isDirectoryUriValid: Boolean by mutableStateOf(true)
        private set

    var isFileNameValid: Boolean by mutableStateOf(true)
        private set

    var isPasswordValid: Boolean by mutableStateOf(true)
        private set

    var isRepeatPasswordValid: Boolean by mutableStateOf(true)
        private set

    var exportProgress: Float by mutableFloatStateOf(0.0f)


    init {
        //Export descriptor:
        val exportServiceId: String? = savedStateHandle["exportServiceId"]
        val exportServices: Set<ExportDescriptor> = discoverExportServicesUseCase.discoverExportServices()
        exportServiceDescriptor = exportServices.first { it.id == exportServiceId }

        //Export file name:
        val formattedDate: String = dateTimeFormatterService.format(LocalDate.now())
        val fileExtension: String = exportServiceDescriptor.exportFileExtension
        fileName = application.getString(R.string.export_templateFilename, formattedDate, fileExtension)
    }


    fun export() = viewModelScope.launch(Dispatchers.IO) {
        val directoryUri: Uri? = this@ExportViewModel.directoryUri
        val fileName: String = this@ExportViewModel.fileName
        val password: String = this@ExportViewModel.password
        val repeatPassword: String = this@ExportViewModel.repeatPassword

        //Check data validity:
        isDirectoryUriValid = directoryUri != null
        isFileNameValid = fileName.isNotBlank() && fileNameValidatorService.isValid(fileName)
        isPasswordValid = password.isNotBlank()
        isRepeatPasswordValid = repeatPassword.isNotBlank() && password == repeatPassword

        //Abort export if data is invalid:
        if (!isDirectoryUriValid || !isFileNameValid || (exportServiceDescriptor.isExportEncrypted && (!isPasswordValid || !isRepeatPasswordValid))) {
            return@launch
        }

        //Begin export
        val uri: Uri? = getFileUri(directoryUri!!, fileName)
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
            progress.collect { (progress, _) -> exportProgress = progress }
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
