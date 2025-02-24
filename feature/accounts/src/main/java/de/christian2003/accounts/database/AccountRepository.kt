package de.christian2003.accounts.database

import java.util.UUID


class AccountRepository(

    private val accountDao: AccountDao

) {

    val allAccounts = accountDao.selectAll()


    suspend fun selectAccountById(id: UUID): AccountEntity? {
        return accountDao.selectById(id)
    }

    suspend fun insertAccount(account: AccountEntity) {
        accountDao.insert(account)
    }

    suspend fun deleteAccount(account: AccountEntity) {
        accountDao.delete(account)
    }

}
