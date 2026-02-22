package de.christian2003.feature.search.presentation.viewmodels

import android.app.Application
import android.graphics.drawable.Drawable
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
        val query: String = this@SearchViewModel.query
        if (!isSearching) {
            isSearching = true
            if (query.isNotBlank() && !recentQueries.contains(query)) {
                addRecentQueryUseCase.addRecentQuery(query)
            }
            try {
                val searchResult: SearchResult = searchUseCase.search(query)
                this@SearchViewModel.searchResult = searchResult
                isQueryInvalid = false
            }
            catch (_: Exception) {
                isQueryInvalid = true
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

}
