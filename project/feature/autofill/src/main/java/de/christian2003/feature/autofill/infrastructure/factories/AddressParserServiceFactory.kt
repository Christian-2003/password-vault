package de.christian2003.feature.autofill.infrastructure.factories

import android.location.Geocoder
import de.christian2003.core.common.domain.services.ConnectivityCheckerService
import de.christian2003.feature.autofill.domain.repositories.AutofillConfigRepository
import de.christian2003.feature.autofill.domain.services.AddressParserService
import de.christian2003.feature.autofill.infrastructure.services.GeocoderAddressParserService
import de.christian2003.feature.autofill.infrastructure.services.HeuristicAddressParserService
import javax.inject.Inject


/**
 * Factory used to create an address parser service.
 *
 * @param configRepository              Repository for the autofill config.
 * @param connectivityCheckerService    Service to check the network connectivity.
 * @param geocoderAddressParserService  Address parser service using Android Geocoder API.
 * @param heuristicAddressParserService Address parser service using simple heuristics.
 */
internal class AddressParserServiceFactory @Inject constructor(
    private val configRepository: AutofillConfigRepository,
    private val connectivityCheckerService: ConnectivityCheckerService,
    private val geocoderAddressParserService: GeocoderAddressParserService,
    private val heuristicAddressParserService: HeuristicAddressParserService
) {

    /**
     * Creates an address parser service.
     *
     * @return  Address parser service.
     */
    fun create(): AddressParserService {
        return if (Geocoder.isPresent()
            && connectivityCheckerService.hasInternet()
            && configRepository.isGeocoderEnabled()
        ) {
            geocoderAddressParserService
        } else {
            heuristicAddressParserService
        }
    }

}
