package de.christian2003.feature.autofill.application.usecases

import de.christian2003.feature.autofill.domain.repositories.AutofillConfigRepository
import javax.inject.Inject


/**
 * Use case to determine whether the autofill service is enabled.
 *
 * @param configRepository  Repository for the autofill config.
 */
internal class IsAutofillEnabledUseCase @Inject constructor(
    private val configRepository: AutofillConfigRepository
) {

    /**
     * Returns whether the autofill is enabled.
     *
     * @return  Whether autofill is enabled.
     */
    fun isEnabled(): Boolean {
        return configRepository.isAutofillEnabled()
    }

}
