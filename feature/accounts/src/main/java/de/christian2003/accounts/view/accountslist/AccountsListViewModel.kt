package de.christian2003.accounts.view.accountslist

import androidx.lifecycle.ViewModel
import de.christian2003.accounts.database.AccountEntity
import de.christian2003.accounts.database.AccountRepository
import kotlinx.coroutines.flow.Flow


class AccountsListViewModel: ViewModel() {

    private lateinit var repository: AccountRepository

    lateinit var accounts: Flow<List<AccountEntity>>

    fun init(repository: AccountRepository) {
        this.repository = repository
        accounts = repository.allAccounts
    }

}
