package de.christian2003.core.common.infrastructure.services

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.core.common.domain.services.ConnectivityCheckerService
import javax.inject.Inject


/**
 * Service to check whether the device has internet. This implementation uses the connectivity
 * manager to determine whether internet is available.
 *
 * @param context   Context.
 */
internal class AndroidConnectivityCheckerService @Inject constructor(
    @param:ApplicationContext private val context: Context
): ConnectivityCheckerService {

    /**
     * Checks whether the device has internet.
     *
     * @return  Whether the device has internet.
     */
    override fun hasInternet(): Boolean {
        val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network: Network? = connectivityManager.activeNetwork

        if (network != null) {
            val capabilities: NetworkCapabilities? = connectivityManager.getNetworkCapabilities(network)

            if (capabilities != null) {
                return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            }
        }

        return false
    }

}
