package de.christian2003.feature.search.application.services

import de.christian2003.feature.search.domain.entities.QueryAstNode


/**
 * Evaluator service for a query AST.
 */
internal interface QueryEvaluatorService<T> {

    /**
     * Evaluates the provided abstract syntax tree for the given value.
     *
     * @param node  AST node to evaluate.
     * @param value Value on which to valuate the AST node.
     * @return      Whether the provided value matches the specified AST node.
     */
    fun evaluate(node: QueryAstNode, value: T): Boolean

}
