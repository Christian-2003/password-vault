package de.christian2003.feature.autofill.application.usecases

import de.christian2003.feature.autofill.domain.repositories.AutofillConfigRepository
import javax.inject.Inject


/**
 * Use case to get whether the autofill service is selected by the Android system.
 *
 * @param configRepository  Repository for the autofill config.
 */
internal class IsServiceSelectedUseCase @Inject constructor(
    private val configRepository: AutofillConfigRepository
) {

    /**
     * Returns whether the autofill service is selected by the Android system.
     *
     * @return  Whether the autofill service is selected.
     */
    fun isSelected(): Boolean {
        return configRepository.isSelectedBySystem()
    }

}
