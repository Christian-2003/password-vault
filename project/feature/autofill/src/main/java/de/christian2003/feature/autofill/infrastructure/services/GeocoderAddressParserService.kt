package de.christian2003.feature.autofill.infrastructure.services

import android.location.Address
import android.location.Geocoder
import de.christian2003.feature.autofill.domain.AddressParserService
import de.christian2003.feature.autofill.domain.entities.AutofillType
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject


/**
 * Implementation for the service to parse an address into individual parts. The service uses
 * the Android Geocoder API for parsing.
 *
 * @param geocoder  Android geocoder used for parsing.
 */
internal class GeocoderAddressParserService @Inject constructor(
    private val geocoder: Geocoder
): AddressParserService {

    /**
     * Parses the specified full address into individual parts.
     *
     * @param fullAddress   Full address to parse.
     * @return              Individual parts of the address parsed into individual parts that are
     *                      mapped to their corresponding autofill type.
     */
    override suspend fun parseAddressToParts(fullAddress: String): Map<AutofillType, String> {
        if (!Geocoder.isPresent()) {
            return emptyMap()
        }

        val address: Address? = geocode(fullAddress)
        if (address == null) {
            return emptyMap()
        }


        val result: Map<AutofillType, String> = buildMap {
            //Postal code:
            address.postalCode?.let {
                put(AutofillType.PostalCode, it)
            }

            //Postal address:
            put(AutofillType.PostalAddress, fullAddress)

            //Auxiliary details:
            val auxiliary = listOfNotNull(
                address.subLocality,
                address.premises,
                address.featureName
            ).joinToString(", ")
            if (auxiliary.isNotBlank()) {
                put(AutofillType.AddressAuxiliaryDetails, auxiliary)
            }

            //Country:
            address.countryName?.let {
                put(AutofillType.AddressCountry, it)
            }

            //Locality:
            address.locality?.let {
                put(AutofillType.AddressLocality, it)
            }

            //Region:
            address.adminArea?.let {
                put(AutofillType.AddressRegion, it)
            }

            //Street (including house number):
            val street = listOfNotNull(address.thoroughfare, address.subThoroughfare).joinToString(" ")
            if (street.isNotBlank()) {
                put(AutofillType.AddressStreet, street)
            }
        }
        return result
    }


    /**
     * Geocodes an address into individual parts. If the address cannot be parsed (or an other issue
     * occurs), null is returned.
     *
     * @param fullAddress   Full address to split.
     * @return              Split address or null.
     */
    private suspend fun geocode(fullAddress: String): Address? = suspendCancellableCoroutine { cancellableContinuation ->
        val listener: Geocoder.GeocodeListener = object : Geocoder.GeocodeListener {
            override fun onGeocode(addresses: List<Address?>) {
                cancellableContinuation.resume(
                    value = addresses.firstOrNull(),
                    onCancellation = null
                )
            }

            override fun onError(errorMessage: String?) {
                cancellableContinuation.resume(
                    value = null,
                    onCancellation = null
                )
            }
        }

        geocoder.getFromLocationName(
            fullAddress,
            1,
            listener
        )
    }

}
