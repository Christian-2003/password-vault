package de.christian2003.feature.search.domain.entities

import de.christian2003.data.accounts.domain.entities.AccountDescriptor
import de.christian2003.data.accounts.domain.entities.Tag


data class AccountSearchResult(
    val matchingStrings: List<String>,
    val accountDescriptor: AccountDescriptor,
    val matchingTags: List<Tag>
)
