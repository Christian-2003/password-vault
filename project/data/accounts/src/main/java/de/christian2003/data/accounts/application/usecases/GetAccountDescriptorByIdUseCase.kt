package de.christian2003.data.accounts.application.usecases

import de.christian2003.data.accounts.domain.entities.AccountDescriptor
import de.christian2003.data.accounts.domain.repositories.AccountRepository
import javax.inject.Inject
import kotlin.uuid.Uuid


/**
 * Use case to get an account descriptor by it's ID.
 *
 * @param accountRepository Repository to access accounts.
 */
class GetAccountDescriptorByIdUseCase @Inject internal constructor(
    private val accountRepository: AccountRepository
) {

    /**
     * Returns the account descriptor for the specified ID or null if no account is found.
     *
     * @param id    ID of the account descriptor to return.
     * @return      Account descriptor for the specified ID or null.
     */
    suspend fun getAccountDescriptorById(id: Uuid): AccountDescriptor? {
        return accountRepository.getAccountDescriptorById(id)
    }

}
