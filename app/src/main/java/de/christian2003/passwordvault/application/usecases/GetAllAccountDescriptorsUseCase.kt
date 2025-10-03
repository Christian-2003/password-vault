package de.christian2003.passwordvault.application.usecases

import de.christian2003.passwordvault.application.repository.AccountRepository
import de.christian2003.passwordvault.domain.model.account.AccountDescriptor
import kotlinx.coroutines.flow.Flow


/**
 * Use case to get a list of all account descriptors.
 *
 * @param accountRepository Repository to access accounts.
 */
class GetAllAccountDescriptorsUseCase(
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
