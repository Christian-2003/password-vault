package de.christian2003.passwordvault.domain.repositories

import de.christian2003.passwordvault.domain.entities.HelpPage


/**
 * Repository through which to access all help pages that are available to users.
 */
internal interface HelpPagesRepository {

    /**
     * Retrieves a list of help pages.
     *
     * @return  List of help pages.
     */
    suspend fun getHelpPages(): List<HelpPage>

}
