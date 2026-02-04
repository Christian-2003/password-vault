package de.christian2003.data.accounts.application.usecases

import de.christian2003.data.accounts.domain.entities.Account
import de.christian2003.data.accounts.domain.entities.AccountDescriptor
import de.christian2003.data.accounts.domain.entities.Detail
import de.christian2003.data.accounts.domain.entities.Tag
import de.christian2003.data.accounts.domain.entities.Target
import de.christian2003.data.accounts.domain.repositories.AccountRepository
import de.christian2003.data.accounts.domain.repositories.TagRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject


/**
 * Use case to create a new account.
 *
 * @param accountRepository Repository to access the accounts.
 * @param tagRepository     Repository to access the tags.
 */
class CreateAccountUseCase @Inject internal constructor(
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
                description = description,
                targets = targets
            ),
            details = details,
            tags = tags
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
