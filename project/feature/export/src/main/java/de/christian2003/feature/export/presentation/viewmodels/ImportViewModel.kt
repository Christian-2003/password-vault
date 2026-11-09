package de.christian2003.feature.export.presentation.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.feature.export.application.usecases.DiscoverImportServicesUseCase
import de.christian2003.feature.export.domain.entities.ImportDescriptor
import de.christian2003.feature.export.domain.entities.ImportOptions
import de.christian2003.feature.export.domain.entities.ServiceProgress
import de.christian2003.feature.export.domain.services.ImportService
import javax.inject.Inject


@HiltViewModel
internal class ImportViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    discoverImportServicesUseCase: DiscoverImportServicesUseCase
): AndroidViewModel(application) {

    val importServiceDescriptor: ImportDescriptor

    var importOption: ImportOptions by mutableStateOf(ImportOptions.DeleteAllExistingData)

    var password: String by mutableStateOf("")

    var importProgress: ServiceProgress? by mutableStateOf(null)
        private set


    init {
        //Import descriptor:
        val importServiceId: String? = savedStateHandle["importServiceId"]
        val importServices: Set<ImportDescriptor> = discoverImportServicesUseCase.discoverImportServices()
        importServiceDescriptor = importServices.first { it.id == importServiceId }
    }

}
