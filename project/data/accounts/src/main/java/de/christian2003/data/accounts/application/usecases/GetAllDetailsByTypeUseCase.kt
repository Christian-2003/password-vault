package de.christian2003.data.accounts.application.usecases

import de.christian2003.data.accounts.domain.entities.Detail
import de.christian2003.data.accounts.domain.entities.DetailType
import de.christian2003.data.accounts.domain.repositories.DetailRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.uuid.Uuid


/**
 * Use case to get a list of all details with a specific type.
 *
 * @param detailRepository  Repsitory to access the details.
 */
class GetAllDetailsByTypeUseCase @Inject internal constructor(
    private val detailRepository: DetailRepository
) {

    /**
     * Returns a map which contains all details (mapped to their account ID) that have the
     * specified type.
     *
     * @param type  Type of the details to return.
     * @return      Map of all details with the specified type.
     */
    suspend fun getAllDetailsByType(type: DetailType): Map<Detail, Uuid> {
        return detailRepository.getAllDetailsByType(type)
    }

}
