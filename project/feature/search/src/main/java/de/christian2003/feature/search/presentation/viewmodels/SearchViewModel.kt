package de.christian2003.feature.search.presentation.viewmodels

import android.app.Application
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.data.accounts.application.usecases.GetAccountIconUseCase
import de.christian2003.data.accounts.application.usecases.GetAllTagsUseCase
import de.christian2003.data.accounts.domain.entities.AccountDescriptor
import de.christian2003.data.accounts.domain.entities.Tag
import de.christian2003.feature.search.application.usecases.AddRecentQueryUseCase
import de.christian2003.feature.search.application.usecases.GetRecentQueriesUseCase
import de.christian2003.feature.search.application.usecases.RemoveRecentQueriesUseCase
import de.christian2003.feature.search.application.usecases.SearchUseCase
import de.christian2003.feature.search.domain.entities.SearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.uuid.Uuid


@HiltViewModel
internal class SearchViewModel @Inject constructor(
    application: Application,
    getAllTagsUseCase: GetAllTagsUseCase,
    private val searchUseCase: SearchUseCase,
    private val getAccountIconUseCase: GetAccountIconUseCase,
    private val getRecentQueriesUseCase: GetRecentQueriesUseCase,
    private val addRecentQueryUseCase: AddRecentQueryUseCase,
    private val removeRecentQueriesUseCase: RemoveRecentQueriesUseCase
) : AndroidViewModel(application) {

    var query: String by mutableStateOf("")

    var recentQueries: List<String> by mutableStateOf(getRecentQueriesUseCase.getQueries())
        private set

    var searchResult: SearchResult? by mutableStateOf(null)
        private set

    var isQueryInvalid: Boolean by mutableStateOf(false)
        private set

    var isSearching: Boolean by mutableStateOf(false)
        private set

    var isSearchingFinished: Boolean by mutableStateOf(false)
        private set

    val allTags: Flow<List<Tag>> = getAllTagsUseCase.getAllTags()

    val selectedTags: MutableSet<Uuid> = mutableStateSetOf()


    fun startSearch() = viewModelScope.launch(Dispatchers.IO) {
        //Query entered by user:
        val query: String = this@SearchViewModel.query

        if (!isSearching) {
            isSearching = true

            //Query extended with filters provided through UI:
            val extendedQuery: String = buildExtendedQuery(query)

            if (query.isNotBlank() && !recentQueries.contains(query)) {
                addRecentQueryUseCase.addRecentQuery(query)
            }
            try {
                val searchResult: SearchResult = searchUseCase.search(extendedQuery, false)
                this@SearchViewModel.searchResult = searchResult
                isQueryInvalid = false
            }
            catch (_: Exception) {
                //The query entered is not a simple free-text search, since it contains words or elements
                //of the query language that are used incorrectly. Therefore, we assume that the query
                //is simply a more complex free-text-search:
                try {
                    val searchResult: SearchResult = searchUseCase.search(extendedQuery, true)
                    this@SearchViewModel.searchResult = searchResult
                    isQueryInvalid = false
                }
                catch (e: Exception) {
                    //Some error occurred that cannot be recovered:
                    Log.d("Search", "Irrecoverable error: ${e.message ?: "Unknown"}")
                    isQueryInvalid = true
                }
            }
            isSearchingFinished = true
            isSearching = false
        }
    }


    fun queryAccountIcon(accountDescriptor: AccountDescriptor): Drawable? {
        return getAccountIconUseCase.getAccountIcon(accountDescriptor)
    }

    fun toggleTag(tagId: Uuid) {
        if (selectedTags.contains(tagId))  {
            selectedTags.remove(tagId)
        }
        else {
            selectedTags.add(tagId)
        }
    }

    fun removeRecentQueries() {
        removeRecentQueriesUseCase.removeRecentQueries()
        recentQueries = getRecentQueriesUseCase.getQueries()
    }


    private suspend fun buildExtendedQuery(query: String): String {
        val queryBuilder = StringBuilder()
        queryBuilder.append("($query)")

        //tags should be AND (tag:A OR tag:B OR tag:C)
        val queryForTags: String = buildExtendedQueryForTags()
        if (queryForTags.isNotEmpty()) {
            queryBuilder.append(queryForTags)
        }

        return queryBuilder.toString()
    }


    /**
     * Builds the extended query for the selected tags of the following format:
     * "AND (tag:a OR tag:b OR tag:c OR tag:d)"
     * If no tags are selected, an empty string is returned.
     *
     * @return  Extended query for the tags.
     */
    private suspend fun buildExtendedQueryForTags(): String {
        val queryBuilder = StringBuilder()

        //Get all selected tags:
        val tags: MutableList<Tag> = mutableListOf()
        selectedTags.forEach { tagId ->
            val tag: Tag? = allTags.first().find { it.id == tagId }
            if (tag != null) {
                tags.add(tag)
            }
        }

        //Build query for tags:
        if (tags.isNotEmpty()) {
            queryBuilder.append(" AND (")

            tags.forEachIndexed { index, tag ->
                if (index == 0) {
                    queryBuilder.append("tag:\"${tag.name}\"")
                }
                else {
                    queryBuilder.append(" OR tag:\"${tag.name}\"")
                }
            }

            queryBuilder.append(")")
        }

        return queryBuilder.toString()
    }

}
