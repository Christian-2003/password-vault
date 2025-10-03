package de.christian2003.passwordvault.plugin.presentation.view.account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.passwordvault.domain.model.detail.Detail
import de.christian2003.passwordvault.domain.model.account.Account
import de.christian2003.passwordvault.domain.model.tag.Tag
import de.christian2003.passwordvault.application.repository.DetailRepository
import de.christian2003.passwordvault.application.repository.AccountRepository
import de.christian2003.passwordvault.application.repository.TagRepository
import de.christian2003.passwordvault.domain.security.ClipboardService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid


class AccountViewModel(): ViewModel() {

    private lateinit var accountRepository: AccountRepository

    private lateinit var detailRepository: DetailRepository

    private lateinit var clipboardService: ClipboardService

    private var account: Account? = null

    private var isInitialized = false

    lateinit var tagRepository: TagRepository

    lateinit var allTags: Flow<List<Tag>>

    lateinit var accountId: Uuid

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
        accountRepository: AccountRepository,
        detailRepository: DetailRepository,
        tagRepository: TagRepository,
        clipboardService: ClipboardService,
        id: Uuid? = null
    ) {
        if (isInitialized) {
            return
        }
        this.accountRepository = accountRepository
        this.detailRepository = detailRepository
        this.tagRepository = tagRepository
        this.clipboardService = clipboardService
        this.accountId = id ?: Uuid.random()

        allTags = tagRepository.getAllTags()
        isInitialized = true
        viewModelScope.launch(Dispatchers.IO) {
            account = accountRepository.getAccountById(accountId)
            if (account == null) {
                name = ""
                description = ""
                tags.clear()
            }
            else {
                name = account!!.name
                description = account!!.description
                tags.clear()
                tags.addAll(account!!.tags)
            }
            details.clear()
            val detailsForAccount: Flow<List<Detail>> = detailRepository.getAllDetailsForAccount(accountId)
            detailsForAccount.first().forEach { detail ->
                details.add(detail)
            }
        }
    }


    fun save() = viewModelScope.launch(Dispatchers.IO) {
        if (name.isNotEmpty() && description.isNotEmpty()) {
            if (account == null) {
                //Create new account:
                account = Account(
                    id = accountId,
                    name = name,
                    description = description,
                    tags = tags
                )
                accountRepository.createAccount(account!!)
            }
            else {
                //Edit existing account:
                account!!.name = name
                account!!.description = description
                account!!.tags = tags
                accountRepository.updateAccount(account!!)
            }
            //Save details:
            detailRepository.saveAllDetailsForAccount(details, accountId)
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
