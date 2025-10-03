package de.christian2003.passwordvault.application.repository

import de.christian2003.passwordvault.domain.model.account.Account
import de.christian2003.passwordvault.domain.model.account.AccountDescriptor
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid


/**
 * Repository through which to access the accounts.
 */
interface AccountRepository {

    /**
     * Returns a list containing all accounts.
     *
     * @return  Flow containing a list of all accounts.
     */
    @Deprecated("Use getAllAccountDescriptors instead")
    fun getAllAccounts(): Flow<List<Account>>

    /**
     * Returns a list which contains the account descriptors of all accounts.
     *
     * @return  List of all account descriptors.
     */
    fun getAllAccountDescriptors(): Flow<List<AccountDescriptor>>


    /**
     * Returns the account with the passed UUID. If no account exists, null is returned.
     *
     * @param id    UUID of the account to return.
     * @return      Account with the specified UUID or null.
     */
    suspend fun getAccountById(id: Uuid): Account?


    /**
     * Creates the new account that is passed as argument.
     *
     * @param account   Account to create.
     */
    suspend fun createAccount(account: Account)


    /**
     * Updates the account that is passed as argument.
     *
     * @param account   Account to update.
     */
    suspend fun updateAccount(account: Account)


    /**
     * Deletes the account that is passed as argument.
     *
     * @param account   Account to delete.
     */
    suspend fun deleteAccount(account: Account)

}
