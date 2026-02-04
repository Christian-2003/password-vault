package de.christian2003.data.accounts.infrastructure.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import de.christian2003.data.accounts.infrastructure.db.entities.TagEntity
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid


@Dao
internal interface TagDao {

    @Query("SELECT * FROM tags")
    fun selectAll(): Flow<List<TagEntity>>

    @Query("SELECT * FROM tags WHERE id = :id")
    suspend fun selectById(id: Uuid): TagEntity?

    @Insert
    suspend fun insert(tag: TagEntity)

    @Delete
    suspend fun delete(tag: TagEntity)

    @Update
    suspend fun update(tag: TagEntity)

}
