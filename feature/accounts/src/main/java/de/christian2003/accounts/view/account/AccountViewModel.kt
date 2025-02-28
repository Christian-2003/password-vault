package de.christian2003.accounts.view.account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.accounts.database.AccountEntity
import de.christian2003.accounts.database.AccountsRepository
import de.christian2003.accounts.database.DetailEntity
import de.christian2003.accounts.model.Detail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class AccountViewModel: ViewModel() {

    private lateinit var repository: AccountsRepository

    private var account: AccountEntity? = null

    lateinit var accountId: UUID


    private val _details = MutableStateFlow<List<Detail>>(emptyList())

    val details: StateFlow<List<Detail>> = _details.asStateFlow()


    var isCreatingNewAccount: Boolean by mutableStateOf(true)

    var name: String by mutableStateOf("")

    var description: String by mutableStateOf("")

    var isEditNameSheetVisible: Boolean by mutableStateOf(false)

    var isEditDescriptionSheetVisible: Boolean by mutableStateOf(false)

    var isDeleteSheetVisible: Boolean by mutableStateOf(false)


    @OptIn(ExperimentalCoroutinesApi::class)
    fun init(repository: AccountsRepository, accountId: UUID?) {
        this.repository = repository
        if (accountId != null) {
            this.accountId = accountId
            viewModelScope.launch(Dispatchers.IO) {
                account = repository.selectAccountById(accountId)
                if (account != null) {
                    name = account!!.name
                    description = account!!.description
                    isCreatingNewAccount = false
                }
            }
            repository.selectDetailsForAccount(accountId)
                ?.mapLatest { items -> items.parallelMap {
                    withContext(Dispatchers.IO) {
                        it.toDetail() //TODO: Samsung KeyStore is not thread safe. This can cause fatal exception!
                    }
                } }
                ?.onEach { detailsList -> _details.value = detailsList }
                ?.launchIn(viewModelScope)
        }
        else {
            this.accountId = UUID.randomUUID()
        }
    }


    fun save() = viewModelScope.launch(Dispatchers.IO) {
        if (name.isNotEmpty()) {
            if (account == null) {
                //Create new account:
                val account = AccountEntity(
                    id = accountId,
                    name = name,
                    description = description
                )
                repository.insertAccount(account)
            }
            else {
                //Edit account:
                val account = AccountEntity(
                    id = account!!.id,
                    name = name,
                    description = description,
                    created = account!!.created,
                    changed = LocalDateTime.now()
                )
                repository.updateAccount(account)
            }
        }
    }

    fun delete() = viewModelScope.launch(Dispatchers.IO) {
        if (account != null) {
            repository.deleteAccount(account!!)
        }
    }

}


suspend fun <A, B> List<A>.parallelMap(transform: suspend (A) -> B): List<B> = coroutineScope {
    map { async { transform(it) } }.awaitAll()
}
