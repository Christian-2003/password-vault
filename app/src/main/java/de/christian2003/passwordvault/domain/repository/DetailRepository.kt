package de.christian2003.passwordvault.domain.repository

import de.christian2003.passwordvault.domain.entry.Detail
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid


/**
 * Repository through which to access the details.
 */
interface DetailRepository {

    /**
     * Returns a list containing all details for the entry specified.
     *
     * @param entry UUID of the entry whose details to return.
     * @return      Flow containing a list of all details for the entry.
     */
    fun getAllDetailsForEntry(entry: Uuid): Flow<List<Detail>>


    /**
     * Returns the detail with the passed UUID. If no detail exists, null is returned.
     *
     * @param id    UUID of the detail to return.
     * @return      Detail with the specified UUID or null.
     */
    suspend fun getDetailById(id: Uuid): Detail?


    /**
     * Creates the new detail that is passed as argument.
     *
     * @param detail    Detail to create.
     */
    suspend fun createDetail(detail: Detail)


    /**
     * Updates the detail that is passed as argument.
     *
     * @param detail    Detail to update.
     */
    suspend fun updateDetail(detail: Detail)


    /**
     * Deletes the detail that is passed as argument.
     *
     * @param detail    Detail to delete.
     */
    suspend fun deleteDetail(detail: Detail)


    suspend fun saveAllDetailsForEntry(details: List<Detail>, entry: Uuid)

}
