package de.christian2003.passwordvault.plugin.presentation.view.tag

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.passwordvault.domain.model.tag.Tag
import de.christian2003.passwordvault.application.repository.TagRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


/**
 * View model for the sheet through which to select tags for an entry.
 */
class TagViewModel(): ViewModel() {

    /**
     * Repository through which to access tags.
     */
    private lateinit var tagRepository: TagRepository

    /**
     * Whether the view model has been initialized.
     */
    private var isInitialized: Boolean = false

    /**
     * List of all tags that are available within the app.
     */
    lateinit var tags: Flow<List<Tag>>

    /**
     * List of all tags that are selected currently.
     */
    var selectedTags: MutableList<Tag> = mutableStateListOf()

    /**
     * Stores the tag to delete. If this is null, no tag is currently being deleted.
     */
    var tagToDelete: Tag? by mutableStateOf(null)

    /**
     * Stores the tag to edit. If this is null, no tag is currently being edited.
     */
    var tagToEdit: Tag? by mutableStateOf(null)

    /**
     * Indicates whether the dialog to create a new tag is currently visible.
     */
    var isCreateTagDialogVisible: Boolean by mutableStateOf(false)


    /**
     * Initializes the view model.
     *
     * @param tagRepository Repository through which to access tags.
     * @param selectedTags  List of tags that are currently selected.
     */
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


    /**
     * Deletes the tag that is currently stored in "tagToDelete". After the function finishes,
     * the attribute "tagToDelete" will be set to null.
     */
    fun deleteTag() = viewModelScope.launch(Dispatchers.IO) {
        val tag: Tag? = this@TagViewModel.tagToDelete
        if (tag != null) {
            this@TagViewModel.tagToDelete = null
            tagRepository.deleteTag(tag)
            selectedTags.remove(tag)
        }
    }


    /**
     * Saves the tag that is passed as argument.
     */
    fun saveTag(tag: Tag) = viewModelScope.launch(Dispatchers.IO) {
        tagRepository.updateTag(tag)
    }


    /**
     * Creates the new tag that is passed as argument.
     */
    fun createTag(tag: Tag) = viewModelScope.launch(Dispatchers.IO) {
        tagRepository.createTag(tag)
    }

}
