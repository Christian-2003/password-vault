package de.christian2003.core.common.domain.services


/**
 * Service to check whether the device has internet.
 */
interface ConnectivityCheckerService {

    /**
     * Checks whether the device has internet.
     *
     * @return  Whether the device has internet.
     */
    fun hasInternet(): Boolean

}
