package de.christian2003.accounts.view.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.accounts.database.AccountsRepository
import de.christian2003.accounts.database.DetailEntity
import de.christian2003.accounts.model.Detail
import de.christian2003.accounts.model.DetailType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class DetailViewModel: ViewModel() {

    private lateinit var repository: AccountsRepository

    private var detail: Detail? = null


    var isCreatingNewDetail: Boolean by mutableStateOf(true)

    var name: String by mutableStateOf("")

    var content: String by mutableStateOf("")

    var type: DetailType by mutableStateOf(DetailType.TEXT)

    var isObfuscated: Boolean by mutableStateOf(false)

    var isVisible: Boolean by mutableStateOf(true)



    fun init(repository: AccountsRepository, detailId: UUID? = null, hmacSeed: ByteArray? = null) {
        this.repository = repository

        viewModelScope.launch(Dispatchers.IO) {
            if (detailId != null && hmacSeed != null) {
                val detailEntity: DetailEntity? = repository.selectDetailById(detailId)
                if (detailEntity != null) {
                    val detail: Detail = detailEntity.toDetail(hmacSeed)
                    this@DetailViewModel.detail = detail
                    isCreatingNewDetail = false
                    name = detail.name
                    content = detail.content
                    type = detail.type
                    isObfuscated = detail.obfuscated
                    isVisible = detail.visible
                }
            }
        }

    }

}
