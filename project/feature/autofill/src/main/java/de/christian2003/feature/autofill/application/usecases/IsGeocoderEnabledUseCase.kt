package de.christian2003.feature.autofill.application.usecases

import de.christian2003.feature.autofill.domain.repositories.AutofillConfigRepository
import javax.inject.Inject


/**
 * Use case to get whether the geocoder for improved address parsing is enabled.
 *
 * @param configRepository  Repository for the autofill config.
 */
internal class IsGeocoderEnabledUseCase @Inject constructor(
    private val configRepository: AutofillConfigRepository
) {

    /**
     * Returns whether the geocoder is enabled.
     *
     * @return  Whether the geocoder is enabled.
     */
    fun isEnabled(): Boolean {
        return configRepository.isGeocoderEnabled()
    }

}
