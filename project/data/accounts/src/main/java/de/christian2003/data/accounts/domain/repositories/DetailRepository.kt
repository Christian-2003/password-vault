package de.christian2003.data.accounts.domain.repositories

import de.christian2003.data.accounts.domain.entities.Detail
import de.christian2003.data.accounts.domain.entities.DetailType
import kotlin.uuid.Uuid


/**
 * Repository through which to access the account details.
 */
internal interface DetailRepository {

    /**
     * Returns a map with all details mapped to their account ID that match the specified type.
     *
     * @param type  Type of the details to return.
     * @return      Map of all details with the specified type.
     */
    suspend fun getAllDetailsByType(type: DetailType): Map<Detail, Uuid>

}
