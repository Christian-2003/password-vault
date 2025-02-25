package de.christian2003.accounts.view.account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.accounts.database.AccountEntity
import de.christian2003.accounts.database.AccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID


class AccountViewModel: ViewModel() {

    private lateinit var repository: AccountRepository

    private var account: AccountEntity? = null

    var name: String by mutableStateOf("")

    var description: String by mutableStateOf("")

    var isEditNameSheetVisible: Boolean by mutableStateOf(false)

    var isEditDescriptionSheetVisible: Boolean by mutableStateOf(false)


    fun init(repository: AccountRepository, id: UUID?) {
        this.repository = repository
        if (id != null) {
            viewModelScope.launch {
                account = repository.selectAccountById(id)
                if (account != null) {
                    name = account!!.name
                    description = account!!.description
                }
            }
        }
    }


    fun save() = viewModelScope.launch(Dispatchers.IO) {

        if (account == null) {
            //Create new account:
            val account = AccountEntity(
                id = UUID.randomUUID(),
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
