package de.christian2003.passwordvault.application.usecases.acount

import de.christian2003.passwordvault.application.repository.AccountRepository
import de.christian2003.passwordvault.domain.model.account.Account
import kotlin.uuid.Uuid


/**
 * Use case to delete an account.
 *
 * @param accountRepository Repository through which to access accounts.
 */
class DeleteAccountUseCase(
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
