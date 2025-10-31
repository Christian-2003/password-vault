package de.christian2003.passwordvault.plugin.presentation.view.account

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import de.christian2003.passwordvault.domain.model.detail.Detail
import de.christian2003.passwordvault.domain.model.account.Account
import de.christian2003.passwordvault.application.usecases.account.CreateAccountUseCase
import de.christian2003.passwordvault.application.usecases.account.GetAccountByIdUseCase
import de.christian2003.passwordvault.application.usecases.account.GetAccountIconUseCase
import de.christian2003.passwordvault.application.usecases.account.UpdateAccountUseCase
import de.christian2003.passwordvault.application.usecases.packages.GetAllPackagesUseCase
import de.christian2003.passwordvault.application.usecases.packages.GetLocalizedPackageNameUseCase
import de.christian2003.passwordvault.application.usecases.packages.GetPackageIconUseCase
import de.christian2003.passwordvault.application.usecases.tag.CreateTagUseCase
import de.christian2003.passwordvault.application.usecases.tag.DeleteTagUseCase
import de.christian2003.passwordvault.application.usecases.tag.GetAllTagsUseCase
import de.christian2003.passwordvault.application.usecases.tag.UpdateTagUseCase
import de.christian2003.passwordvault.domain.model.tag.Tag
import de.christian2003.passwordvault.domain.model.target.PackageFingerprintService
import de.christian2003.passwordvault.domain.model.target.Target
import de.christian2003.passwordvault.domain.security.ClipboardService
import de.christian2003.passwordvault.plugin.presentation.view.account.tag.TagViewModel
import de.christian2003.passwordvault.plugin.presentation.view.account.target.TargetViewModel
import de.christian2003.passwordvault.plugin.presentation.view.help.HelpCard
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
import kotlin.collections.map
import kotlin.collections.toSet
import kotlin.uuid.Uuid


class AccountViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var createAccountUseCase: CreateAccountUseCase

    private lateinit var updateAccountUseCase: UpdateAccountUseCase

    private lateinit var getAccountIconUseCase: GetAccountIconUseCase

    private lateinit var getAllTagsUseCase: GetAllTagsUseCase

    private lateinit var createTagUseCase: CreateTagUseCase

    private lateinit var updateTagUseCase: UpdateTagUseCase

    private lateinit var deleteTagUseCase: DeleteTagUseCase

    private lateinit var getAllPackagesUseCase: GetAllPackagesUseCase

    private lateinit var getLocalizedPackageNameUseCase: GetLocalizedPackageNameUseCase

    private lateinit var getPackageIconUseCase: GetPackageIconUseCase

    private lateinit var packageFingerprintService: PackageFingerprintService

    private lateinit var clipboardService: ClipboardService

    private val tagMapper: TagUiMapper = TagUiMapper()

    private var account: Account? = null

    private var isInitialized = false

    lateinit var allTags: Flow<List<TagUiDto>>

    var name: String by mutableStateOf("")

    var description: String by mutableStateOf("")

    val details: MutableList<Detail> = mutableStateListOf()

    val visibleDetails: State<List<Detail>> = derivedStateOf {
        val visibleDetails: MutableList<Detail> = mutableListOf()
        details.forEach { detail ->
            if (detail.metadata.isVisible) {
                visibleDetails.add(detail)
            }
        }
        return@derivedStateOf visibleDetails
    }

    val invisibleDetails: State<List<Detail>> = derivedStateOf {
        val invisibleDetails: MutableList<Detail> = mutableListOf()
        details.forEach { detail ->
            if (!detail.metadata.isVisible) {
                invisibleDetails.add(detail)
            }
        }
        return@derivedStateOf invisibleDetails
    }

    val targets: MutableList<Target> = mutableStateListOf()

    val icon: State<Drawable?> = derivedStateOf {
        getAccountIconUseCase.getAccountIcon(targets)
    }

    val selectedTagIds: MutableStateFlow<Set<Uuid>> = MutableStateFlow(emptySet())

    val selectedDetailIds: MutableList<Uuid> = mutableStateListOf()

    var isInReorderableState: Boolean by mutableStateOf(false)
        private set

    var isInMultiselectState: Boolean by mutableStateOf(false)
        private set

    var isNameDialogVisible: Boolean by mutableStateOf(false)

    var isDescriptionDialogVisible: Boolean by mutableStateOf(false)

    var isTagDialogVisible: Boolean by mutableStateOf(false)

    var isDetailDialogVisible: Boolean by mutableStateOf(false)

    var isTargetDialogVisible: Boolean by mutableStateOf(false)

    var isDeleteDetailMultiselectDialogVisible: Boolean by mutableStateOf(false)

    var areInvisibleDetailsVisible: Boolean by mutableStateOf(false)

    var detailToEdit: Detail? by mutableStateOf(null)

    var detailToDelete: Detail? by mutableStateOf(null)

    lateinit var selectedTags: StateFlow<List<TagUiDto>>

    var helpState: AccountScreenHelpState? by mutableStateOf(if (HelpCard.ACCOUNT.getVisible(application)) {
        AccountScreenHelpState.NAME
    } else {
        null
    })

    var viewModelStoreId: Int = 0


    fun init(
        getAccountByIdUseCase: GetAccountByIdUseCase,
        createAccountUseCase: CreateAccountUseCase,
        updateAccountUseCase: UpdateAccountUseCase,
        getAccountIconUseCase: GetAccountIconUseCase,
        getAllTagsUseCase: GetAllTagsUseCase,
        createTagUseCase: CreateTagUseCase,
        updateTagUseCase: UpdateTagUseCase,
        deleteTagUseCase: DeleteTagUseCase,
        getAllPackagesUseCase: GetAllPackagesUseCase,
        getLocalizedPackageNameUseCase: GetLocalizedPackageNameUseCase,
        getPackageIconUseCase: GetPackageIconUseCase,
        packageFingerprintService: PackageFingerprintService,
        clipboardService: ClipboardService,
        id: Uuid? = null
    ) {
        if (isInitialized) {
            return
        }
        this.createAccountUseCase = createAccountUseCase
        this.updateAccountUseCase = updateAccountUseCase
        this.getAccountIconUseCase = getAccountIconUseCase
        this.getAllTagsUseCase = getAllTagsUseCase
        this.createTagUseCase = createTagUseCase
        this.updateTagUseCase = updateTagUseCase
        this.deleteTagUseCase = deleteTagUseCase
        this.getAllPackagesUseCase = getAllPackagesUseCase
        this.getLocalizedPackageNameUseCase = getLocalizedPackageNameUseCase
        this.getPackageIconUseCase = getPackageIconUseCase
        this.packageFingerprintService = packageFingerprintService
        this.clipboardService = clipboardService
        allTags = getAllTagsUseCase.getAllTags().map { list ->
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


    fun copyToClipboard(detail: Detail) = viewModelScope.launch(Dispatchers.IO) {
        clipboardService.copyToClipboard(
            label = detail.name,
            data = detail.content,
            isSensitive = detail.metadata.isObfuscated
        )
    }


    fun startReorderableState() {
        isInReorderableState = true
        determineNextHelpState()
    }
    fun dismissReorderableState() {
        isInReorderableState = false
        determineNextHelpState()
    }

    fun startMultiselectState(selectedDetailId: Uuid) {
        selectedDetailIds.add(selectedDetailId)
        isInMultiselectState = true
        determineNextHelpState()
    }
    fun dismissMultiselectState() {
        isInMultiselectState = false
        selectedDetailIds.clear()
        determineNextHelpState()
    }


    fun dismissNameDialog(name: String? = null) {
        isNameDialogVisible = false
        if (name != null) {
            this.name = name
            determineNextHelpState()
        }
    }


    fun dismissDescriptionDialog(description: String? = null) {
        isDescriptionDialogVisible = false
        if (description != null) {
            this.description = description
            determineNextHelpState()
        }
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
            determineNextHelpState()
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
            determineNextHelpState()
        }
        else {
            //Dismiss dialog without saving:
            isDetailDialogVisible = false
            detailToEdit = null
            viewModelStoreId++
        }
    }


    fun dismissDeleteDetailDialog(detailToDelete: Detail? = null) = viewModelScope.launch(Dispatchers.Default) {
        this@AccountViewModel.detailToDelete = null
        if (detailToDelete != null) {
            details.remove(detailToDelete)
        }
    }

    fun dismissDeleteDetailsMultiselectDialog(selectedDetailIds: List<Uuid>? = null) = viewModelScope.launch(Dispatchers.Default) {
        isInMultiselectState = false
        isDeleteDetailMultiselectDialogVisible = false
        selectedDetailIds?.forEach { id ->
            val detail: Detail? = details.find { it.id == id }
            if (detail != null) {
                details.remove(detail)
            }
        }
        this@AccountViewModel.selectedDetailIds.clear()
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

    fun initTagViewModel(viewModel: TagViewModel, selectedTags: List<TagUiDto>) {
        viewModel.init(
            getAllTagsUseCase = getAllTagsUseCase,
            createTagUseCase = createTagUseCase,
            updateTagUseCase = updateTagUseCase,
            deleteTagUseCase = deleteTagUseCase,
            selectedTagIds = selectedTags.map { it.id }.toSet()
        )
    }


    fun initTargetViewModel(viewModel: TargetViewModel) {
        viewModel.init(
            getAllPackagesUseCase = getAllPackagesUseCase,
            getLocalizedPackageNameUseCase = getLocalizedPackageNameUseCase,
            getPackageIconUseCase = getPackageIconUseCase,
            packageFingerprintService = packageFingerprintService,
            targets = targets
        )
    }


    fun dismissHelpCard() {
        HelpCard.ACCOUNT.setVisible(application, false)
        helpState = null
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

    private fun determineNextHelpState() {
        if (helpState != null) {
            helpState = if (isInMultiselectState) {
                AccountScreenHelpState.CLOSE_MULTISELECT
            }
            else if (isInReorderableState) {
                AccountScreenHelpState.CLOSE_REORDER
            }
            else if (name.isBlank()) {
                AccountScreenHelpState.NAME
            }
            else if (description.isBlank()) {
                AccountScreenHelpState.DESCRIPTION
            }
            else if (details.isEmpty()) {
                AccountScreenHelpState.DETAILS
            }
            else if (targets.isEmpty()) {
                AccountScreenHelpState.TARGETS
            }
            else {
                AccountScreenHelpState.SAVE
            }
        }
    }

}
