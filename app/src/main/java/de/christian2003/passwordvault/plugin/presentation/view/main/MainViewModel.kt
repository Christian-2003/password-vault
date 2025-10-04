package de.christian2003.passwordvault.plugin.presentation.view.main

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.passwordvault.application.usecases.acount.DeleteAccountUseCase
import de.christian2003.passwordvault.application.usecases.acount.GetAllAccountDescriptorsUseCase
import de.christian2003.passwordvault.domain.model.account.AccountDescriptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class MainViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var deleteAccountUseCase: DeleteAccountUseCase

    lateinit var accountDescriptors: Flow<List<AccountDescriptor>>

    var accountToDelete: AccountDescriptor? by mutableStateOf(null)


    fun init(
        getAllAccountDescriptorsUseCase: GetAllAccountDescriptorsUseCase,
        deleteAccountUseCase: DeleteAccountUseCase
    ) {
        this.deleteAccountUseCase = deleteAccountUseCase
        this.accountDescriptors = getAllAccountDescriptorsUseCase.getAllAccountDescriptors()
    }


    fun deleteAccount() = viewModelScope.launch(Dispatchers.IO) {
        val accountToDelete: AccountDescriptor? = this@MainViewModel.accountToDelete
        this@MainViewModel.accountToDelete = null
        if (accountToDelete != null) {
            deleteAccountUseCase.deleteAccount(accountToDelete.id)
        }
    }

}
