package de.christian2003.data.accounts.domain.repositories

import de.christian2003.data.accounts.domain.entities.Account
import de.christian2003.data.accounts.domain.entities.Target
import kotlinx.coroutines.flow.Flow


/**
 * Repository to access autofill targets.
 */
internal interface TargetRepository {

    /**
     * Returns the targets with the specified package name (e.g. "de.christian2003.passwordvault") or
     * URL host (e.g. "passwordvault.christian2003.de").
     *
     * @param name  Name of the targets to return (either package name or URL host).
     * @return      List of targets with the specified name.
     */
    fun getTargetsByName(name: String): Flow<List<Target>>


    /**
     * For the specified target, the account is returned. If no account can be retrieved, null is
     * returned.
     *
     * @param target    Target whose account to return.
     * @return          Account of the specified target.
     */
    suspend fun getAccountForTarget(target: Target): Account?

}
