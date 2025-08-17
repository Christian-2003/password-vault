package de.christian2003.passwordvault.plugin.infrastructure.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.EntryEntity
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid


@Dao
interface EntryDao {

    @Query("SELECT * FROM entries")
    fun selectAllEntries(): Flow<List<EntryEntity>>

    @Query("SELECT * FROM entries WHERE id = :id")
    suspend fun selectById(id: Uuid): EntryEntity?

    @Insert
    suspend fun insert(entry: EntryEntity)

    @Delete
    suspend fun delete(entry: EntryEntity)

    @Update
    suspend fun update(entry: EntryEntity)

}
