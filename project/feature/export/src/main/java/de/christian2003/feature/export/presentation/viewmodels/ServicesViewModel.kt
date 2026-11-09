package de.christian2003.feature.export.presentation.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.feature.export.application.usecases.DiscoverExportServicesUseCase
import de.christian2003.feature.export.application.usecases.DiscoverImportServicesUseCase
import de.christian2003.feature.export.presentation.model.dto.ServiceDescriptorUiDto
import javax.inject.Inject


@HiltViewModel
internal class ServicesViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    discoverExportServicesUseCase: DiscoverExportServicesUseCase,
    discoverImportServicesUseCase: DiscoverImportServicesUseCase
): AndroidViewModel(application) {


    val isExport: Boolean = savedStateHandle["isExport"] ?: false

    val serviceDescriptors: List<ServiceDescriptorUiDto>


    init {
        if (isExport) {
            serviceDescriptors = discoverExportServicesUseCase
                .discoverExportServices()
                .sortedBy {
                    application.getString(it.titleId)
                }
                .map { exportDescriptor ->
                    ServiceDescriptorUiDto(
                        id = exportDescriptor.id,
                        serviceMimeTypes = setOf(exportDescriptor.exportFileMimeType),
                        titleId = exportDescriptor.titleId,
                        subtitleId = exportDescriptor.subtitleId
                    )
                }
        }
        else {
            serviceDescriptors = discoverImportServicesUseCase
                .discoverImportServices()
                .sortedBy {
                    application.getString(it.titleId)
                }
                .map { exportDescriptor ->
                    ServiceDescriptorUiDto(
                        id = exportDescriptor.id,
                        serviceMimeTypes = exportDescriptor.importFileMimeTypes,
                        titleId = exportDescriptor.titleId,
                        subtitleId = exportDescriptor.subtitleId
                    )
                }
        }
    }

}
