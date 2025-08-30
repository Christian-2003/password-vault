package de.christian2003.passwordvault.plugin.infrastructure.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.EntryTagCrossRef
import de.christian2003.passwordvault.plugin.infrastructure.db.dto.EntryWithTags
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.EntryEntity
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid


@Dao
interface EntryDao {

    @Query("SELECT * FROM entries")
    @Transaction
    fun selectAllEntries(): Flow<List<EntryWithTags>>

    @Query("SELECT * FROM entries WHERE id = :id")
    @Transaction
    fun selectEntryById(id: Uuid): EntryWithTags?

    @Insert
    suspend fun insertEntry(entry: EntryEntity)

    @Delete
    suspend fun deleteEntry(entry: EntryEntity)

    @Update
    suspend fun updateEntry(entry: EntryEntity)


    @Insert
    suspend fun insertEntryTagCrossRef(crossRef: EntryTagCrossRef)

    @Query("DELETE FROM entries_tags_cross_ref WHERE entry = :entryId")
    suspend fun deleteAllEntryTagCrossRefs(entryId: Uuid)


    @Transaction
    suspend fun insertEntryWithTags(entryWithTags: EntryWithTags) {
        insertEntry(entryWithTags.entry)
        entryWithTags.tags.forEach { tag ->
            insertEntryTagCrossRef(EntryTagCrossRef(entryWithTags.entry.id, tag.id))
        }
    }

    @Transaction
    suspend fun updateEntryWithTags(entryWithTags: EntryWithTags) {
        deleteAllEntryTagCrossRefs(entryWithTags.entry.id)
        updateEntry(entryWithTags.entry)
        entryWithTags.tags.forEach { tag ->
            insertEntryTagCrossRef(EntryTagCrossRef(entryWithTags.entry.id, tag.id))
        }
    }

}
