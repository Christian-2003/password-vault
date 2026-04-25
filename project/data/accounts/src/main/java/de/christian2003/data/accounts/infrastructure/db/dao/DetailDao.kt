package de.christian2003.data.accounts.infrastructure.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import de.christian2003.data.accounts.domain.entities.DetailType
import de.christian2003.data.accounts.infrastructure.db.entities.DetailEntity
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid


@Dao
interface DetailDao {

    @Query("SELECT * FROM details WHERE account = :account")
    fun selectAllForAccount(account: Uuid): Flow<List<DetailEntity>>

    @Query("SELECT * FROM details WHERE id = :id")
    suspend fun selectById(id: Uuid): DetailEntity?

    @Query("SELECT * FROM details WHERE type = :type")
    suspend fun selectAllByType(type: DetailType): List<DetailEntity>

    @Insert
    suspend fun insert(detail: DetailEntity)

    @Delete
    suspend fun delete(detail: DetailEntity)

    @Update
    suspend fun update(detail: DetailEntity)

    @Query("DELETE FROM details WHERE account = :account")
    suspend fun deleteAllForAccount(account: Uuid)

    @Transaction
    suspend fun saveAllDetailsForAccount(details: List<DetailEntity>, account: Uuid) {
        deleteAllForAccount(account)
        details.forEach { detail ->
            insert(detail)
        }
    }

}
