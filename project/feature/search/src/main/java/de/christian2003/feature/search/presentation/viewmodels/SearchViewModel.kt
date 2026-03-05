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
import de.christian2003.core.common.application.services.DateTimeFormatterService
import de.christian2003.data.accounts.application.usecases.GetAccountIconUseCase
import de.christian2003.data.accounts.application.usecases.GetAllTagsUseCase
import de.christian2003.data.accounts.domain.entities.AccountDescriptor
import de.christian2003.data.accounts.domain.entities.Tag
import de.christian2003.feature.search.application.usecases.AddRecentQueryUseCase
import de.christian2003.feature.search.application.usecases.GetRecentQueriesUseCase
import de.christian2003.feature.search.application.usecases.RemoveRecentQueriesUseCase
import de.christian2003.feature.search.application.usecases.SearchUseCase
import de.christian2003.feature.search.domain.entities.SearchResult
import de.christian2003.feature.search.presentation.models.dialogs.SearchScreenDialog
import de.christian2003.feature.search.presentation.models.other.FilterTimeSpan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
    private val removeRecentQueriesUseCase: RemoveRecentQueriesUseCase,
    private val dateTimeFormatterService: DateTimeFormatterService
) : AndroidViewModel(application) {

    var query: String by mutableStateOf("")

    var recentQueries: List<String> by mutableStateOf(getRecentQueriesUseCase.getQueries())
        private set

    var searchResult: SearchResult? by mutableStateOf(null)
        private set

    var isSearching: Boolean by mutableStateOf(false)
        private set

    var isSearchingFinished: Boolean by mutableStateOf(false)
        private set

    val allTags: Flow<List<Tag>> = getAllTagsUseCase.getAllTags()

    val selectedTags: MutableSet<Uuid> = mutableStateSetOf()

    var editedTimeSpan: FilterTimeSpan by mutableStateOf(FilterTimeSpan.All)

    var createdTimeSpan: FilterTimeSpan by mutableStateOf(FilterTimeSpan.All)

    var dialog: SearchScreenDialog by mutableStateOf(SearchScreenDialog.None)



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
            }
            catch (_: Exception) {
                //The query entered is not a simple free-text search, since it contains words or elements
                //of the query language that are used incorrectly. Therefore, we assume that the query
                //is simply a more complex free-text-search:
                try {
                    val searchResult: SearchResult = searchUseCase.search(extendedQuery, true)
                    this@SearchViewModel.searchResult = searchResult
                }
                catch (e: Exception) {
                    //Some error occurred that cannot be recovered:
                    Log.d("Search", "Irrecoverable error: ${e.message ?: "Unknown"}")
                }
            }
            isSearchingFinished = true
            isSearching = false
        }
    }


    fun queryAccountIcon(accountDescriptor: AccountDescriptor): Drawable? {
        return getAccountIconUseCase.getAccountIcon(accountDescriptor)
    }


    fun formatDate(date: LocalDate): String {
        return dateTimeFormatterService.format(date)
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
        if (query.isNotBlank()) {
            queryBuilder.append("($query)")
        }

        //tags should be '(tag:"A" OR tag:"B" OR tag:"C")'
        val queryForTags: String = buildExtendedQueryForTags()
        if (queryForTags.isNotEmpty()) {
            if (queryBuilder.isNotEmpty()) {
                queryBuilder.append(" AND ")
            }
            queryBuilder.append(queryForTags)
        }

        val queryForEdited: String = buildExtendedQueryForTimeSpan("editedAt", editedTimeSpan)
        if (queryForEdited.isNotEmpty()) {
            if (queryBuilder.isNotEmpty()) {
                queryBuilder.append(" AND ")
            }
            queryBuilder.append(queryForEdited)
        }
        val queryForCreated: String = buildExtendedQueryForTimeSpan("createdAt", createdTimeSpan)
        if (queryForCreated.isNotEmpty()) {
            if (queryBuilder.isNotEmpty()) {
                queryBuilder.append(" AND ")
            }
            queryBuilder.append(queryForCreated)
        }

        return queryBuilder.toString()
    }


    /**
     * Builds the extended query for the selected tags of the following format:
     * '(tag:"a" OR tag:"b" OR tag:"c" OR tag:"d")'
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
            queryBuilder.append("(")

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


    private fun buildExtendedQueryForTimeSpan(field: String, timeSpan: FilterTimeSpan): String {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return when {
            timeSpan.start == null && timeSpan.end != null -> {
                //Before end date:
                "$field:<=${timeSpan.end.format(formatter)}"
            }
            timeSpan.start != null && timeSpan.end == null -> {
                //After start date:
                "$field:>=${timeSpan.start.format(formatter)}"
            }
            timeSpan.start != null && timeSpan.end != null -> {
                if (timeSpan.start == timeSpan.end) {
                    //Single day:
                    "$field:${timeSpan.start.format(formatter)}"
                }
                else {
                    //Between dates:
                    "($field:>=${timeSpan.start.format(formatter)} AND $field:<=${timeSpan.end.format(formatter)})"
                }
            }
            else -> {
                //Any dates - no filter required:
                ""
            }
        }
    }

}
