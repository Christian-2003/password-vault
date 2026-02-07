package de.christian2003.data.accounts.application.usecases

import de.christian2003.data.accounts.domain.entities.Account
import de.christian2003.data.accounts.domain.repositories.AccountRepository
import javax.inject.Inject
import kotlin.uuid.Uuid


/**
 * Use case to delete an account.
 *
 * @param accountRepository Repository through which to access accounts.
 */
class DeleteAccountUseCase @Inject internal constructor(
    private val accountRepository: AccountRepository
) {

    /**
     * Deletes the specified account. If no account with the specified ID exists, nothing happens.
     *
     * @param accountId ID of the account to delete.
     */
    suspend fun deleteAccount(
        accountId: Uuid
    ) {
        val account: Account? = accountRepository.getAccountById(accountId)
        if (account != null) {
            accountRepository.deleteAccount(account)
        }
    }

}
