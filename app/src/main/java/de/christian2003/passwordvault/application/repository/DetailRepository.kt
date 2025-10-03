package de.christian2003.passwordvault.application.repository

import de.christian2003.passwordvault.domain.model.detail.Detail
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
    fun getAllDetailsForAccount(entry: Uuid): Flow<List<Detail>>


    suspend fun saveAllDetailsForAccount(details: List<Detail>, entry: Uuid)

}
