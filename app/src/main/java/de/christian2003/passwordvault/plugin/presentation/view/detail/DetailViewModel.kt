package de.christian2003.passwordvault.plugin.presentation.view.detail

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.passwordvault.domain.entry.Detail
import de.christian2003.passwordvault.domain.entry.DetailIcon
import de.christian2003.passwordvault.domain.entry.DetailType
import de.christian2003.passwordvault.domain.repository.DetailRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid


class DetailViewModel(): ViewModel() {

    private lateinit var detailRepository: DetailRepository

    private var isInitialized = false

    private var entryId: Uuid? = null

    private var detail: Detail? = null

    var name: String by mutableStateOf("")

    var content: String by mutableStateOf("")

    var type: DetailType by mutableStateOf(DetailType.TEXT)

    var icon: DetailIcon? by mutableStateOf(null)

    var isObfuscated: Boolean by mutableStateOf(false)

    var isVisible: Boolean by mutableStateOf(true)

    var isCreatingNewDetail: Boolean by mutableStateOf(false)
        private set

    var isDataValid: State<Boolean> = derivedStateOf {
        name.isNotBlank() && content.isNotBlank()
    }


    fun init(detailRepository: DetailRepository, detailId: Uuid? = null, entryId: Uuid? = null) {
        if (isInitialized) {
            return
        }
        this.detailRepository = detailRepository
        this.entryId = entryId
        isInitialized = true
        viewModelScope.launch {
            val detail: Detail? = if (detailId != null) {
                detailRepository.getDetailById(detailId)
            } else {
                null
            }
            this@DetailViewModel.detail = detail
            if (detail == null) {
                isCreatingNewDetail = true
                name = ""
                content = ""
                type = DetailType.TEXT
                icon = null
                isObfuscated = false
                isVisible = true
            }
            else {
                isCreatingNewDetail = false
                name = detail.name
                content = detail.content
                type = detail.type
                icon = detail.icon
                isObfuscated = detail.isObfuscated
                isVisible = detail.isVisible
            }
        }
    }


    fun save() = viewModelScope.launch(Dispatchers.IO) {
        if (name.isNotEmpty() && content.isNotEmpty()) {
            var detail: Detail? = this@DetailViewModel.detail
            if (detail == null) {
                //Create new detail:
                val entry: Uuid? = this@DetailViewModel.entryId
                if (entry != null) {
                    detail = Detail(
                        entry = entry,
                        name = name,
                        content = content,
                        type = type,
                        icon = icon,
                        isObfuscated = isObfuscated,
                        isVisible = isVisible
                    )
                    detailRepository.createDetail(detail)
                }
            }
            else {
                //Edit existing detail:
                detail.name = name
                detail.content = content
                detail.type = type
                detail.icon = icon
                detail.isObfuscated = isObfuscated
                detail.isVisible = isVisible
                detailRepository.updateDetail(detail)
            }
        }
    }

}
