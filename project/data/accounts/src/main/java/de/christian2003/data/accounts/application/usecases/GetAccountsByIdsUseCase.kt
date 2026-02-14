package de.christian2003.data.accounts.application.usecases

import de.christian2003.data.accounts.domain.entities.Account
import de.christian2003.data.accounts.domain.repositories.AccountRepository
import javax.inject.Inject
import kotlin.uuid.Uuid


/**
 * Use case to get a list of accounts based on their IDs.
 *
 * @param accountRepository Repository to access accounts.
 */
class GetAccountsByIdsUseCase @Inject internal constructor(
    private val accountRepository: AccountRepository
) {

    /**
     * Returns a list of accounts that have the IDs that are provided as argument.
     *
     * @param accountIds    List of IDs whose accounts to return.
     * @return              List accounts with the provided IDs.
     */
    suspend fun getAccountsByIds(accountIds: List<Uuid>): List<Account> {
        return accountRepository.getAccountsByIds(accountIds)
    }

}
