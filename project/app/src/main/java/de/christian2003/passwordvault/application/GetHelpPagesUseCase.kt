package de.christian2003.passwordvault.application

import de.christian2003.passwordvault.domain.entities.HelpPage
import de.christian2003.passwordvault.domain.repositories.HelpPagesRepository
import javax.inject.Inject


/**
 * Use case to retrieve a list of all available help pages.
 *
 * @param helpPagesRepository   Repository through which to access the help pages.
 */
internal class GetHelpPagesUseCase @Inject constructor(
    private val helpPagesRepository: HelpPagesRepository
) {

    /**
     * Retrieves a list of all available help pages.
     *
     * @return  List of all available help pages.
     */
    suspend fun getHelpPages(): List<HelpPage> {
        return helpPagesRepository.getHelpPages()
    }

}
