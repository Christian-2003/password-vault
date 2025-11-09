package de.christian2003.passwordvault.plugin.presentation.view.main

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.passwordvault.application.usecases.account.DeleteAccountUseCase
import de.christian2003.passwordvault.application.usecases.account.GetAccountIconUseCase
import de.christian2003.passwordvault.application.usecases.account.GetAllAccountDescriptorsUseCase
import de.christian2003.passwordvault.domain.model.account.AccountDescriptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    getAllAccountDescriptorsUseCase: GetAllAccountDescriptorsUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val getAccountIconUseCase: GetAccountIconUseCase
): AndroidViewModel(application) {

    val accountDescriptors: Flow<List<AccountDescriptor>> = getAllAccountDescriptorsUseCase.getAllAccountDescriptors()

    var accountToDelete: AccountDescriptor? by mutableStateOf(null)


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
