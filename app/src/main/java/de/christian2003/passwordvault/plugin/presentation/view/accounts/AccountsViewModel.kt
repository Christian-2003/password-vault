package de.christian2003.passwordvault.plugin.presentation.view.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.passwordvault.domain.model.account.Account
import de.christian2003.passwordvault.application.repository.AccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AccountsViewModel(): ViewModel() {

    private lateinit var accountRepository: AccountRepository


    lateinit var allAccounts: Flow<List<Account>>

    fun init(accountRepository: AccountRepository) {
        this.accountRepository = accountRepository
        this.allAccounts = accountRepository.getAllAccounts()
    }


    fun deleteAccount(account: Account) = viewModelScope.launch(Dispatchers.IO) {
        accountRepository.deleteAccount(account)
    }

}
