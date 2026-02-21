package de.christian2003.feature.search.domain.entities


/**
 * Value object models a single token of a search query.
 *
 * @param type  Type of the token.
 * @param value Value of the token.
 */
internal data class QueryToken(
    val type: QueryTokenType,
    val value: String
)
