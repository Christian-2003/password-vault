package de.christian2003.feature.search.application.usecases

import de.christian2003.feature.search.application.services.QueryParserService
import de.christian2003.feature.search.application.services.QueryTokenizerService
import de.christian2003.feature.search.domain.entities.QueryAstNode
import de.christian2003.feature.search.domain.entities.QueryTokenCollection
import de.christian2003.feature.search.domain.entities.SearchResult
import javax.inject.Inject


internal class SearchUseCase @Inject constructor(
    private val queryTokenizerService: QueryTokenizerService,
    private val queryParserService: QueryParserService
) {

    fun search(query: String): SearchResult {
        return SearchResult(
            listOf()
        )
    }



    private fun parseQuery(query: String): QueryAstNode {
        val tokens: QueryTokenCollection = queryTokenizerService.tokenize(query)
        val ast: QueryAstNode? = queryParserService.parse(tokens)
        if (ast == null) {
            throw IllegalArgumentException("Invalid query")
        }
        return ast
    }

}
