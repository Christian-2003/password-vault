package de.christian2003.feature.export.application.usecases

import de.christian2003.feature.export.domain.entities.ImportDescriptor
import de.christian2003.feature.export.domain.services.ImportService
import javax.inject.Inject


/**
 * Use case to discover all available import services.
 *
 * @param services  Set of all available import services.
 */
internal class DiscoverImportServicesUseCase @Inject constructor(
    private val services: Set<@JvmSuppressWildcards ImportService>
) {

    /**
     * Discovers the available import services and returns their descriptor.
     *
     * @return  Set containing all import descriptors.
     */
    fun discoverImportServices(): Set<ImportDescriptor> {
        return services.map { it.importDescriptor }.toSet()
    }

}
