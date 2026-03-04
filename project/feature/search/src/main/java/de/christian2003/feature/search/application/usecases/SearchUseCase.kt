package de.christian2003.feature.search.application.usecases

import de.christian2003.data.accounts.application.usecases.GetAccountByIdUseCase
import de.christian2003.data.accounts.application.usecases.GetAllAccountDescriptorsUseCase
import de.christian2003.data.accounts.domain.entities.Account
import de.christian2003.data.accounts.domain.entities.AccountDescriptor
import de.christian2003.data.accounts.domain.entities.Detail
import de.christian2003.feature.search.application.services.QueryDetailEvaluatorService
import de.christian2003.feature.search.application.services.QueryEvaluatorService
import de.christian2003.feature.search.application.services.QueryParserService
import de.christian2003.feature.search.application.services.QueryTokenizerService
import de.christian2003.feature.search.domain.entities.AccountSearchResult
import de.christian2003.feature.search.domain.entities.QueryAstNode
import de.christian2003.feature.search.domain.entities.QueryToken
import de.christian2003.feature.search.domain.entities.QueryTokenCollection
import de.christian2003.feature.search.domain.entities.QueryTokenType
import de.christian2003.feature.search.domain.entities.SearchResult
import kotlinx.coroutines.flow.first
import javax.inject.Inject


internal class SearchUseCase @Inject constructor(
    private val queryTokenizerService: QueryTokenizerService,
    private val queryParserService: QueryParserService,
    private val queryAccountEvaluatorService: QueryEvaluatorService<Account>,
    private val queryDetailEvaluatorService: QueryEvaluatorService<Detail>,
    private val getAllAccountDescriptorsUseCase: GetAllAccountDescriptorsUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase
) {

    suspend fun search(query: String, isFreeTextSearch: Boolean): SearchResult {
        val queryAst: QueryAstNode = parseQuery(query, isFreeTextSearch)
        val accountDescriptors: List<AccountDescriptor> = getAllAccountDescriptorsUseCase.getAllAccountDescriptors().first()
        val accountResults: MutableList<AccountSearchResult> = mutableListOf()

        accountDescriptors.forEach { accountDescriptor ->
            val result: AccountSearchResult? = getResultForAccountDescriptor(queryAst, accountDescriptor)
            if (result != null) {
                accountResults.add(result)
            }
        }

        return SearchResult(accountResults)
    }


    private fun parseQuery(query: String, isFreeTextSearch: Boolean): QueryAstNode {
        if (isFreeTextSearch || isQuerySimpleFreeText(query)) {
            //Free text entered:
            val left = QueryAstNode(
                token = QueryToken(QueryTokenType.Literal, "any"),
                left = null,
                right = null
            )
            val right = QueryAstNode(
                token = QueryToken(QueryTokenType.Literal, query),
                left = null,
                right = null
            )
            val ast = QueryAstNode(
                token = QueryToken(QueryTokenType.Colon, ":"),
                left = left,
                right = right
            )
            return ast
        }
        else {
            //Query entered:
            val tokens: QueryTokenCollection = queryTokenizerService.tokenize(query)
            val ast: QueryAstNode? = queryParserService.parse(tokens)
            if (ast == null) {
                throw IllegalArgumentException("Invalid query")
            }
            return ast
        }
    }


    private fun isQuerySimpleFreeText(query: String): Boolean {
        return !query.contains(':')
                && !query.contains("(")
                && !query.contains(")")
                && !Regex("\\bAND\\b|\\bOR\\b", RegexOption.IGNORE_CASE).containsMatchIn(query)
                && !Regex("<=|>=|<>|<|>").containsMatchIn(query)
    }


    private suspend fun getResultForAccountDescriptor(queryAst: QueryAstNode, accountDescriptor: AccountDescriptor): AccountSearchResult? {
        val account: Account? = getAccountByIdUseCase.getAccountById(accountDescriptor.id)

        if (account != null) {
            //Evaluate account:
            val resultAccount: Boolean = queryAccountEvaluatorService.evaluate(queryAst, account)

            //Evaluate details:
            val matchingDetails: MutableList<Detail> = mutableListOf()

            account.details.forEach { detail ->
                val resultDetail: Boolean = queryDetailEvaluatorService.evaluate(queryAst, detail)
                if (resultDetail) {
                    matchingDetails.add(detail)
                }
            }

            if (resultAccount || matchingDetails.isNotEmpty()) {
                val result = AccountSearchResult(
                    accountDescriptor = accountDescriptor,
                    details = matchingDetails
                )
                return result
            }
        }

        return null
    }

}
