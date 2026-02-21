package de.christian2003.feature.search.application.services

import de.christian2003.feature.search.domain.entities.QueryAstNode
import de.christian2003.feature.search.domain.entities.QueryToken
import de.christian2003.feature.search.domain.entities.QueryTokenCollection
import de.christian2003.feature.search.domain.entities.QueryTokenType
import javax.inject.Inject


/**
 * Service that builds an AST from a collection of tokens.
 */
internal class QueryAstNodeBuilderService @Inject constructor() {

    /**
     * List of tokens that are being parsed.
     */
    private lateinit var tokens: QueryTokenCollection


    /**
     * Parses the provided list of tokens into an AST.
     *
     * @param tokens                    List of tokens to parse.
     * @return                          Generated abstract syntax tree (AST).
     * @throws IllegalArgumentException The passed list of tokens is invalid.
     */
    fun parse(tokens: QueryTokenCollection): QueryAstNode? {
        this.tokens = tokens

        val rootNode: QueryAstNode? = parseExpression()
        if (tokens.remaining > 0) {
            throw IllegalArgumentException("Unexpected trailing tokens")
        }
        return rootNode
    }


    /**
     * Parses an expression.
     *
     * @return  Generated AST node.
     */
    private fun parseExpression(): QueryAstNode? {
        var node: QueryAstNode? = parseAndExpression()

        while (tokens.match(QueryTokenType.OperatorBool) && tokens.peek()!!.value == "or") {
            tokens.consume() //OR
            val right: QueryAstNode? = parseAndExpression()

            node = QueryAstNode(
                token = QueryToken(QueryTokenType.OperatorBool, "or"),
                left = node,
                right = right
            )
        }

        return node
    }


    /**
     * Parses an AND expression.
     *
     * @return  Generated AST node.
     */
    private fun parseAndExpression(): QueryAstNode? {
        var node: QueryAstNode? = parsePrimary()

        while (true) {
            val next: QueryToken? = tokens.peek()

            //Explicit AND
            if (next != null && next.type == QueryTokenType.OperatorBool && next.value == "and") {
                tokens.consume()

                val right: QueryAstNode? = parsePrimary()
                node = QueryAstNode(
                    token = QueryToken(QueryTokenType.OperatorBool, "and"),
                    left = node,
                    right = right
                )
                continue
            }

            //Implicit AND:
            //If the next token can start a primary expression and no boolean operator was used, treat it as AND.
            if (next != null && isPrimaryStart(next)) {
                val right: QueryAstNode? = parsePrimary()
                node = QueryAstNode(
                    token = QueryToken(QueryTokenType.OperatorBool, "and"),
                    left = node,
                    right = right
                )
                continue
            }

            break
        }

        return node
    }


    /**
     * Parses a primary token.
     *
     * @return  Generated AST node.
     */
    private fun parsePrimary(): QueryAstNode? {
        val token: QueryToken? = tokens.peek()
        if (token == null) {
            return null
        }

        return when (token.type) {
            QueryTokenType.ParenthesesOpen -> {
                tokens.consume()
                val node: QueryAstNode? = parseExpression()
                if (node == null) {
                    throw IllegalArgumentException("Empty group")
                }
                tokens.expect(QueryTokenType.ParenthesesClose)
                node
            }
            QueryTokenType.Literal -> {
                val left: QueryToken = tokens.consume()

                //Comparison detection (field:relation value)
                if (tokens.match(QueryTokenType.Colon)) {
                    tokens.consume()

                    //If relation exists, use it
                    if (tokens.match(QueryTokenType.OperatorRelation)) {
                        val relation = tokens.consume()
                        val right = tokens.expect(QueryTokenType.Literal)
                        return QueryAstNode(
                            token = relation,
                            left = QueryAstNode(left, null, null),
                            right = QueryAstNode(right, null, null)
                        )
                    }

                    //Otherwise treat as equality (field:value)
                    val right = tokens.expect(QueryTokenType.Literal)
                    return QueryAstNode(
                        token = QueryToken(QueryTokenType.Colon, ":"),
                        left = QueryAstNode(left, null, null),
                        right = QueryAstNode(right, null, null)
                    )
                }

                QueryAstNode(left, null, null)
            }
            else -> throw IllegalArgumentException("Unexpected token: ${token.type}")
        }
    }


    /**
     * Returns whether the provided token is a primary start token.
     *
     * @param token Token to test.
     * @return      Whether the provided token is a primary start token.
     */
    private fun isPrimaryStart(token: QueryToken): Boolean {
        return when (token.type) {
            QueryTokenType.Literal, QueryTokenType.ParenthesesOpen -> true
            else -> false
        }
    }

}
