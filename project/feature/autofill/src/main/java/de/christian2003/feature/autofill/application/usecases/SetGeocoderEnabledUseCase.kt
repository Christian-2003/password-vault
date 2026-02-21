package de.christian2003.feature.autofill.application.usecases

import de.christian2003.feature.autofill.domain.repositories.AutofillConfigRepository
import javax.inject.Inject


/**
 * Use case to set whether the geocoder for improved address parsing is enabled.
 *
 * @param configRepository  Repository for the autofill config.
 */
internal class SetGeocoderEnabledUseCase @Inject constructor(
    private val configRepository: AutofillConfigRepository
) {

    /**
     * Changes whether the geocoder is enabled.
     *
     * @param isGeocoderEnabled Whether the geocoder is enabled.
     */
    fun setEnabled(isGeocoderEnabled: Boolean) {
        configRepository.setGeocoderEnabled(isGeocoderEnabled)
    }

}
