package de.christian2003.data.accounts.application.usecases

import de.christian2003.data.accounts.domain.entities.AccountCapability
import de.christian2003.data.accounts.domain.entities.DetailType
import de.christian2003.data.accounts.domain.repositories.AccountRepository
import javax.inject.Inject


/**
 * Use case to get a list of account capabilities based on a target name and list of detail types.
 *
 * @param accountRepository Repository to access accounts.
 */
class GetAccountCapabilitiesUseCase @Inject internal constructor(
    private val accountRepository: AccountRepository
) {

    /**
     * Returns account capabilities
     *
     * @param targetName    Target name (e.g. Android package name or website host).
     * @param detailTypes   List of detail types.
     *
     * @return              List of account capabilities.
     */
    suspend fun getAccountCapabilities(targetName: String, detailTypes: List<DetailType>): List<AccountCapability> {
        val capabilities: List<AccountCapability> = accountRepository.getAccountsByMetadata(targetName, detailTypes)
        return capabilities
    }

}
