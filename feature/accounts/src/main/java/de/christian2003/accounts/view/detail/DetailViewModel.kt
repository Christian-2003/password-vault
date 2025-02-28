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
import java.time.LocalDateTime
import java.util.UUID


class DetailViewModel: ViewModel() {

    private lateinit var repository: AccountsRepository

    private lateinit var accountId: UUID

    private lateinit var hmacSeed: ByteArray

    private var detail: Detail? = null


    var isCreatingNewDetail: Boolean by mutableStateOf(true)

    var name: String by mutableStateOf("")

    var content: String by mutableStateOf("")

    var type: DetailType by mutableStateOf(DetailType.TEXT)

    var isObfuscated: Boolean by mutableStateOf(false)

    var isVisible: Boolean by mutableStateOf(true)



    fun init(repository: AccountsRepository, accountId: UUID, detailId: UUID? = null) {
        this.repository = repository
        this.accountId = accountId

        if (detailId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val detailEntity: DetailEntity? = repository.selectDetailById(detailId)
                if (detailEntity != null) {
                    val detail: Detail = detailEntity.toDetail()
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


    fun save() = viewModelScope.launch(Dispatchers.IO) {
        if (name.isNotEmpty() && content.isNotEmpty() /*&& type != DetailType.UNDEFINED*/) {
            if (detail == null) {
                //Save new detail:
                val detail = Detail(
                    id = UUID.randomUUID(),
                    account = accountId,
                    name = name,
                    content = content,
                    created = LocalDateTime.now(),
                    changed = LocalDateTime.now(),
                    type = type,
                    obfuscated = isObfuscated,
                    visible = isVisible
                )
                repository.insertDetail(detail.toDetailEntity())
            }
            else {
                //Edit detail:
                val detail = Detail(
                    id = detail!!.id,
                    account = detail!!.account,
                    name = name,
                    content = content,
                    created = detail!!.created,
                    changed = LocalDateTime.now(),
                    type = type,
                    obfuscated = isObfuscated,
                    visible = isVisible
                )
                repository.updateDetail(detail.toDetailEntity())
            }
        }
    }

}
