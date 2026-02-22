package de.christian2003.feature.search.presentation.viewmodels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.feature.search.application.usecases.SearchUseCase
import de.christian2003.feature.search.domain.entities.SearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
internal class SearchViewModel @Inject constructor(
    application: Application,
    private val searchUseCase: SearchUseCase
) : AndroidViewModel(application) {

    var query: String by mutableStateOf("")

    var searchResult: SearchResult? by mutableStateOf(null)
        private set

    var isQueryInvalid: Boolean by mutableStateOf(false)
        private set

    var isSearching: Boolean by mutableStateOf(false)
        private set

    var isSearchingFinished: Boolean by mutableStateOf(false)
        private set


    fun startSearch() = viewModelScope.launch(Dispatchers.IO) {
        if (!isSearching) {
            isSearching = true
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

}
