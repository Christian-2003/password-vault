package de.christian2003.data.accounts.application.usecases

import de.christian2003.data.accounts.domain.entities.Account
import de.christian2003.data.accounts.domain.repositories.AccountRepository
import javax.inject.Inject
import kotlin.uuid.Uuid


/**
 * Use case to get an account by ID.
 *
 * @param accountRepository Repository to access accounts.
 */
class GetAccountByIdUseCase @Inject internal constructor(
    private val accountRepository: AccountRepository
) {

    /**
     * Returns the account with the specified ID. If no account with the ID exists, null is returned.
     *
     * @param accountId ID of the account to return.
     * @return          Account with the specified ID or null.
     */
    suspend fun getAccountById(
        accountId: Uuid
    ): Account? {
        return accountRepository.getAccountById(accountId)
    }

}
