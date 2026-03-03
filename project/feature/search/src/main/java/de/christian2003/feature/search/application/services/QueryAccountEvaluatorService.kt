package de.christian2003.feature.search.application.services

import de.christian2003.data.accounts.application.usecases.GetLocalizedPackageNameUseCase
import de.christian2003.data.accounts.domain.entities.Account
import de.christian2003.feature.search.domain.entities.QueryAstNode
import de.christian2003.feature.search.domain.entities.QueryTokenType
import java.time.LocalDate
import javax.inject.Inject


/**
 * Evaluator service to evaluate whether a specific query operation (such as equals or relational)
 * matches for an account.
 *
 * @param dateEvaluatorService              Date evaluator service.
 * @param getLocalizedPackageNameUseCase    Use case to get a localized package name.
 */
internal class QueryAccountEvaluatorService @Inject constructor(
    private val dateEvaluatorService: QueryDateEvaluatorService,
    private val getLocalizedPackageNameUseCase: GetLocalizedPackageNameUseCase
): QueryEvaluatorService<Account> {

    /**
     * Evaluates the provided abstract syntax tree for the given account.
     *
     * @param node  AST node to evaluate.
     * @param value Account on which to valuate the AST node.
     * @return      Whether the provided account matches the specified AST node.
     */
    override fun evaluate(node: QueryAstNode, value: Account): Boolean {
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
                "tag" -> evaluateTagEqualityInternal(value, account)
                "target" -> evaluateTargetEqualityInternal(value, account)
                "createdAt" -> {
                    val valueDate: LocalDate = LocalDate.parse(value)
                    return account.metadata.createdAt.toLocalDate() == valueDate
                }
                "editedAt" -> {
                    val valueDate: LocalDate = LocalDate.parse(value)
                    return account.metadata.editedAt.toLocalDate() == valueDate
                }
                "any" -> {
                    return account.descriptor.name.contains(value, true)
                            || account.descriptor.description.contains(value, true)
                            || evaluateTagEqualityInternal(value, account)
                            || evaluateTargetEqualityInternal(value, account)
                }
                else -> false
            }
        }
        catch (_: Exception) {
            return false
        }
    }


    /**
     * Internal evaluation of equality for the value (e.g. "Work") against the targets
     * of the specified account.
     *
     * @param value     Expression value.
     * @param account   Account for the evaluation.
     * @return          Whether the evaluation is true or false.
     */
    private fun evaluateTagEqualityInternal(value: String, account: Account): Boolean {
        var tagResult = false

        account.tags.forEach { tag ->
            if (tag.name.contains(value, true)) {
                tagResult = true
                return@forEach
            }
        }

        return tagResult
    }


    /**
     * Internal evaluation of equality for the value (e.g. "Password Vault") against the targets
     * of the specified account.
     *
     * @param value     Expression value.
     * @param account   Account for the evaluation.
     * @return          Whether the evaluation is true or false.
     */
    private fun evaluateTargetEqualityInternal(value: String, account: Account): Boolean {
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

        return targetResult
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
                        "createdAt" -> dateEvaluatorService.evaluateDate(account.metadata.createdAt.toLocalDate(), valueDate, operator)
                        "editedAt" -> dateEvaluatorService.evaluateDate(account.metadata.editedAt.toLocalDate(), valueDate, operator)
                        else -> false
                    }
                }
                "<>" -> {
                    !evaluateEqualityInternal(field, value, account)
                }
                else -> false
            }
        }
        catch (_: Exception) {
            return false
        }
    }

}
