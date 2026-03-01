package de.christian2003.feature.search.domain.entities

import de.christian2003.data.accounts.domain.entities.AccountDescriptor
import de.christian2003.data.accounts.domain.entities.Detail
import de.christian2003.data.accounts.domain.entities.Tag


/**
 * Search result for a single account.
 *
 * @param accountDescriptor Account descriptor of the account search result item.
 * @param details           Details of the account which match the search query.
 */
data class AccountSearchResult(
    val accountDescriptor: AccountDescriptor,
    val details: List<Detail>
)
