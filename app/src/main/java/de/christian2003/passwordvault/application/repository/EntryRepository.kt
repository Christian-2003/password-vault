package de.christian2003.passwordvault.application.repository

import de.christian2003.passwordvault.domain.model.entry.Entry
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid


/**
 * Repository through which to access the entries.
 */
interface EntryRepository {

    /**
     * Returns a list containing all entries.
     *
     * @return  Flow containing a list of all entries.
     */
    fun getAllEntries(): Flow<List<Entry>>


    /**
     * Returns the entry with the passed UUID. If no entry exists, null is returned.
     *
     * @param id    UUID of the entry to return.
     * @return      Entry with the specified UUID or null.
     */
    suspend fun getEntryById(id: Uuid): Entry?


    /**
     * Creates the new entry that is passed as argument.
     *
     * @param entry Entry to create.
     */
    suspend fun createEntry(entry: Entry)


    /**
     * Updates the entry that is passed as argument.
     *
     * @param entry Entry to update.
     */
    suspend fun updateEntry(entry: Entry)


    /**
     * Deletes the entry that is passed as argument.
     *
     * @param entry Entry to delete.
     */
    suspend fun deleteEntry(entry: Entry)

}
