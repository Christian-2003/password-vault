package de.christian2003.passwordvault.application.usecases

import de.christian2003.passwordvault.application.repository.AccountRepository
import de.christian2003.passwordvault.application.repository.TagRepository
import de.christian2003.passwordvault.domain.model.account.Account
import de.christian2003.passwordvault.domain.model.detail.Detail
import de.christian2003.passwordvault.domain.model.tag.Tag
import de.christian2003.passwordvault.domain.model.target.Target
import kotlinx.coroutines.flow.first
import kotlin.uuid.Uuid


/**
 * Use case to update an existing account.
 *
 * @param accountRepository Repository to access the accounts.
 */
class UpdateAccountUseCase(
    private val accountRepository: AccountRepository,
    private val tagRepository: TagRepository
) {

    /**
     * Updates an existing account.
     *
     * @param id            ID of the account to update.
     * @param name          Name for the account.
     * @param description   Description for the account.
     * @param details       List of details for the account.
     * @param tags          List of tags for the account.
     * @param targets       List of targets for the account.
     */
    suspend fun updateAccount(
        id: Uuid,
        name: String,
        description: String,
        details: List<Detail>,
        tags: List<Tag>,
        targets: List<Target>
    ) {
        val account: Account? = accountRepository.getAccountById(id)

        if (account != null) {
            account.descriptor = account.descriptor.copy(
                name = name,
                description = description
            )
            account.details = details
            account.tags = tags
            account.targets = targets

            val allTags: List<Tag> = tagRepository.getAllTags().first()
            tags.forEach { tag ->
                if (!allTags.contains(tag)) {
                    throw IllegalStateException("Tag '$tag' cannot be added to account because it does not exist.")
                }
            }

            accountRepository.updateAccount(account)
        }
    }

}
