package de.christian2003.feature.search.application.services

import de.christian2003.data.accounts.application.usecases.GetLocalizedPackageNameUseCase
import de.christian2003.data.accounts.domain.entities.Account
import de.christian2003.feature.search.domain.entities.QueryAstNode
import de.christian2003.feature.search.domain.entities.QueryTokenType
import java.time.LocalDate
import javax.inject.Inject


/**
 * Service can evaluate a search query.
 *
 * @param getLocalizedPackageNameUseCase    Use case to get a localized package name.
 */
internal class QueryEvaluatorService @Inject constructor(
    private val getLocalizedPackageNameUseCase: GetLocalizedPackageNameUseCase
) {

    /**
     * Evaluates the provided AST for the specified account.
     *
     * @param node      AST representing the search query.
     * @param account   Account for which to verify whether it matches the specified AST.
     * @return          Whether the account matches the search query provided as AST.
     */
    fun evaluate(node: QueryAstNode, account: Account): Boolean {
        when (node.token.type) {
            QueryTokenType.OperatorBool -> {
                val left: Boolean = evaluate(node.left!!, account)
                val right: Boolean = evaluate(node.right!!, account)
                val operator: String = node.token.value

                return when (operator) {
                    "and" -> left && right
                    "or" -> left || right
                    else -> false
                }
            }
            QueryTokenType.OperatorRelation -> {
                return evaluateRelation(node, account)
            }
            QueryTokenType.Colon -> {
                return evaluateEquality(node, account)
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
     * @param account   Account used to evaluate the expression.
     * @return          Whether the expression is true or false.
     */
    private fun evaluateEquality(node: QueryAstNode, account: Account): Boolean {
        val field: String = node.left!!.token.value
        val value: String = node.right!!.token.value
        return evaluateEqualityInternal(field, value, account)
    }


    /**
     * Internal evaluation of equality for the provided field (e.g. "name") and value (e.g. "Gmail").
     *
     * @param field     Field name.
     * @param value     Expression value.
     * @param account   Account for the evaluation.
     * @return          Whether the evaluation is true or false.
     */
    private fun evaluateEqualityInternal(field: String, value: String, account: Account): Boolean {
        try {
            return when (field) {
                "name" -> account.descriptor.name.contains(value, true)
                "description" -> account.descriptor.description.contains(value, true)
                "tag" -> {
                    var tagResult = false
                    account.tags.forEach { tag ->
                        if (tag.name.contains(value, true)) {
                            tagResult = true
                            return@forEach
                        }
                    }
                    tagResult
                }
                "target" -> {
                    var targetResult = false
                    account.descriptor.targets.forEach { target ->
                        //Test package name:
                        if (target.name.contains(value, true)) {
                            targetResult = true
                            return@forEach
                        }
                        else if (target.isAndroidApp()) {
                            //Test for localized name:
                            val localizedName: String? = getLocalizedPackageNameUseCase.getLocalizedPackageName(target.name)
                            if (localizedName != null && localizedName.contains(value, true)) {
                                targetResult = true
                                return@forEach
                            }
                        }
                    }
                    targetResult
                }
                "createdAt" -> {
                    val valueDate: LocalDate = LocalDate.parse(value)
                    return account.metadata.createdAt.toLocalDate() == valueDate
                }
                "editedAt" -> {
                    val valueDate: LocalDate = LocalDate.parse(value)
                    return account.metadata.editedAt.toLocalDate() == valueDate
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
     * @param account   Account to use for evaluation.
     * @return          Whether the expression is true or false.
     */
    private fun evaluateRelation(node: QueryAstNode, account: Account): Boolean {
        try {
            val field: String = node.left!!.token.value
            val value: String = node.right!!.token.value
            val operator: String = node.token.value

            return when (operator) {
                "<=", ">=", "<", ">" -> {
                    val valueDate: LocalDate = LocalDate.parse(value)
                    when (field) {
                        "createdAt" -> evaluateDate(account.metadata.createdAt.toLocalDate(), valueDate, operator)
                        "editedAt" -> evaluateDate(account.metadata.editedAt.toLocalDate(), valueDate, operator)
                        else -> false
                    }
                }
                "<>" -> {
                    println("<> $value")
                    !evaluateEqualityInternal(field, value, account)
                }
                else -> false
            }
        }
        catch (_: Exception) {
            return false
        }
    }


    /**
     * Evaluates the relational expression between the provided dates based on the specified operator.
     *
     * @param leftDate  Left date of the relation.
     * @param rightDate Right date of the relation.
     * @param operator  Relational operator.
     * @return          Whether the relational expression is true or false.
     */
    private fun evaluateDate(leftDate: LocalDate, rightDate: LocalDate, operator: String): Boolean {
        return when (operator) {
            "<=" -> leftDate <= rightDate
            ">=" -> leftDate >= rightDate
            "<" -> leftDate < rightDate
            ">" -> leftDate > rightDate
            "<>" -> leftDate != rightDate
            else -> false
        }
    }

}
