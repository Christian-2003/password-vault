package de.christian2003.passwordvault.plugin.presentation.view.tag

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.passwordvault.domain.entry.Tag
import de.christian2003.passwordvault.domain.repository.TagRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class TagViewModel(): ViewModel() {

    private lateinit var tagRepository: TagRepository

    private var isInitialized: Boolean = false

    lateinit var tags: Flow<List<Tag>>

    var selectedTags: MutableList<Tag> = mutableStateListOf()

    var tagToDelete: Tag? by mutableStateOf(null)

    var isCreateTagDialogVisible: Boolean by mutableStateOf(false)

    var tagToEdit: Tag? by mutableStateOf(null)



    fun init(tagRepository: TagRepository, selectedTags: List<Tag>) {
        if (isInitialized) {
            return
        }
        this.tagRepository = tagRepository
        this.selectedTags.addAll(selectedTags)
        isInitialized = true
        viewModelScope.launch(Dispatchers.IO) {
            tags = tagRepository.getAllTags()
        }
    }


    fun deleteTag() = viewModelScope.launch(Dispatchers.IO) {
        val tag: Tag? = this@TagViewModel.tagToDelete
        if (tag != null) {
            this@TagViewModel.tagToDelete = null
            tagRepository.deleteTag(tag)
            selectedTags.remove(tag)
        }
    }


    fun saveTag(tag: Tag) = viewModelScope.launch(Dispatchers.IO) {
        tagRepository.updateTag(tag)
    }


    fun createTag(tag: Tag) = viewModelScope.launch(Dispatchers.IO) {
        tagRepository.createTag(tag)
    }

}
