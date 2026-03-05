package de.christian2003.feature.search.domain.entities


/**
 * Search result generated after a search operation finishes.
 *
 * @param accountResults    List of account results describing the accounts found by the search
 *                          operation.
 */
internal data class SearchResult(
    val accountResults: List<AccountSearchResult>
)
