package de.christian2003.data.accounts.application.usecases

import de.christian2003.data.accounts.domain.entities.AccountDescriptor
import de.christian2003.data.accounts.domain.repositories.AccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


/**
 * Use case to get a list of all account descriptors.
 *
 * @param accountRepository Repository to access accounts.
 */
class GetAllAccountDescriptorsUseCase @Inject internal constructor(
    private val accountRepository: AccountRepository
) {

    /**
     * Returns a list with all account descriptors.
     *
     * @return  List of all account descriptors.
     */
    fun getAllAccountDescriptors(): Flow<List<AccountDescriptor>> {
        return accountRepository.getAllAccountDescriptors()
    }

}
