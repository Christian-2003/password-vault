package de.christian2003.feature.export.application.usecases

import de.christian2003.feature.export.domain.entities.ExportDescriptor
import de.christian2003.feature.export.domain.services.ExportService
import javax.inject.Inject


/**
 * Use case to discover all available export services.
 *
 * @param services  Set of all available export services.
 */
internal class DiscoverExportServicesUseCase @Inject constructor(
    private val services: Set<@JvmSuppressWildcards ExportService>
) {

    /**
     * Discovers the available export services and returns their descriptor.
     *
     * @return  Set containing all export descriptors.
     */
    fun discoverExportServices(): Set<ExportDescriptor> {
        return services.map { it.exportDescriptor }.toSet()
    }

}
