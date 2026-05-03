package de.christian2003.feature.export.presentation.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.feature.export.application.usecases.DiscoverExportServicesUseCase
import de.christian2003.feature.export.domain.entities.ExportDescriptor
import javax.inject.Inject


@HiltViewModel
internal class ExportsViewModel @Inject constructor(
    application: Application,
    discoverExportServicesUseCase: DiscoverExportServicesUseCase
): AndroidViewModel(application) {

    val exportDescriptors: List<ExportDescriptor> = discoverExportServicesUseCase
        .discoverExportServices()
        .sortedBy {
            application.getString(it.titleId)
        }

}
