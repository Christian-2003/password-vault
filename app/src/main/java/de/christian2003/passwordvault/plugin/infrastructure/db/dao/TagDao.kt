package de.christian2003.passwordvault.plugin.infrastructure.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.TagEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface TagDao {

    @Query("SELECT * FROM tags")
    fun selectAll(): Flow<List<TagEntity>>

    @Insert
    suspend fun insert(tag: TagEntity)

    @Delete
    suspend fun delete(tag: TagEntity)

    @Update
    suspend fun update(tag: TagEntity)

}
