package de.christian2003.passwordvault.plugin.presentation.view.account.detail

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import de.christian2003.passwordvault.domain.model.detail.Detail
import de.christian2003.passwordvault.domain.model.detail.DetailIcon
import de.christian2003.passwordvault.domain.model.detail.DetailMetadata
import de.christian2003.passwordvault.domain.model.detail.DetailType
import kotlin.uuid.Uuid


class DetailViewModel: ViewModel() {

    private var isInitialized = false

    private var detail: Detail? = null

    var name: String by mutableStateOf("")

    var content: String by mutableStateOf("")

    var isObfuscated: Boolean by mutableStateOf(false)

    var isVisible: Boolean by mutableStateOf(true)

    var type: DetailType by mutableStateOf(DetailType.TEXT)

    var icon: DetailIcon? by mutableStateOf(DetailIcon.TEXT)

    var isCreatingNewDetail: Boolean by mutableStateOf(false)

    fun init(detail: Detail?) {
        if (isInitialized) {
            return
        }
        this.detail = detail
        isInitialized = true
        if (detail == null) {
            //Create new detail:
            name = ""
            content = ""
            isObfuscated = false
            isVisible = true
            type = DetailType.TEXT
            icon = null
            isCreatingNewDetail = true
        }
        else {
            //Edit detail:
            name = detail.name
            content = detail.content
            isObfuscated = detail.metadata.isObfuscated
            isVisible = detail.metadata.isVisible
            type = detail.type
            icon = detail.icon
            isCreatingNewDetail = false
        }
    }


    var isDataValid: State<Boolean> = derivedStateOf {
        name.isNotBlank() && content.isNotBlank()
    }

    fun createDetailToSave(): Detail? {
        if (isDataValid.value) {
            val detail: Detail? = detail
            if (detail == null) {
                //Create new detail:
                val newDetail = Detail(
                    name = name,
                    content = content,
                    type = type,
                    icon = icon,
                    metadata = DetailMetadata(
                        isObfuscated = isObfuscated,
                        isVisible = isVisible
                    )
                )
                return newDetail
            }
            else {
                //Edit detail:
                val editedDetail = Detail(
                    id = detail.id,
                    name = name,
                    content = content,
                    type = type,
                    icon = icon,
                    metadata = detail.metadata.copy(
                        isObfuscated = isObfuscated,
                        isVisible = isVisible
                    )
                )
                return editedDetail
            }
        }
        return null
    }

}
