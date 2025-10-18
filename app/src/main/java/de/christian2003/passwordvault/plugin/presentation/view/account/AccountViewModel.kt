package de.christian2003.passwordvault.plugin.presentation.view.account

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.passwordvault.domain.model.detail.Detail
import de.christian2003.passwordvault.domain.model.account.Account
import de.christian2003.passwordvault.application.repository.TagRepository
import de.christian2003.passwordvault.application.usecases.acount.CreateAccountUseCase
import de.christian2003.passwordvault.application.usecases.acount.GetAccountByIdUseCase
import de.christian2003.passwordvault.application.usecases.acount.UpdateAccountUseCase
import de.christian2003.passwordvault.domain.model.tag.Tag
import de.christian2003.passwordvault.domain.model.target.Target
import de.christian2003.passwordvault.domain.security.ClipboardService
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
import kotlin.uuid.Uuid


class AccountViewModel(): ViewModel() {

    private lateinit var createAccountUseCase: CreateAccountUseCase

    private lateinit var updateAccountUseCase: UpdateAccountUseCase

    private lateinit var clipboardService: ClipboardService

    private val tagMapper: TagUiMapper = TagUiMapper()

    private var account: Account? = null

    private var isInitialized = false

    lateinit var tagRepository: TagRepository

    lateinit var allTags: Flow<List<TagUiDto>>

    var name: String by mutableStateOf("")

    var description: String by mutableStateOf("")

    val details: MutableList<Detail> = mutableStateListOf()

    val targets: MutableList<Target> = mutableStateListOf()

    val selectedTagIds: MutableStateFlow<Set<Uuid>> = MutableStateFlow(emptySet())

    val selectedDetailIds: MutableList<Uuid> = mutableStateListOf()

    var isInReorderableState: Boolean by mutableStateOf(false)

    var isInMultiselectState: Boolean by mutableStateOf(false)

    var isNameDialogVisible: Boolean by mutableStateOf(false)

    var isDescriptionDialogVisible: Boolean by mutableStateOf(false)

    var isTagDialogVisible: Boolean by mutableStateOf(false)

    var isDetailDialogVisible: Boolean by mutableStateOf(false)

    var isTargetDialogVisible: Boolean by mutableStateOf(false)

    var detailToEdit: Detail? by mutableStateOf(null)

    var detailToDelete: Detail? by mutableStateOf(null)

    lateinit var selectedTags: StateFlow<List<TagUiDto>>

    var viewModelStoreId: Int = 0


    fun init(
        getAccountByIdUseCase: GetAccountByIdUseCase,
        createAccountUseCase: CreateAccountUseCase,
        updateAccountUseCase: UpdateAccountUseCase,
        tagRepository: TagRepository,
        clipboardService: ClipboardService,
        id: Uuid? = null
    ) {
        if (isInitialized) {
            return
        }
        this.createAccountUseCase = createAccountUseCase
        this.updateAccountUseCase = updateAccountUseCase
        this.tagRepository = tagRepository
        this.clipboardService = clipboardService
        allTags = tagRepository.getAllTags().map { list ->
            list.map { tag -> tagMapper.toDto(tag)}
        }
        selectedTags = combine(allTags, selectedTagIds) { all, ids ->
            all.filter { it.id in ids }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
        isInitialized = true

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


    fun deleteDetail(detail: Detail) = viewModelScope.launch(Dispatchers.IO) {
        details.remove(detail)
    }


    fun copyToClipboard(detail: Detail) = viewModelScope.launch(Dispatchers.IO) {
        clipboardService.copyToClipboard(
            label = detail.name,
            data =detail.content,
            isSensitive = detail.metadata.isObfuscated
        )
    }


    fun dismissTagDialog(selectedTagIds: Set<Uuid>? = null) {
        isTagDialogVisible = false
        viewModelStoreId++
        if (selectedTagIds != null) {
            //Save new selected tags:
            clearSelectedTags()
            selectedTagIds.forEach { tagId ->
                toggleTagSelection(tagId)
            }
        }
    }


    fun dismissTargetDialog(targets: List<Target>? = null) {
        isTargetDialogVisible = false
        viewModelStoreId++
        if (targets != null) {
            this.targets.clear()
            this.targets.addAll(targets)
        }
    }


    fun dismissDetailDialog(detail: Detail? = null) {
        if (detail != null) {
            //Save detail:
            if (isDetailDialogVisible) {
                //Dialog to create new detail:
                isDetailDialogVisible = false
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
        }
        else {
            //Dismiss dialog without saving:
            isDetailDialogVisible = false
            detailToEdit = null
            viewModelStoreId++
        }
    }


    fun deleteSelectedDetails() {
        selectedDetailIds.forEach { id ->
            val detail: Detail? = details.find { it.id == id }
            if (detail != null) {
                details.remove(detail)
            }
        }
        isInMultiselectState = false
        selectedDetailIds.clear()
    }

    fun selectAllDetails() {
        details.forEach { detail ->
            if (!selectedDetailIds.contains(detail.id)) {
                selectedDetailIds.add(detail.id)
            }
        }
    }

    fun isDetailSelected(id: Uuid): Boolean {
        return id in selectedDetailIds
    }


    private fun clearSelectedTags() {
        selectedTagIds.value = emptySet()
    }

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

}
