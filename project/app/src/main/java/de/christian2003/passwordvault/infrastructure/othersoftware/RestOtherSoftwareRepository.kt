package de.christian2003.passwordvault.infrastructure.othersoftware

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.core.common.domain.services.WebRequestService
import de.christian2003.passwordvault.domain.entities.OtherSoftware
import de.christian2003.passwordvault.domain.repositories.OtherSoftwareRepository
import de.christian2003.passwordvault.infrastructure.othersoftware.dto.SoftwareRootDto
import de.christian2003.passwordvault.infrastructure.othersoftware.mapper.OtherSoftwareRestMapper
import kotlinx.serialization.json.Json
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject


/**
 * Repository accesses other software that should be advertised by Password Vault.
 */
internal class RestOtherSoftwareRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val otherSoftwareMapper: OtherSoftwareRestMapper,
    private val webRequestService: WebRequestService
): OtherSoftwareRepository {

    /**
     * JSON used to deserialize the server response.
     */
    private val json = Json {
        ignoreUnknownKeys = true
    }

    /**
     * URL of the REST API where to fetch info about the other software.
     */
    private val restApiUrl: String = "https://api.christian2003.de/v1/apps"


    /**
     * Caches the deserialized response from the server (with timestamp).
     */
    private var responseCache: Pair<LocalDateTime, List<OtherSoftware>>? = null


    /**
     * Retrieves a list of other software that should be advertised by Password Vault.
     *
     * @return  List of other software.
     */
    override suspend fun getOtherSoftware(): List<OtherSoftware> {
        val responseCache: Pair<LocalDateTime, List<OtherSoftware>>? = this@RestOtherSoftwareRepository.responseCache

        //Check whether cache can be used as return value:
        if (responseCache != null) {
            val duration: Duration = Duration.between(responseCache.first, LocalDateTime.now()).abs()
            val maxDuration: Duration = Duration.ofMinutes(15) //Cache may be 15 minutes old max!

            if (duration < maxDuration) {
                return responseCache.second
            }
        }

        //No cache available or cache too old:
        var software: List<OtherSoftware>? = null

        val response = webRequestService.request(restApiUrl)
        if (response.isSuccessful) {
            val responseJson: String? = response.content

            if (!responseJson.isNullOrBlank()) {
                val responseDto: SoftwareRootDto = json.decodeFromString(responseJson)

                software = responseDto.software
                    .map { otherSoftwareMapper.toDomain(it) }
                    .filter { it.packageName != context.packageName } //Do not advertise Password Vault inside Password Vault
            }
        }

        if (software != null) {
            //Request successful: Update cache:
            this@RestOtherSoftwareRepository.responseCache = Pair<LocalDateTime, List<OtherSoftware>>(
                first = LocalDateTime.now(),
                second = software
            )
            return software
        }
        else {
            //Request unsuccessful: Do not update cache:
            return emptyList()
        }
    }

}
