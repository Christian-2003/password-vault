package de.christian2003.accounts.database

import kotlinx.coroutines.flow.Flow
import java.util.UUID


/**
 * Class implements the repository through which to access and manipulate all data relevant to the
 * accounts-feature.
 *
 * @author  Christian-2003
 * @since   3.8.0
 */
class AccountsRepository(

    /**
     * DAO through which to access the accounts.
     */
    private val accountDao: AccountDao,

    /**
     * DAO through which to access the details of accounts.
     */
    private val detailDao: DetailDao

) {

    /**
     * List of all accounts.
     */
    val allAccounts = accountDao.selectAll()


    /**
     * Returns an account of the ID specified. If no account exists with the ID specified,
     * null is returned.
     *
     * @param id    ID of the account to return.
     * @return      Account of the ID specified or null if no account exists.
     */
    suspend fun selectAccountById(id: UUID): AccountEntity? {
        return accountDao.selectById(id)
    }

    /**
     * Inserts the account specified into the database. If the account already exists, it is replaced
     * with the account passed.
     *
     * @param account   Account to insert into the database.
     */
    suspend fun insertAccount(account: AccountEntity) {
        accountDao.insert(account)
    }

    /**
     * Deletes the account specified.
     *
     * @param account   Account to delete.
     */
    suspend fun deleteAccount(account: AccountEntity) {
        accountDao.delete(account)
    }

    /**
     * Updates the account within the database. The account to update is determined based on it's
     * primary key (i.e. it's UUID).
     *
     * @param account   Account to update.
     */
    suspend fun updateAccount(account: AccountEntity) {
        accountDao.update(account)
    }


    /**
     * Returns all details for the account specified.
     *
     * @param account   ID of the account whose details to return.
     * @return          List of details associated to the account specified.
     */
    fun selectDetailsForAccount(account: UUID): Flow<List<DetailEntity>> {
        return detailDao.selectForAccount(account)
    }

    /**
     * Returns the detail with the ID specified. If no detail with this ID exists, null is returned.
     *
     * @param id    ID of the detail to return.
     * @return      Detail with the ID specified or null if no detail with the ID exists.
     */
    suspend fun selectDetailById(id: UUID): DetailEntity? {
        return detailDao.selectById(id)
    }

    /**
     * Inserts the detail specified into the database. If the detail already exists, it is replaced.
     *
     * @param detail    Detail to insert.
     */
    suspend fun insertDetail(detail: DetailEntity) {
        detailDao.insert(detail)
    }

    /**
     * Deletes the detail specified from the database.
     *
     * @param detail    Detail to delete.
     */
    suspend fun deleteDetail(detail: DetailEntity) {
        detailDao.delete(detail)
    }

    /**
     * Updates the detail specified within the database. The detail to update is determined by it's
     * primary key (i.e. it's UUID).
     *
     * @param detail    Detail to update.
     */
    suspend fun updateDetail(detail: DetailEntity) {
        detailDao.update(detail)
    }

}
