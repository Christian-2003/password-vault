package de.christian2003.feature.search.domain.entities


/**
 * Value object models a node for an abstract syntax tree (AST) for a search query.
 *
 * @param token Token of this node.
 * @param left  Left AST child node.
 * @param right Right AST child node.
 */
internal data class QueryAstNode(
    val token: QueryToken,
    val left: QueryAstNode?,
    val right: QueryAstNode?
)
