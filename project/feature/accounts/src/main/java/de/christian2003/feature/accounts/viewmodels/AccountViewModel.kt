package de.christian2003.feature.accounts.viewmodels

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.core.common.domain.services.ClipboardService
import de.christian2003.core.ui.model.HelpCard
import de.christian2003.data.accounts.application.usecases.CreateAccountUseCase
import de.christian2003.data.accounts.application.usecases.GetAccountByIdUseCase
import de.christian2003.data.accounts.application.usecases.GetAccountIconUseCase
import de.christian2003.data.accounts.application.usecases.GetAllTagsUseCase
import de.christian2003.data.accounts.application.usecases.UpdateAccountUseCase
import de.christian2003.data.accounts.domain.entities.Account
import de.christian2003.data.accounts.domain.entities.Detail
import de.christian2003.data.accounts.domain.entities.Tag
import de.christian2003.data.accounts.domain.entities.Target
import de.christian2003.feature.accounts.models.dialogs.AccountScreenDialog
import de.christian2003.feature.accounts.models.states.AccountScreenHelpState
import de.christian2003.feature.accounts.models.states.AccountScreenState
import de.christian2003.feature.accounts.models.dto.TagUiDto
import de.christian2003.feature.accounts.models.dto.TagUiMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.collections.map
import kotlin.collections.toSet
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid


/**
 * View model for the screen through which to create, edit or view an account.
 *
 * @param application           Application.
 * @param savedStateHandle      Saved state handle.
 * @param getAccountByIdUseCase Use case to get an account by it's ID.
 * @param getAllTagsUseCase     Use case to get a list of all tags.
 * @param createAccountUseCase  Use case to create a new account.
 * @param updateAccountUseCase  Use case to update an existing account.
 * @param getAccountIconUseCase Use case to get the icon for an account.
 * @param clipboardService      Service to copy content to the clipboard.
 */
