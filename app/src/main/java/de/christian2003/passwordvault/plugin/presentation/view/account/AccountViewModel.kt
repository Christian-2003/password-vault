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
import de.christian2003.passwordvault.domain.model.tag.Tag
import de.christian2003.passwordvault.application.repository.TagRepository
import de.christian2003.passwordvault.application.usecases.acount.CreateAccountUseCase
import de.christian2003.passwordvault.application.usecases.acount.GetAccountByIdUseCase
import de.christian2003.passwordvault.application.usecases.acount.UpdateAccountUseCase
import de.christian2003.passwordvault.domain.security.ClipboardService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid


class AccountViewModel(): ViewModel() {

    private lateinit var createAccountUseCase: CreateAccountUseCase

    private lateinit var updateAccountUseCase: UpdateAccountUseCase

    private lateinit var clipboardService: ClipboardService

    private var account: Account? = null

    private var isInitialized = false

    lateinit var tagRepository: TagRepository

    lateinit var allTags: Flow<List<Tag>>

    var name: String by mutableStateOf("")

    var description: String by mutableStateOf("")

    var isNameDialogVisible: Boolean by mutableStateOf(false)

    var isDescriptionDialogVisible: Boolean by mutableStateOf(false)

    var isTagDialogVisible: Boolean by mutableStateOf(false)

    var isDetailDialogVisible: Boolean by mutableStateOf(false)

    var detailToEdit: Detail? by mutableStateOf(null)

    var detailToDelete: Detail? by mutableStateOf(null)

    val tags: MutableList<Tag> = mutableStateListOf()

    val details: MutableList<Detail> = mutableStateListOf()

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
        allTags = tagRepository.getAllTags()
        isInitialized = true

        if (id != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val account: Account? = getAccountByIdUseCase.getAccountById(id)
                this@AccountViewModel.account = account
                if (account == null) {
                    name = ""
                    description = ""
                    details.clear()
                    tags.clear()
                }
                else {
                    name = account.descriptor.name
                    description = account.descriptor.description
                    tags.clear()
                    tags.addAll(account.tags)
                    details.clear()
                    details.addAll(account.details)
                }
            }
        }
    }


    fun save() = viewModelScope.launch(Dispatchers.IO) {
        if (name.isNotBlank() && (description.isEmpty() || description.isNotBlank())) {
            val account: Account? = this@AccountViewModel.account
            if (account == null) {
                //Create new account:
                Log.d("Account", "Create account")
                createAccountUseCase.createAccount(
                    name = name,
                    description = description,
                    details = details,
                    tags = tags,
                    targets = listOf()
                )
            }
            else {
                //Edit existing account:
                Log.d("Account", "Edit account")
                updateAccountUseCase.updateAccount(
                    id = account.descriptor.id,
                    name = name,
                    description = description,
                    details = details,
                    tags = tags,
                    targets = listOf()
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


    fun dismissTagDialog(selectedTags: List<Tag>? = null) {
        isTagDialogVisible = false
        viewModelStoreId++
        if (selectedTags != null) {
            //Save new selected tags:
            tags.clear()
            tags.addAll(selectedTags)
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

}
