package de.christian2003.feature.search.application.usecases

import de.christian2003.data.accounts.application.usecases.GetAccountByIdUseCase
import de.christian2003.data.accounts.application.usecases.GetAllAccountDescriptorsUseCase
import de.christian2003.data.accounts.domain.entities.Account
import de.christian2003.data.accounts.domain.entities.AccountDescriptor
import de.christian2003.data.accounts.domain.entities.Detail
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


/**
 * Use case to perform a search operation.
 *
 * @param queryTokenizerService             Service to tokenize a query.
 * @param queryParserService                Service to parse a query.
 * @param queryAccountEvaluatorService      Service to evaluate a query for an account.
 * @param queryDetailEvaluatorService       Service to evaluate a query for an account detail.
 * @param getAllAccountDescriptorsUseCase   Use case to get a list of all account descriptors.
 * @param getAccountByIdUseCase             Use case to get an account by it's ID.
 */
internal class SearchUseCase @Inject constructor(
    private val queryTokenizerService: QueryTokenizerService,
    private val queryParserService: QueryParserService,
    private val queryAccountEvaluatorService: QueryEvaluatorService<Account>,
    private val queryDetailEvaluatorService: QueryEvaluatorService<Detail>,
    private val getAllAccountDescriptorsUseCase: GetAllAccountDescriptorsUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase
) {

    /**
     * Performs the search operations for the specified query.
     *
     * @param query             Search query.
     * @param isFreeTextSearch  Whether the query is a free-text search.
     * @return                  Search result for the provided query.
     */
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


    /**
     * Parses the provided query and returns it's AST.
     *
     * @param query             Query to parse.
     * @param isFreeTextSearch  Whether the query should be treated as free-text search.
     * @return                  AST for the query.
     */
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


    /**
     * Tests whether the provided query is a simple free-text search.
     *
     * @param query Query to test.
     * @return      Whether the query is a free-text search.
     */
    private fun isQuerySimpleFreeText(query: String): Boolean {
        return !query.contains(':')
                && !query.contains("(")
                && !query.contains(")")
                && !Regex("\\bAND\\b|\\bOR\\b", RegexOption.IGNORE_CASE).containsMatchIn(query)
                && !Regex("<=|>=|<>|<|>").containsMatchIn(query)
    }


    /**
     * Generates a search result for the specified account descriptor. If the account does not match
     * the provided query, null is returned.
     *
     * @param queryAst          AST modelling the search query.
     * @param accountDescriptor Account descriptor.
     * @return                  Search result or null.
     */
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