@HiltViewModel
internal class AccountViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    getAccountByIdUseCase: GetAccountByIdUseCase,
    getAllTagsUseCase: GetAllTagsUseCase,
    private val createAccountUseCase: CreateAccountUseCase,
    private val updateAccountUseCase: UpdateAccountUseCase,
    private val getAccountIconUseCase: GetAccountIconUseCase,
    private val clipboardService: ClipboardService
): AndroidViewModel(application) {

    /**
     * Mapper to map tags from their domain model to the UI DTO.
     */
    private val tagMapper: TagUiMapper = TagUiMapper()

    /**
     * Account that is being edited. If a new account is being created, this is null.
     */
    private var account: Account? = null

    /**
     * IDs of the tags that are selected for the account.
     */
    private val selectedTagIds: MutableStateFlow<Set<Uuid>> = MutableStateFlow(emptySet())

    /**
     * Flow contains all tags that are available.
     */
    val allTags: Flow<List<TagUiDto>> = getAllTagsUseCase.getAllTags().map { list ->
        list.map { tag -> tagMapper.toDto(tag)}
    }

    /**
     * Name of the account.
     */
    var name: String by mutableStateOf("")

    /**
     * Description of the account.
     */
    var description: String by mutableStateOf("")

    /**
     * Details of the account.
     */
    val details: MutableList<Detail> = mutableStateListOf()

    /**
     * List of visible details. This is a sublist of "details".
     */
    val visibleDetails: State<List<Detail>> = derivedStateOf {
        val visibleDetails: MutableList<Detail> = mutableListOf()
        details.forEach { detail ->
            if (detail.metadata.isVisible) {
                visibleDetails.add(detail)
            }
        }
        return@derivedStateOf visibleDetails
    }

    /**
     * List of invisible details. This is a sublist of "details".
     */
    val invisibleDetails: State<List<Detail>> = derivedStateOf {
        val invisibleDetails: MutableList<Detail> = mutableListOf()
        details.forEach { detail ->
            if (!detail.metadata.isVisible) {
                invisibleDetails.add(detail)
            }
        }
        return@derivedStateOf invisibleDetails
    }

    /**
     * Targets of the account.
     */
    val targets: MutableList<Target> = mutableStateListOf()

    /**
     * Icon of the account. If no icon is available, this is null.
     */
    val icon: State<Drawable?> = derivedStateOf {
        getAccountIconUseCase.getAccountIcon(targets)
    }

    /**
     * IDs of the details that are selected in the multiselect state.
     */
    val selectedDetailIds: MutableList<Uuid> = mutableStateListOf()

    /**
     * Current screen state (i.e. Default, Reorder or Multiselect).
     */
    var screenState: AccountScreenState by mutableStateOf(AccountScreenState.Default)
        private set

    /**
     * Dialog that is currently visible to the user.
     */
    var visibleDialog: AccountScreenDialog by mutableStateOf(AccountScreenDialog.None)

    /**
     * Indicates whether the data entered is valid. This is used to determine whether the account
     * can be saved.
     */
    val isDataValid: State<Boolean> = derivedStateOf {
        name.isNotBlank()
    }

    /**
     * Indicates whether the invisible details are expanded and currently visible.
     */
    var areInvisibleDetailsVisible: Boolean by mutableStateOf(false)

    /**
     * Detail that is currently being edited.
     */
    var detailToEdit: Detail? by mutableStateOf(null)

    /**
     * Detail that is currently being deleted.
     */
    var detailToDelete: Detail? by mutableStateOf(null)

    /**
     * Tags that are currently selected. The list inside the flow is generated automatically based
     * on "selectedTagIds".
     */
    val selectedTags: StateFlow<List<TagUiDto>> = combine(allTags, selectedTagIds) { all, ids ->
        all.filter { it.id in ids }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    /**
     * Help state the screen is currently in. This indicates the help message that is currently
     * shown to the user. If no help message should be shown (e.g. because they were dismissed or
     * the screen is opened to edit / show an account), this is null.
     */
    var helpState: AccountScreenHelpState? by mutableStateOf(if (HelpCard.Account.getVisible(application)) {
        AccountScreenHelpState.Name
    } else {
        null
    })

    /**
     * View model store ID used to remember the view models for the sheets through which to edit
     * details, tags and targets.
     */
    var viewModelStoreId: Int = 0


    /**
     * Initializes the view model.
     */
    init {
        val id: Uuid? = try {
            UUID.fromString(savedStateHandle["accountId"]).toKotlinUuid()
        } catch (_: Exception) {
            null
        }

        viewModelScope.launch(Dispatchers.IO) {
            if (id != null) {
                val account: Account? = getAccountByIdUseCase.getAccountById(id)
                this@AccountViewModel.account = account
                if (account == null) {
                    name = ""
                    description = ""
                    details.clear()
                    targets.clear()
                    clearSelectedTags()
                }
                else {
                    helpState = null
                    name = account.descriptor.name
                    description = account.descriptor.description
                    details.clear()
                    details.addAll(account.details)
                    targets.clear()
                    targets.addAll(account.targets)
                    clearSelectedTags()
                    account.tags.forEach { tag ->
                        toggleTagSelection(tag.id)
                    }
                }
            }
        }
    }


    /**
     * Saves the account.
     */
    fun save() = viewModelScope.launch(Dispatchers.IO) {
        if (name.isNotBlank() && (description.isEmpty() || description.isNotBlank())) {
            val account: Account? = this@AccountViewModel.account
            val tags: List<Tag> = selectedTags.value.map { tag -> tagMapper.toDomain(tag) }
            if (account == null) {
                //Create new account:
                createAccountUseCase.createAccount(
                    name = name,
                    description = description,
                    details = details,
                    tags = tags,
                    targets = targets
                )
            }
            else {
                //Edit existing account:
                updateAccountUseCase.updateAccount(
                    id = account.descriptor.id,
                    name = name,
                    description = description,
                    details = details,
                    tags = tags,
                    targets = targets
                )
            }
        }
    }


    /**
     * Copies the content of the specified detail to the clipboard.
     *
     * @param detail    Detail whose content to copy to the clipboard.
     */
    fun copyToClipboard(detail: Detail) = viewModelScope.launch(Dispatchers.IO) {
        clipboardService.copyToClipboard(
            label = detail.name,
            data = detail.content,
            isSensitive = detail.metadata.isObfuscated
        )
    }


    /**
     * Starts the reorder state.
     */
    fun startReorderableState() {
        screenState = AccountScreenState.Reorder
        determineNextHelpState()
    }


    /**
     * Dismisses the reorder state.
     */
    fun dismissReorderableState() {
        screenState = AccountScreenState.Default
        determineNextHelpState()
    }


    /**
     * Starts the multiselect state.
     */
    fun startMultiselectState(selectedDetailId: Uuid) {
        selectedDetailIds.add(selectedDetailId)
        screenState = AccountScreenState.Multiselect
        determineNextHelpState()
    }


    /**
     * Dismisses the multiselect state.
     */
    fun dismissMultiselectState() {
        screenState = AccountScreenState.Default
        selectedDetailIds.clear()
        determineNextHelpState()
    }


    /**
     * Dismisses the name dialog.
     *
     * @param name  Name to save or null to dismiss without saving.
     */
    fun dismissNameDialog(name: String? = null) {
        visibleDialog = AccountScreenDialog.None
        if (name != null) {
            this.name = name
            determineNextHelpState()
        }
    }


    /**
     * Dismisses the description dialog.
     *
     * @param description   Description to save or null to dismiss without saving.
     */
    fun dismissDescriptionDialog(description: String? = null) {
        visibleDialog = AccountScreenDialog.None
        if (description != null) {
            this.description = description
            determineNextHelpState()
        }
    }


    /**
     * Dismisses the tag dialog.
     *
     * @param selectedTagIds    List of selected tag IDs to save or null to dismiss without saving.
     */
    fun dismissTagDialog(selectedTagIds: Set<Uuid>? = null) {
        visibleDialog = AccountScreenDialog.None
        viewModelStoreId++
        if (selectedTagIds != null) {
            //Save new selected tags:
            clearSelectedTags()
            selectedTagIds.forEach { tagId ->
                toggleTagSelection(tagId)
            }
        }
    }


    /**
     * Dismisses the target dialog.
     *
     * @param targets   List of targets to save or null to dismiss without saving.
     */
    fun dismissTargetDialog(targets: List<Target>? = null) {
        visibleDialog = AccountScreenDialog.None
        viewModelStoreId++
        if (targets != null) {
            this.targets.clear()
            this.targets.addAll(targets)
            determineNextHelpState()
        }
    }


    /**
     * Dismisses the detail dialog.
     *
     * @param detail    Detail to save or null to dismiss without saving.
     */
    fun dismissDetailDialog(detail: Detail? = null) {
        if (detail != null) {
            //Save detail:
            if (visibleDialog == AccountScreenDialog.Detail) {
                //Dialog to create new detail:
                visibleDialog = AccountScreenDialog.None
                viewModelStoreId++
                details.add(detail)
            }
            else if (detailToEdit != null) {
                //Dialog to edit detail:
                detailToEdit = null
                viewModelStoreId++
                val index = details.indexOf(detail)
                if (index >= 0 && index < details.size) {
                    details.removeAt(index)
                    details.add(index, detail)
                }
            }
            determineNextHelpState()
        }
        else {
            //Dismiss dialog without saving:
            visibleDialog = AccountScreenDialog.None
            detailToEdit = null
            viewModelStoreId++
        }
    }


    /**
     * Dismisses the dialog through which to delete a detail.
     *
     * @param detailToDelete    Detail to delete or null to dismiss without deleting any detail.
     */
    fun dismissDeleteDetailDialog(detailToDelete: Detail? = null) = viewModelScope.launch(Dispatchers.Default) {
        this@AccountViewModel.detailToDelete = null
        if (detailToDelete != null) {
            details.remove(detailToDelete)
        }
    }


    /**
     * Dismisses the dialog through which to delete all details selected in multiselect state.
     *
     * @param selectedDetailIds IDs of the details to delete or null to dismiss without deleting.
     */
    fun dismissDeleteDetailsMultiselectDialog(selectedDetailIds: List<Uuid>? = null) = viewModelScope.launch(Dispatchers.Default) {
        screenState = AccountScreenState.Default
        visibleDialog = AccountScreenDialog.None
        selectedDetailIds?.forEach { id ->
            val detail: Detail? = details.find { it.id == id }
            if (detail != null) {
                details.remove(detail)
            }
        }
        this@AccountViewModel.selectedDetailIds.clear()
    }


    /**
     * Selects all available details in the multiselect state.
     */
    fun selectAllDetails() {
        details.forEach { detail ->
            if (!selectedDetailIds.contains(detail.id)) {
                selectedDetailIds.add(detail.id)
            }
        }
    }


    /**
     * Determines whether the detail with the specified ID is selected in the multiselect state.
     *
     * @param id    ID of the detail to test.
     * @return      Whether the detail of the specified ID is selected.
     */
    fun isDetailSelected(id: Uuid): Boolean {
        return id in selectedDetailIds
    }


    /**
     * Reorders the details at the specified indices. The indices are 0 based starting from the first
     * detail displayed in the user interface and correspond to their position within the LazyColumn.
     *
     * @param fromIndex Index from which to move a detail.
     * @param toIndex   Index to which to move a detail.
     */
    fun reorderDetails(fromIndex: Int, toIndex: Int) {
        var fromDetail: Detail?
        var toDetail: Detail?
        val isVisible: Boolean = toIndex < visibleDetails.value.size
        if (fromIndex >= visibleDetails.value.size) {
            val index: Int = fromIndex - visibleDetails.value.size - 1 // Subtract 1 for the "Show more" / "Show less" button
            fromDetail = invisibleDetails.value[index]
        }
        else {
            fromDetail = visibleDetails.value[fromIndex]
        }
        if (toIndex >= visibleDetails.value.size) {
            val index: Int = toIndex - visibleDetails.value.size - 1 // Subtract 1 for the "Show more" / "Show less" button
            toDetail = invisibleDetails.value[index]
        }
        else {
            toDetail = visibleDetails.value[toIndex]
        }

        val fromIndexDetail: Int = details.indexOf(fromDetail)
        val toIndexDetail: Int = details.indexOf(toDetail)

        details.apply {
            this[toIndexDetail] = this[fromIndexDetail].also {
                this[fromIndexDetail] = this[toIndexDetail]
            }
        }
        if (fromDetail.metadata.isVisible != isVisible) {
            fromDetail.metadata = fromDetail.metadata.copy(isVisible = isVisible)
        }
    }


    /**
     * Initializes the specified tag view model.
     *
     * @param viewModel     View model to initialize.
     * @param selectedTags  List of selected tags to use for initialization.
     */
    fun initTagViewModel(viewModel: TagViewModel, selectedTags: List<TagUiDto>) {
        val selectedTagIds: Set<Uuid> = selectedTags.map { it.id }.toSet()
        viewModel.init(selectedTagIds)
    }


    /**
     * Initializes the specified target view model.
     *
     * @param viewModel View model to initialize.
     */
    fun initTargetViewModel(viewModel: TargetViewModel) {
        viewModel.init(targets)
    }


    /**
     * Dismisses the help card that is displayed to the user.
     */
    fun dismissHelpCard() {
        HelpCard.Account.setVisible(application, false)
        helpState = null
    }


    /**
     * Determines whether changes were made to the account while the screen is alive.
     *
     * @param selectedTags  List of tags selected by the user.
     * @return              Whether changes were made.
     */
    fun areChangesMade(selectedTags: List<TagUiDto>): Boolean {
        val account: Account? = this.account
        if (account == null) {
            //Account created:
            return name != ""
                    || description != ""
                    || selectedTags.isNotEmpty()
                    || details.isNotEmpty()
                    || targets.isNotEmpty()
        }
        else {
            //Account edited:
            return account.descriptor.name != name
                    || account.descriptor.description != description
                    || account.tags != selectedTags.map { tag -> tagMapper.toDomain(tag) }
                    || !areDetailListsIdentical(account.details, details)
                    || account.targets != targets
        }
    }


    /**
     * Tests whether the lists of details are identical based on their actual values (not their IDs).
     *
     * @param lhs   First list.
     * @param rhs   Second list.
     * @return      Whether both lists are identical.
     */
    private fun areDetailListsIdentical(lhs: List<Detail>, rhs: List<Detail>): Boolean {
        if (lhs.size == rhs.size) {
            lhs.forEachIndexed { index, detail ->
                val other: Detail = rhs[index]
                if (detail.name != other.name
                    || detail.content != other.content
                    || detail.icon != other.icon
                    || detail.type != other.type
                    || detail.metadata.isVisible != other.metadata.isVisible
                    || detail.metadata.isObfuscated != other.metadata.isObfuscated) {
                    //Details are not identical:
                    return false
                }
            }
            //No changes made:
            return true
        }
        else {
            //Details added / removed:
            return false
        }
    }


    /**
     * Clears the list of selected tags.
     */
    private fun clearSelectedTags() {
        selectedTagIds.value = emptySet()
    }


    /**
     * Toggles whether the tag with the specified ID is selected or not.
     *
     * @param tagId ID of the tag for which to toggle selection.
     */
    private fun toggleTagSelection(tagId: Uuid) {
        selectedTagIds.update { currentIds ->
            if (tagId in currentIds) {
                currentIds - tagId
            }
            else {
                currentIds + tagId
            }
        }
    }


    /**
     * Determines the next help state for the user. This can change the displayed help message.
     */
    private fun determineNextHelpState() {
        if (helpState != null) {
            helpState = if (screenState == AccountScreenState.Multiselect) {
                AccountScreenHelpState.CloseMultiselect
            }
            else if (screenState == AccountScreenState.Reorder) {
                AccountScreenHelpState.CloseReorder
            }
            else if (name.isBlank()) {
                AccountScreenHelpState.Name
            }
            else if (description.isBlank()) {
                AccountScreenHelpState.Description
            }
            else if (details.isEmpty()) {
                AccountScreenHelpState.Details
            }
            else if (targets.isEmpty()) {
                AccountScreenHelpState.Targets
            }
            else {
                AccountScreenHelpState.Save
            }
        }
    }

}
