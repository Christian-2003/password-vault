package de.christian2003.passwordvault.plugin.presentation.view.main

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.passwordvault.application.usecases.account.DeleteAccountUseCase
import de.christian2003.passwordvault.application.usecases.account.GetAccountIconUseCase
import de.christian2003.passwordvault.application.usecases.account.GetAllAccountDescriptorsUseCase
import de.christian2003.passwordvault.domain.model.account.AccountDescriptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class MainViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var deleteAccountUseCase: DeleteAccountUseCase

    private lateinit var getAccountIconUseCase: GetAccountIconUseCase

    private var isInitialized: Boolean = false

    lateinit var accountDescriptors: Flow<List<AccountDescriptor>>

    var accountToDelete: AccountDescriptor? by mutableStateOf(null)


    fun init(
        getAllAccountDescriptorsUseCase: GetAllAccountDescriptorsUseCase,
        deleteAccountUseCase: DeleteAccountUseCase,
        getAccountIconUseCase: GetAccountIconUseCase
    ) {
        if (isInitialized) {
            return
        }

        this.deleteAccountUseCase = deleteAccountUseCase
        this.getAccountIconUseCase = getAccountIconUseCase
        this.accountDescriptors = getAllAccountDescriptorsUseCase.getAllAccountDescriptors()
        isInitialized = true
    }


    fun deleteAccount() = viewModelScope.launch(Dispatchers.IO) {
        val accountToDelete: AccountDescriptor? = this@MainViewModel.accountToDelete
        this@MainViewModel.accountToDelete = null
        if (accountToDelete != null) {
            deleteAccountUseCase.deleteAccount(accountToDelete.id)
        }
    }


    fun queryIconForAccount(accountDescriptor: AccountDescriptor): Drawable? {
        return getAccountIconUseCase.getAccountIcon(accountDescriptor)
    }

}
