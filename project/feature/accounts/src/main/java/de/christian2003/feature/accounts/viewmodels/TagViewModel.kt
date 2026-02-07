package de.christian2003.feature.accounts.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.core.ui.model.HelpCard
import de.christian2003.data.accounts.application.usecases.CreateTagUseCase
import de.christian2003.data.accounts.application.usecases.DeleteTagUseCase
import de.christian2003.data.accounts.application.usecases.GetAllTagsUseCase
import de.christian2003.data.accounts.application.usecases.UpdateTagUseCase
import de.christian2003.feature.accounts.models.dto.TagUiDto
import de.christian2003.feature.accounts.models.dto.TagUiMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.uuid.Uuid


/**
 * View model for the sheet through which to select tags for an entry.
 *
 * @param application       Application.
 * @param getAllTagsUseCase Use case to get a list of all available tags.
 * @param createTagUseCase  Use case to create a new tag.
 * @param updateTagUseCase  Use case to update an existing tag.
 * @param deleteTagUseCase  Use case to delete an existing tag.
 */
@HiltViewModel
class TagViewModel @Inject constructor(
    application: Application,
    getAllTagsUseCase: GetAllTagsUseCase,
    private val createTagUseCase: CreateTagUseCase,
    private val updateTagUseCase: UpdateTagUseCase,
    private val deleteTagUseCase: DeleteTagUseCase,
): AndroidViewModel(application) {

    /**
     * Mapper used to map tags to the DTO used in the UI and vice versa.
     */
    private val tagMapper: TagUiMapper = TagUiMapper()

    /**
     * Tag IDs that were selected when the init-function was called.
     */
    private var selectedTagIdsAtInit: Set<Uuid>? = null

    /**
     * List of all tags that are available within the app.
     */
    val tags: Flow<List<TagUiDto>> = getAllTagsUseCase.getAllTags().map { list ->
        list.map { domain ->
            val dto: TagUiDto = tagMapper.toDto(domain)
            return@map dto
        }
    }

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
     * Indicates whether the help card is visible.
     */
    var isHelpCardVisible: Boolean by mutableStateOf(HelpCard.Tags.getVisible(application))
        private set


    /**
     * Initializes the view model.
     *
     * @param selectedTagIds    Set of tags that are currently selected.
     */
    fun init(selectedTagIds: Set<Uuid>) {
        if (selectedTagIdsAtInit == null) {
            this.selectedTagIds.clear()
            this.selectedTagIds.addAll(selectedTagIds)
            this.selectedTagIdsAtInit = selectedTagIds
        }
    }


    /**
     * Determines whether changes were made to the selected tags.
     *
     * @param allTags   List of all tags that are available to the sheet.
     * @return          Whether changes were made.
     */
    fun areChangesMade(allTags: List<TagUiDto>): Boolean {
        selectedTagIds.forEach { selectedTagId ->
            if (!selectedTagIdsAtInit!!.contains(selectedTagId)) {
                return true //Tag added
            }
        }
        selectedTagIdsAtInit!!.forEach { tagId ->
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


    /**
     * Dismisses the help card.
     */
    fun dismissHelpCard() {
        HelpCard.Tags.setVisible(application, false)
        isHelpCardVisible = false
    }

}
