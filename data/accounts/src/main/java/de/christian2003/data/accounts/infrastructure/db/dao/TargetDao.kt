package de.christian2003.data.accounts.infrastructure.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import de.christian2003.data.accounts.infrastructure.db.entities.TargetEntity
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid


@Dao
interface TargetDao {

    @Query("SELECT * FROM targets WHERE account = :account")
    fun selectAllForAccount(account: Uuid): Flow<List<TargetEntity>>

    @Query("DELETE FROM targets WHERE account = :account")
    suspend fun deleteAllForAccount(account: Uuid)

    @Insert
    suspend fun insert(targetEntity: TargetEntity)


    @Transaction
    suspend fun saveAllTargetsForAccount(targets: List<TargetEntity>, account: Uuid) {
        deleteAllForAccount(account)
        targets.forEach { target ->
            insert(target)
        }
    }

}
