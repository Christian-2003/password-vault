package de.christian2003.feature.search.application.usecases

import de.christian2003.data.accounts.application.usecases.GetAccountByIdUseCase
import de.christian2003.data.accounts.application.usecases.GetAllAccountDescriptorsUseCase
import de.christian2003.data.accounts.domain.entities.Account
import de.christian2003.data.accounts.domain.entities.AccountDescriptor
import de.christian2003.feature.search.application.services.QueryEvaluatorService
import de.christian2003.feature.search.application.services.QueryParserService
import de.christian2003.feature.search.application.services.QueryTokenizerService
import de.christian2003.feature.search.domain.entities.AccountSearchResult
import de.christian2003.feature.search.domain.entities.QueryAstNode
import de.christian2003.feature.search.domain.entities.QueryTokenCollection
import de.christian2003.feature.search.domain.entities.SearchResult
import kotlinx.coroutines.flow.first
import javax.inject.Inject


internal class SearchUseCase @Inject constructor(
    private val queryTokenizerService: QueryTokenizerService,
    private val queryParserService: QueryParserService,
    private val queryEvaluatorService: QueryEvaluatorService,
    private val getAllAccountDescriptorsUseCase: GetAllAccountDescriptorsUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase
) {

    suspend fun search(query: String): SearchResult {
        val queryAst: QueryAstNode = parseQuery(query)
        val accountDescriptors: List<AccountDescriptor> = getAllAccountDescriptorsUseCase.getAllAccountDescriptors().first()
        val accountResults: MutableList<AccountSearchResult> = mutableListOf()

        accountDescriptors.forEach { accountDescriptor ->
            val result: Boolean = doesAccountDescriptorMatchQuery(queryAst, accountDescriptor)
            if (result) {
                val searchResult = AccountSearchResult(
                    accountDescriptor = accountDescriptor
                )
                accountResults.add(searchResult)

            }
        }

        return SearchResult(accountResults)
    }


    private fun parseQuery(query: String): QueryAstNode {
        val tokens: QueryTokenCollection = queryTokenizerService.tokenize(query)
        val ast: QueryAstNode? = queryParserService.parse(tokens)
        if (ast == null) {
            throw IllegalArgumentException("Invalid query")
        }
        return ast
    }


    private suspend fun doesAccountDescriptorMatchQuery(queryAst: QueryAstNode, accountDescriptor: AccountDescriptor): Boolean {
        val account: Account? = getAccountByIdUseCase.getAccountById(accountDescriptor.id)
        if (account != null) {
            val result: Boolean = queryEvaluatorService.evaluate(queryAst, account)
            return result
        }
        return false
    }

}
