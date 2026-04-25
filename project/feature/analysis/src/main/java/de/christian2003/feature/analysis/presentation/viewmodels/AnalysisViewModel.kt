package de.christian2003.feature.analysis.presentation.viewmodels

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.data.accounts.application.usecases.GetAccountDescriptorByIdUseCase
import de.christian2003.data.accounts.application.usecases.GetAccountIconUseCase
import de.christian2003.data.accounts.domain.entities.AccountDescriptor
import de.christian2003.feature.analysis.application.usecases.AnalyzePasswordsUseCase
import de.christian2003.feature.analysis.domain.entities.SecurityResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.uuid.Uuid


@HiltViewModel
internal class AnalysisViewModel @Inject constructor(
    application: Application,
    private val analyzePasswordsUseCase: AnalyzePasswordsUseCase,
    private val getAccountDescriptorByIdUseCase: GetAccountDescriptorByIdUseCase,
    private val getAccountIconUseCase: GetAccountIconUseCase
): AndroidViewModel(application) {

    private val accountDescriptorsCache: MutableMap<Uuid, AccountDescriptor> = mutableMapOf()

    var securityResult: SecurityResult? by mutableStateOf(null)
        private set


    init {
        startAnalysis()
    }


    suspend fun queryAccountDescriptor(accountId: Uuid): AccountDescriptor? {
        if (accountDescriptorsCache.contains(accountId)) {
            return accountDescriptorsCache[accountId]
        }

        val accountDescriptor: AccountDescriptor? = getAccountDescriptorByIdUseCase.getAccountDescriptorById(accountId)
        if (accountDescriptor != null) {
            accountDescriptorsCache.put(accountId, accountDescriptor)
        }
        return accountDescriptor
    }


    fun queryAccountIcon(accountDescriptor: AccountDescriptor): Drawable? {
        return getAccountIconUseCase.getAccountIcon(accountDescriptor)
    }


    fun startAnalysis() {
        viewModelScope.launch(Dispatchers.Main) {
            securityResult = null
            securityResult = analyzePasswordsUseCase.analyzePasswords()
        }
    }

}
