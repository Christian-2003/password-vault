package de.christian2003.passwordvault.infrastructure.legal

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.core.common.domain.services.WebRequestService
import de.christian2003.passwordvault.domain.entities.LegalPage
import de.christian2003.passwordvault.domain.repositories.LegalPagesRepository
import de.christian2003.passwordvault.infrastructure.legal.dto.LegalRootRestDto
import de.christian2003.passwordvault.infrastructure.legal.mapper.LegalPageRestMapper
import kotlinx.serialization.json.Json
import java.util.Locale
import javax.inject.Inject


/**
 * Repository through which to access information about the legal pages (i.e. "Privacy Policy" and
 * "Terms of Service") of Password Vault.
 *
 * This implementation uses the REST API to access information about the legal pages.
 * Information about the version of legal documents accepted is stored in SharedPreferences "settings"
 * with keys "privacy_version" and "tos_version". These keys are chosen because prior to v3.8.0, these
 * keys were used as well.
 *
 * @param context           Application context.
 * @param legalPageMapper   Mapper for legal pages.
 * @param webRequestService Service for web requests.
 */
internal class RestLegalPagesRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val legalPageMapper: LegalPageRestMapper,
    private val webRequestService: WebRequestService
): LegalPagesRepository {

    /**
     * JSON used to deserialize the server response.
     */
    private val json = Json {
        ignoreUnknownKeys = true
    }

    /**
     * URL of the REST API where to fetch info about the legal pages.
     */
    private val restApiUrl: String = "https://api.christian2003.de/v1/passwordvault/legal"

    /**
     * Shared preferences. Used to store which version of legal documents was "accepted" by user.
     */
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    /**
     * Caches the privacy policy page. May be null if no page was loaded so far or could not be loaded.
     * Cache is not invalidated if too old. We may assume that PP and TOS do not update frequently.
     */
    private var privacyPolicyCache: LegalPage? = null

    /**
     * Caches the terms of service page. May be null if no page was loaded so far or could not be loaded.
     * Cache is not invalidated if too old. We may assume that PP and TOS do not update frequently.
     */
    private var termsOfServiceCache: LegalPage? = null


    /**
     * Returns the legal page of the privacy policy.
     * If privacy policy cannot be loaded, null may be returned.
     *
     * @return  Privacy policy legal page or null.
     */
    override suspend fun getPrivacyPolicyPage(): LegalPage? {
        if (privacyPolicyCache == null) {
            loadLegalDocuments()
        }
        return privacyPolicyCache
    }


    /**
     * Checks whether a new version of the privacy policy is available.
     *
     * @return  Whether a new version is available.
     */
    override suspend fun isNewPrivacyPolicyAvailable(): Boolean {
        if (privacyPolicyCache == null) {
            loadLegalDocuments()
        }
        val acceptedVersion: Int = sharedPreferences.getInt("privacy_version", -1)
        val onlineVersion: Int = privacyPolicyCache?.version ?: -1

        if (onlineVersion == -1) {
            //Online version cannot be loaded:
            return false
        }
        else {
            return acceptedVersion < onlineVersion
        }
    }


    /**
     * Accepts the current version of the privacy policy.
     */
    override suspend fun acceptPrivacyPolicy() {
        if (privacyPolicyCache == null) {
            loadLegalDocuments()
        }
        val onlineVersion: Int = privacyPolicyCache?.version ?: -1

        if (onlineVersion != -1) {
            //Online version loaded:
            sharedPreferences.edit {
                putInt("privacy_version", onlineVersion)
            }
        }
    }


    /**
     * Returns the legal page of the terms of service.
     * If terms of service cannot be loaded, null may be returned.
     *
     * @return  Terms of service legal page or null.
     */
    override suspend fun getTermsOfServicePage(): LegalPage? {
        if (termsOfServiceCache == null) {
            loadLegalDocuments()
        }
        return termsOfServiceCache
    }


    /**
     * Checks whether a new version of the terms of service is available.
     *
     * @return  Whether a new version is available.
     */
    override suspend fun isNewTermsOfServiceAvailable(): Boolean {
        if (termsOfServiceCache == null) {
            loadLegalDocuments()
        }
        val acceptedVersion: Int = sharedPreferences.getInt("tos_version", -1)
        val onlineVersion: Int = termsOfServiceCache?.version ?: -1

        if (onlineVersion == -1) {
            //Online version cannot be loaded:
            return false
        }
        else {
            return acceptedVersion < onlineVersion
        }
    }


    /**
     * Accepts the current version of the terms of service.
     */
    override suspend fun acceptTermsOfService() {
        if (termsOfServiceCache == null) {
            loadLegalDocuments()
        }
        val onlineVersion: Int = termsOfServiceCache?.version ?: -1

        if (onlineVersion != -1) {
            //Online version loaded:
            sharedPreferences.edit {
                putInt("tos_version", onlineVersion)
            }
        }
    }


    /**
     * Loads the legal documents and updates the cache ("privacyPolicyCache" and "termsOfServiceCache").
     * If legal document cannot be loaded, the cache is null afterward.
     */
    private suspend fun loadLegalDocuments() {
        if (privacyPolicyCache != null && termsOfServiceCache != null) {
            //Information already loaded:
            return
        }

        //Load legal documents information:
        var privacyPolicy: LegalPage? = null
        var termsOfService: LegalPage? = null

        val response = webRequestService.request(restApiUrl)
        if (response.isSuccessful) {
            val responseJson: String? = response.content

            if (!responseJson.isNullOrBlank()) {
                val responseDto: LegalRootRestDto? = json.decodeFromString(responseJson)

                val appLocale: Locale = context.resources.configuration.locales.get(0)

                if (responseDto != null) {
                    if (responseDto.privacyPolicy != null) {
                        privacyPolicy = legalPageMapper.toDomain(responseDto.privacyPolicy, appLocale)
                    }
                    if (responseDto.termsOfService != null) {
                        termsOfService = legalPageMapper.toDomain(responseDto.termsOfService, appLocale)
                    }
                }
            }
        }

        //Update cache:
        if (privacyPolicy != null) {
            privacyPolicyCache = privacyPolicy
        }
        if (termsOfService != null) {
            termsOfServiceCache = termsOfService
        }
    }

}
