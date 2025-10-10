package de.christian2003.passwordvault.plugin.presentation.view.tag

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.passwordvault.domain.model.tag.Tag
import de.christian2003.passwordvault.application.usecases.tag.CreateTagUseCase
import de.christian2003.passwordvault.application.usecases.tag.DeleteTagUseCase
import de.christian2003.passwordvault.application.usecases.tag.GetAllTagsUseCase
import de.christian2003.passwordvault.application.usecases.tag.UpdateTagUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


/**
 * View model for the sheet through which to select tags for an entry.
 */
class TagViewModel(): ViewModel() {

    /**
     * Use case to create a new tag.
     */
    private lateinit var createTagUseCase: CreateTagUseCase

    /**
     * Use case to update an existing tag.
     */
    private lateinit var updateTagUseCase: UpdateTagUseCase

    /**
     * Use case to delete a tag.
     */
    private lateinit var deleteTagUseCase: DeleteTagUseCase

    /**
     * Mapper used to map tags to the DTO used in the UI and vice versa.
     */
    private var tagMapper: TagUiMapper = TagUiMapper()

    /**
     * Whether the view model has been initialized.
     */
    private var isInitialized: Boolean = false

    /**
     * List of all tags that are available within the app.
     */
    lateinit var tags: Flow<List<TagUiDto>>

    /**
     * List of all tags that are selected currently.
     */
    var selectedTags: MutableList<Tag> = mutableStateListOf()

    /**
     * Stores the tag to delete. If this is null, no tag is currently being deleted.
     */
    var tagToDelete: TagUiDto? by mutableStateOf(null)

    /**
     * Stores the tag to edit. If this is null, no tag is currently being edited.
     */
    var tagToEdit: TagUiDto? by mutableStateOf(null)

    /**
     * Indicates whether the dialog to create a new tag is currently visible.
     */
    var isCreateTagDialogVisible: Boolean by mutableStateOf(false)


    /**
     * Initializes the view model.
     *
     * @param selectedTags  List of tags that are currently selected.
     */
    fun init(
        getAllTagsUseCase: GetAllTagsUseCase,
        createTagUseCase: CreateTagUseCase,
        updateTagUseCase: UpdateTagUseCase,
        deleteTagUseCase: DeleteTagUseCase,
        selectedTags: List<Tag>
    ) {
        if (isInitialized) {
            return
        }
        this.tags = getAllTagsUseCase.getAllTags().map { list ->
            list.map { domain ->
                val dto: TagUiDto = tagMapper.toDto(domain)
                return@map dto
            }
        }
        this.createTagUseCase = createTagUseCase
        this.updateTagUseCase = updateTagUseCase
        this.deleteTagUseCase = deleteTagUseCase
        this.selectedTags.addAll(selectedTags)
        isInitialized = true
    }


    /**
     * Deletes the tag that is currently stored in "tagToDelete". After the function finishes,
     * the attribute "tagToDelete" will be set to null.
     */
    fun deleteTag() = viewModelScope.launch(Dispatchers.IO) {
        val tag: TagUiDto? = this@TagViewModel.tagToDelete
        this@TagViewModel.tagToDelete = null
        if (tag != null) {
            deselectTag(tag)
            deleteTagUseCase.deleteTag(tag.id)
        }
    }


    /**
     * Saves the tag that is passed as argument.
     */
    fun saveTag(tagName: String) = viewModelScope.launch(Dispatchers.IO) {
        val tagToEdit: TagUiDto? = this@TagViewModel.tagToEdit
        this@TagViewModel.tagToEdit = null
        if (tagToEdit != null) {
            updateTagUseCase.updateTag(
                id = tagToEdit.id,
                name = tagName
            )
        }
    }


    /**
     * Creates the new tag that is passed as argument.
     */
    fun createTag(tagName: String) = viewModelScope.launch(Dispatchers.IO) {
        createTagUseCase.createTag(
            name = tagName
        )
    }


    /**
     * Selects the tag that is passed as argument.
     *
     * @param tag   Tag to select.
     */
    fun selectTag(tag: TagUiDto) {
        selectedTags.forEach { selectedTag ->
            if (selectedTag.id == tag.id) {
                selectedTags.remove(selectedTag)
                return@forEach
            }
        }
        selectedTags.add(tagMapper.toDomain(tag))
    }


    /**
     * Deselects the tag that is passed as argument.
     *
     * @param tag   Tag to deselect.
     */
    fun deselectTag(tag: TagUiDto) {
        selectedTags.forEach { selectedTag ->
            if (selectedTag.id == tag.id) {
                selectedTags.remove(selectedTag)
                return
            }
        }
    }


    /**
     * Determines whether the specified tag is selected.
     *
     * @param tag   Tag for which to determine whether it is selected.
     * @return      Whether the tag is selected or not.
     */
    fun isTagSelected(tag: TagUiDto): Boolean {
        selectedTags.forEach { selectedTag ->
            if (selectedTag.id == tag.id) {
                return true
            }
        }
        return false
    }

}
