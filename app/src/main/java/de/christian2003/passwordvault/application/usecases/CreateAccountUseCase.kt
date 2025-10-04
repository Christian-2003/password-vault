package de.christian2003.passwordvault.application.usecases

import de.christian2003.passwordvault.application.repository.AccountRepository
import de.christian2003.passwordvault.application.repository.TagRepository
import de.christian2003.passwordvault.domain.model.detail.Detail
import de.christian2003.passwordvault.domain.model.account.Account
import de.christian2003.passwordvault.domain.model.account.AccountDescriptor
import de.christian2003.passwordvault.domain.model.tag.Tag
import de.christian2003.passwordvault.domain.model.target.Target
import kotlinx.coroutines.flow.first


/**
 * Use case to create a new account.
 *
 * @param accountRepository Repository to access the accounts.
 * @param tagRepository     Repository to access the tags.
 */
class CreateAccountUseCase(
    private val accountRepository: AccountRepository,
    private val tagRepository: TagRepository,
) {

    /**
     * Creates a new account.
     *
     * @param name          Name for the account.
     * @param description   Description for the account.
     * @param details       List of details for the account.
     * @param tags          List of tags for the account.
     * @param targets       List of targets for the account.
     */
    suspend fun createAccount(
        name: String,
        description: String,
        details: List<Detail>,
        tags: List<Tag>,
        targets: List<Target>
    ) {
        val account = Account(
            descriptor = AccountDescriptor(
                name = name,
                description = description
            ),
            details = details,
            tags = tags,
            targets = targets
        )

        val allTags: List<Tag> = tagRepository.getAllTags().first()
        tags.forEach { tag ->
            if (!allTags.contains(tag)) {
                throw IllegalStateException("Tag '$tag' cannot be added to account because it does not exist.")
            }
        }

        accountRepository.createAccount(account)
    }

}
