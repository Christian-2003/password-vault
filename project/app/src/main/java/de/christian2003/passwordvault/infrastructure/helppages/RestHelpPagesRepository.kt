package de.christian2003.passwordvault.infrastructure.helppages

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.core.common.domain.services.WebRequestService
import de.christian2003.passwordvault.domain.entities.HelpPage
import de.christian2003.passwordvault.domain.repositories.HelpPagesRepository
import de.christian2003.passwordvault.infrastructure.helppages.dto.HelpPagesRootDto
import de.christian2003.passwordvault.infrastructure.helppages.mapper.HelpPageRestMapper
import kotlinx.serialization.json.Json
import java.time.Duration
import java.time.LocalDateTime
import java.util.Locale
import javax.inject.Inject


/**
 * Repository through which to access all help pages that are available to users.
 *
 * This implementation uses the REST API of Password Vault to access all help pages that are available.
 */
internal class RestHelpPagesRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val helpPageMapper: HelpPageRestMapper,
    private val webRequestService: WebRequestService
): HelpPagesRepository {

    /**
     * JSON used to deserialize the server response.
     */
    private val json = Json {
        ignoreUnknownKeys = true
    }

    /**
     * URL of the REST API where to fetch info about the help pages.
     */
    private val restApiUrl: String = "https://api.christian2003.de/v1/passwordvault/help"

    /**
     * Caches the deserialized response from the server (with timestamp).
     */
    private var responseCache: Pair<LocalDateTime, List<HelpPage>>? = null


    /**
     * Retrieves a list of help pages.
     *
     * @return  List of help pages.
     */
    override suspend fun getHelpPages(): List<HelpPage> {
        val responseCache: Pair<LocalDateTime, List<HelpPage>>? = this@RestHelpPagesRepository.responseCache

        //Check whether cache can be used as return value:
        if (responseCache != null) {
            val duration: Duration = Duration.between(responseCache.first, LocalDateTime.now()).abs()
            val maxDuration: Duration = Duration.ofMinutes(15) //Cache may be 15 minutes old max!

            if (duration < maxDuration) {
                return responseCache.second
            }
        }

        //No cache available or cache too old:
        var helpPages: List<HelpPage>? = null

        val response = webRequestService.request(restApiUrl)
        if (response.isSuccessful) {
            val responseJson: String? = response.content

            if (!responseJson.isNullOrBlank()) {
                val responseDto: HelpPagesRootDto = json.decodeFromString(responseJson)

                val packageInfo: PackageInfo = getPasswordVaultPackageInfo()
                val versionCode: Long = packageInfo.longVersionCode
                val appLocale: Locale = context.resources.configuration.locales.get(0)

                helpPages = responseDto.pages
                    .filter { it.minVersion <= versionCode && it.maxVersion >= versionCode }
                    .mapNotNull { helpPageMapper.toDomain(it, appLocale) }
            }
        }

        if (helpPages != null) {
            //Request successful: Update cache:
            this@RestHelpPagesRepository.responseCache = Pair<LocalDateTime, List<HelpPage>>(
                first = LocalDateTime.now(),
                second = helpPages
            )
            return helpPages
        }
        else {
            //Request unsuccessful: Do not update cache:
            return emptyList()
        }
    }


    /**
     * Returns the package info for Password Vault.
     *
     * @return  Package info for Password Vault.
     */
    private fun getPasswordVaultPackageInfo(): PackageInfo {
        val packageName: String = context.packageName
        val packageManager: PackageManager = context.packageManager
        val flags: PackageManager.PackageInfoFlags = PackageManager.PackageInfoFlags.of(0)

        return packageManager.getPackageInfo(packageName, flags)
    }

}
