package de.christian2003.passwordvault.application.usecases

import de.christian2003.passwordvault.application.repository.AccountRepository
import de.christian2003.passwordvault.domain.model.account.Account
import kotlin.uuid.Uuid


/**
 * Use case to get an account by ID.
 *
 * @param accountRepository Repository to access accounts.
 */
class GetAccountByIdUseCase(
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
