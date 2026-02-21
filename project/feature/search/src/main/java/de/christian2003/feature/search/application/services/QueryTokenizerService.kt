package de.christian2003.feature.search.application.services

import de.christian2003.feature.search.domain.entities.QueryToken
import de.christian2003.feature.search.domain.entities.QueryTokenType


/**
 * Service can tokenize search queries.
 */
internal class QueryTokenizerService {

    /**
     * Tokenizes the provided query string.
     *
     * @param query                     Query to tokenize.
     * @return                          List of tokens.
     * @throws IllegalArgumentException The provided query string is illegal (e.g. a quoted literal
     *                                  is not closed),
     */
    fun tokenize(query: String): List<QueryToken> {
        val tokens: MutableList<QueryToken> = mutableListOf()
        var i = 0

        while (i < query.length) {
            val c: Char = query[i]

            when {
                c.isWhitespace() -> {
                    i++
                }

                c == '(' -> {
                    val token = QueryToken(
                        type = QueryTokenType.ParenthesesOpen,
                        value = "("
                    )
                    tokens.add(token)
                    i++
                }

                c == ')' -> {
                    val token = QueryToken(
                        type = QueryTokenType.ParenthesesClose,
                        value = ")"
                    )
                    tokens.add(token)
                    i++
                }

                c == ':' -> {
                    val token = QueryToken(
                        type = QueryTokenType.Colon,
                        value = ":"
                    )
                    tokens.add(token)
                    i++
                }

                c == '"' -> {
                    i++ //Skip opening quote
                    val builder = StringBuilder()

                    while (i < query.length && query[i] != '"') {
                        builder.append(query[i])
                        i++
                    }

                    if (i >= query.length) {
                        throw IllegalArgumentException("Quoted string not terminated")
                    }

                    i++ //Skip closing quote

                    val token = QueryToken(
                        type = QueryTokenType.QuotedLiteral,
                        value = builder.toString()
                    )
                    tokens.add(token)
                }

                c == '<' || c == '>' -> {
                    val start: Int = i
                    i++

                    if (i < query.length && (query[i] == '=' || (c == '<' && query[i] == '>'))) {
                        i++
                    }

                    val operator: String = query.substring(start, i)

                    val token = QueryToken(
                        type = QueryTokenType.OperatorRelation,
                        value = operator
                    )
                    tokens.add(token)
                }

                else -> {
                    val start: Int = i
                    while (i < query.length
                        && !query[i].isWhitespace()
                        && query[i] != '('
                        && query[i] != ')'
                        && query[i] != ':'
                        && query[i] != '"'
                        && query[i] != '<'
                        && query[i] != '>'
                    ) {
                        i++
                    }

                    val literal: String = query.substring(start, i)
                    val lowercaseLiteral: String = literal.lowercase()

                    val token: QueryToken = if (lowercaseLiteral == "and" || lowercaseLiteral == "or") {
                        QueryToken(
                            type = QueryTokenType.OperatorBool,
                            value = lowercaseLiteral
                        )
                    } else {
                        QueryToken(
                            type = QueryTokenType.Literal,
                            value = literal
                        )
                    }

                    tokens.add(token)
                }
            }
        }

        return tokens
    }

}
