package de.christian2003.passwordvault.plugin.infrastructure.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.EntryEntity
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.EntryTagCrossRef
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.TagEntity
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid


@Dao
interface EntryDao {

    @Query("SELECT * FROM entries")
    fun selectAllEntries(): Flow<List<EntryEntity>>

    @Query("SELECT * FROM entries WHERE id = :id")
    suspend fun selectEntryById(id: Uuid): EntryEntity?

    @Insert
    suspend fun insertEntry(entry: EntryEntity)

    @Delete
    suspend fun deleteEntry(entry: EntryEntity)

    @Update
    suspend fun updateEntry(entry: EntryEntity)

    @Transaction
    suspend fun insertEntryWithTags(entry: EntryEntity, tags: List<TagEntity>) {
        insertEntry(entry)
        tags.forEach { tag ->
            insertEntryTagCrossRef(EntryTagCrossRef(entry.id, tag.id))
        }
    }

    @Transaction
    suspend fun updateEntryWithTags(entry: EntryEntity, tags: List<TagEntity>) {
        deleteAllEntryTagCrossRefs(entry.id)
        updateEntry(entry)
        tags.forEach { tag ->
            insertEntryTagCrossRef(EntryTagCrossRef(entry.id, tag.id))
        }
    }

    @Insert
    suspend fun insertEntryTagCrossRef(crossRef: EntryTagCrossRef)

    @Query("DELETE FROM entries_tags_cross_ref WHERE entry = :entryId")
    suspend fun deleteAllEntryTagCrossRefs(entryId: Uuid)

}
