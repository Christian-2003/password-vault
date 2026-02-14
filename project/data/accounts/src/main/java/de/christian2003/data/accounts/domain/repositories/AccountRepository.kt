package de.christian2003.data.accounts.domain.repositories

import de.christian2003.data.accounts.domain.entities.Account
import de.christian2003.data.accounts.domain.entities.AccountCapability
import de.christian2003.data.accounts.domain.entities.AccountDescriptor
import de.christian2003.data.accounts.domain.entities.DetailType
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid


/**
 * Repository through which to access the accounts.
 */
internal interface AccountRepository {

    /**
     * Returns a list which contains the account descriptors of all accounts.
     *
     * @return  List of all account descriptors.
     */
    fun getAllAccountDescriptors(): Flow<List<AccountDescriptor>>


    /**
     * Returns a list with the accounts whose IDs are passed as argument.
     *
     * @param accountIds    List of IDs whose accounts to return.
     * @return              Accounts with the provided IDs.
     */
    suspend fun getAccountsByIds(accountIds: List<Uuid>): List<Account>


    /**
     * Returns the account with the passed UUID. If no account exists, null is returned.
     *
     * @param id    UUID of the account to return.
     * @return      Account with the specified UUID or null.
     */
    suspend fun getAccountById(id: Uuid): Account?


    /**
     * Returns an account capability (i.e. account IDs and IDs of details) that whose account and
     * details match the specified metadata.
     *
     * @param targetName    Target name (e.g. Android package name or website host).
     * @param detailTypes   Types of details.
     * @return              List of account capabilities.
     */
    suspend fun getAccountsByMetadata(targetName: String, detailTypes: List<DetailType>): List<AccountCapability>


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
