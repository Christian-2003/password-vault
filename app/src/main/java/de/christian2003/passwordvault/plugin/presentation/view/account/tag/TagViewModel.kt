package de.christian2003.passwordvault.plugin.presentation.view.account.tag

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.passwordvault.application.usecases.tag.CreateTagUseCase
import de.christian2003.passwordvault.application.usecases.tag.DeleteTagUseCase
import de.christian2003.passwordvault.application.usecases.tag.GetAllTagsUseCase
import de.christian2003.passwordvault.application.usecases.tag.UpdateTagUseCase
import de.christian2003.passwordvault.plugin.presentation.view.account.TagUiDto
import de.christian2003.passwordvault.plugin.presentation.view.account.TagUiMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid


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
     * Stores the tag IDs that were selected when the init-function was called.
     */
    private lateinit var selectedTagIdsAtInit: Set<Uuid>

    /**
     * Whether the view model has been initialized.
     */
    private var isInitialized: Boolean = false

    /**
     * List of all tags that are available within the app.
     */
    lateinit var tags: Flow<List<TagUiDto>>

    /**
     * Set of the IDs of all selected tags.
     */
    val selectedTagIds: MutableSet<Uuid> = mutableStateSetOf()

    /**
     * Stores the tag to delete. If this is null, no tag is currently being deleted.
     */
    var tagToDelete: TagUiDto? by mutableStateOf(null)

    /**
     * Stores the tag to edit. If this is null, no tag is currently being edited.
     */
    var tagToEdit: TagUiDto? by mutableStateOf(null)

    /**
     * Indicates whether the dialog to discard without saving is visible.
     */
    var isDiscardDialogVisible: Boolean by mutableStateOf(false)

    /**
     * Indicates whether the dialog to create a new tag is currently visible.
     */
    var isCreateTagDialogVisible: Boolean by mutableStateOf(false)


    /**
     * Initializes the view model.
     *
     * @param selectedTagIds    Set of tags that are currently selected.
     */
    fun init(
        getAllTagsUseCase: GetAllTagsUseCase,
        createTagUseCase: CreateTagUseCase,
        updateTagUseCase: UpdateTagUseCase,
        deleteTagUseCase: DeleteTagUseCase,
        selectedTagIds: Set<Uuid>
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
        this.selectedTagIds.clear()
        this.selectedTagIds.addAll(selectedTagIds)
        this.selectedTagIdsAtInit = selectedTagIds
        isInitialized = true
    }


    /**
     * Determines whether changes were made to the selected tags.
     *
     * @param allTags   List of all tags that are available to the sheet.
     * @return          Whether changes were made.
     */
    fun areChangesMade(allTags: List<TagUiDto>): Boolean {
        selectedTagIds.forEach { selectedTagId ->
            if (!selectedTagIdsAtInit.contains(selectedTagId)) {
                return true //Tag added
            }
        }
        selectedTagIdsAtInit.forEach { tagId ->
            if (!selectedTagIds.contains(tagId)) {
                if (allTags.find { it.id == tagId } != null) {
                    return true //Tag removed
                }
            }
        }
        return false
    }


    /**
     * Deletes the tag that is currently stored in "tagToDelete". After the function finishes,
     * the attribute "tagToDelete" will be set to null.
     */
    fun deleteTag() = viewModelScope.launch(Dispatchers.IO) {
        val tag: TagUiDto? = this@TagViewModel.tagToDelete
        this@TagViewModel.tagToDelete = null
        if (tag != null) {
            deselectTag(tag.id)
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
     * Selects the tag whose ID is passed as argument.
     *
     * @param tagId ID of the tag to select.
     */
    fun selectTag(tagId: Uuid) {
        selectedTagIds.add(tagId)
    }


    /**
     * Deselects the tag whose ID is passed as argument.
     *
     * @param tagId ID of the tag to deselect.
     */
    fun deselectTag(tagId: Uuid) {
        selectedTagIds.remove(tagId)
    }


    /**
     * Determines whether the specified tag is selected.
     *
     * @param tagId ID of the tag for which to determine whether it is selected.
     * @return      Whether the tag is selected or not.
     */
    fun isTagSelected(tagId: Uuid): Boolean {
        return tagId in selectedTagIds
    }

}
