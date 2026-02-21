package de.christian2003.feature.autofill.application.usecases

import de.christian2003.feature.autofill.domain.repositories.AutofillConfigRepository
import javax.inject.Inject


/**
 * Use case to set whether to use the autofill service. This does not affect whether the service is
 * selected by the Android system!
 *
 * @param configRepository  Repository for the autofill config.
 */
internal class SetAutofillEnabledUseCase @Inject constructor(
    private val configRepository: AutofillConfigRepository
) {

    /**
     * Changes whether the autofill service is enabled. If this is set to true, the service will
     * autofill data to other apps. However, this does NOT select the service in the Android system.
     * The service needs to be selected by the system separately!
     *
     * @param isAutofillEnabled Whether the autofill service is enabled.
     */
    fun setEnabled(isAutofillEnabled: Boolean) {
        configRepository.setAutofillEnabled(isAutofillEnabled)
    }

}
