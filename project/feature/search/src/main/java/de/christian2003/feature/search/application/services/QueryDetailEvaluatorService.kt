package de.christian2003.feature.search.application.services

import de.christian2003.data.accounts.domain.entities.Detail
import de.christian2003.feature.search.domain.entities.QueryAstNode
import de.christian2003.feature.search.domain.entities.QueryTokenType
import java.time.LocalDate
import javax.inject.Inject


/**
 * Service to evaluate whether an account detail matches a query.
 *
 * @param dateEvaluatorService  Evaluator service for dates.
 */
internal class QueryDetailEvaluatorService @Inject constructor(
    private val dateEvaluatorService: QueryDateEvaluatorService
): QueryEvaluatorService<Detail> {

    /**
     * Evaluates the provided abstract syntax tree for the given detail.
     *
     * @param node  AST node to evaluate.
     * @param value Detail on which to valuate the AST node.
     * @return      Whether the provided detail matches the specified AST node.
     */
    override fun evaluate(node: QueryAstNode, value: Detail): Boolean {
        when (node.token.type) {
            QueryTokenType.OperatorBool -> {
                val operator: String = node.token.value

                return when (operator) {
                    "and" -> evaluate(node.left!!, value) && evaluate(node.right!!, value)
                    "or" -> evaluate(node.left!!, value) || evaluate(node.right!!, value)
                    else -> false
                }
            }
            QueryTokenType.OperatorRelation -> {
                return evaluateRelation(node, value)
            }
            QueryTokenType.Colon -> {
                return evaluateEquality(node, value)
            }
            else -> {
                return false
            }
        }
    }


    /**
     * Evaluates the equality expression described by the provided AST node.
     *
     * @param node      AST node describing the expression.
     * @param detail    Detail used to evaluate the expression.
     * @return          Whether the expression is true or false.
     */
    private fun evaluateEquality(node: QueryAstNode, detail: Detail): Boolean {
        val field: String = node.left!!.token.value
        val value: String = node.right!!.token.value
        return evaluateEqualityInternal(field, value, detail)
    }


    /**
     * Internal evaluation of equality for the provided field (e.g. "name") and value (e.g. "Address").
     *
     * @param field     Field name.
     * @param value     Expression value.
     * @param detail    Detail for the evaluation.
     * @return          Whether the evaluation is true or false.
     */
    private fun evaluateEqualityInternal(field: String, value: String, detail: Detail): Boolean {
        try {
            return when (field) {
                "name" -> detail.name.contains(value, true)
                "content" -> !detail.metadata.isObfuscated //Prevent searching obfuscated details:
                        && detail.content.contains(value, true)
                "createdAt" -> {
                    val valueDate: LocalDate = LocalDate.parse(value)
                    return detail.metadata.createdAt.toLocalDate() == valueDate
                }
                "editedAt" -> {
                    val valueDate: LocalDate = LocalDate.parse(value)
                    return detail.metadata.editedAt.toLocalDate() == valueDate
                }
                "any" -> {
                    return detail.name.contains(value, true)
                            || (!detail.metadata.isObfuscated && detail.content.contains(value, true))
                }
                else -> false
            }
        }
        catch (_: Exception) {
            return false
        }
    }


    /**
     * Evaluates the relation described by the provided node.
     *
     * @param node      AST node representing the relational expression.
     * @param detail    Detail to use for evaluation.
     * @return          Whether the expression is true or false.
     */
    private fun evaluateRelation(node: QueryAstNode, detail: Detail): Boolean {
        try {
            val field: String = node.left!!.token.value
            val value: String = node.right!!.token.value
            val operator: String = node.token.value

            return when (operator) {
                "<=", ">=", "<", ">" -> {
                    val valueDate: LocalDate = LocalDate.parse(value)
                    when (field) {
                        "createdAt" -> dateEvaluatorService.evaluateDate(detail.metadata.createdAt.toLocalDate(), valueDate, operator)
                        "editedAt" -> dateEvaluatorService.evaluateDate(detail.metadata.editedAt.toLocalDate(), valueDate, operator)
                        else -> false
                    }
                }
                "<>" -> {
                    !evaluateEqualityInternal(field, value, detail)
                }
                else -> false
            }
        }
        catch (_: Exception) {
            return false
        }
    }

}
