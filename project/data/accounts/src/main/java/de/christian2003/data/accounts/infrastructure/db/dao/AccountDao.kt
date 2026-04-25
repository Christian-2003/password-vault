package de.christian2003.data.accounts.infrastructure.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import de.christian2003.data.accounts.domain.entities.DetailType
import de.christian2003.data.accounts.infrastructure.db.dto.AccountDetailsDto
import de.christian2003.data.accounts.infrastructure.db.dto.AccountWithTags
import de.christian2003.data.accounts.infrastructure.db.entities.AccountEntity
import de.christian2003.data.accounts.infrastructure.db.entities.AccountTagCrossRef
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid


@Dao
interface AccountDao {

    @Query("SELECT * FROM accounts")
    @Transaction
    fun selectAllAccounts(): Flow<List<AccountWithTags>>


    @Query("SELECT * FROM accounts")
    fun selectAllAccountsWithoutTags(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun selectAccountWithoutTagsById(id: Uuid): AccountEntity?

    @Query("SELECT * FROM accounts WHERE id IN (:accountIds)")
    suspend fun selectAccountsByIds(accountIds: List<Uuid>): List<AccountEntity>


    @Query("SELECT * FROM accounts WHERE id = :id")
    @Transaction
    fun selectAccountById(id: Uuid): AccountWithTags?


    @Query("""
        SELECT a.id AS accountId, d.id AS detailId, t.url AS targetUrl
        FROM accounts a
        INNER JOIN details d ON d.account = a.id
        INNER JOIN targets t ON t.account = a.id
        WHERE d.type IN (:detailTypes)
        AND t.name = :targetName
    """)
    suspend fun selectAccountsAndDetailsByMetadata(targetName: String, detailTypes: List<DetailType>): List<AccountDetailsDto>


    @Insert
    suspend fun insertAccount(account: AccountEntity)


    @Delete
    suspend fun deleteAccount(account: AccountEntity)


    @Update
    suspend fun updateAccount(account: AccountEntity)


    @Insert
    suspend fun insertAccountTagCrossRef(crossRef: AccountTagCrossRef)


    @Query("DELETE FROM accounts_tags_cross_ref WHERE account = :accountId")
    suspend fun deleteAllAccountTagCrossRefs(accountId: Uuid)


    @Transaction
    suspend fun insertAccountWithTags(accountWithTags: AccountWithTags) {
        insertAccount(accountWithTags.account)
        accountWithTags.tags.forEach { tag ->
            insertAccountTagCrossRef(AccountTagCrossRef(accountWithTags.account.id, tag.id))
        }
    }


    @Transaction
    suspend fun updateAccountWithTags(accountWithTags: AccountWithTags) {
        deleteAllAccountTagCrossRefs(accountWithTags.account.id)
        updateAccount(accountWithTags.account)
        accountWithTags.tags.forEach { tag ->
            insertAccountTagCrossRef(AccountTagCrossRef(accountWithTags.account.id, tag.id))
        }
    }

}
