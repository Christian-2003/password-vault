package de.christian2003.data.accounts.application.usecases

import de.christian2003.data.accounts.domain.entities.Account
import de.christian2003.data.accounts.domain.entities.BatchOption
import de.christian2003.data.accounts.domain.entities.Tag
import de.christian2003.data.accounts.domain.repositories.AccountRepository
import de.christian2003.data.accounts.domain.repositories.TagRepository
import javax.inject.Inject


/**
 * Use case to create a batch of accounts.
 *
 * @param accountRepository Repository to access accounts.
 * @param tagRepository     Repository to access tags.
 */
class CreateAccountsBatchUseCase @Inject internal constructor(
    private val accountRepository: AccountRepository,
    private val tagRepository: TagRepository
) {

    /**
     * Creates the accounts specified as argument in a batch operation.
     *
     * @param accounts      Accounts to create.
     * @param tags          Tags to create.
     * @param  batchOption  Batch option describing what should happen with existing data.
     */
    suspend fun createAccountsBatch(
        accounts: List<Account>,
        tags: List<Tag>,
        batchOption: BatchOption
    ) {
        tagRepository.createTagsBatch(tags, batchOption)
        accountRepository.createAccountsBatch(accounts, batchOption)
    }

}
