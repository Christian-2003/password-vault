package de.christian2003.feature.accounts.viewmodels

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.core.ui.model.HelpCard
import de.christian2003.data.accounts.domain.entities.Detail
import de.christian2003.data.accounts.domain.entities.DetailIcon
import de.christian2003.data.accounts.domain.entities.DetailMetadata
import de.christian2003.data.accounts.domain.entities.DetailType
import javax.inject.Inject


/**
 * View model for the sheet through which to edit (or create) a detail.
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    application: Application
): AndroidViewModel(application) {

    /**
     * Indicates whether the view model has been initialized.
     */
    private var isInitialized = false

    /**
     * Detail tho edit. This is null when the sheet is used to create a new detail.
     */
    private var detail: Detail? = null

    /**
     * Name of the detail.
     */
    var name: String by mutableStateOf("")

    /**
     * Content of the detail.
     */
    var content: String by mutableStateOf("")

    /**
     * Whether the detail should be obfuscated or not.
     */
    var isObfuscated: Boolean by mutableStateOf(false)

    /**
     * Whether the detail should be visible or not.
     */
    var isVisible: Boolean by mutableStateOf(true)

    /**
     * Type that can be selected by the user.
     */
    var type: DetailType by mutableStateOf(DetailType.Text)

    /**
     * Icon that can be selected by the user.
     */
    var icon: DetailIcon? by mutableStateOf(DetailIcon.Text)

    /**
     * Indicates whether the dialog is used to create a new detail.
     */
    var isCreatingNewDetail: Boolean by mutableStateOf(false)

    /**
     * Indicates whether the data entered is valid and can be used to create a detail.
     */
    var isDataValid: State<Boolean> = derivedStateOf {
        name.isNotBlank() && content.isNotBlank()
    }

    /**
     * Indicates whether the dialog to discard without saving is visible.
     */
    var isDiscardDialogVisible: Boolean by mutableStateOf(false)

    /**
     * Indicates whether the help card is visible.
     */
    var isHelpCardVisible: Boolean by mutableStateOf(HelpCard.Detail.getVisible(application))


    /**
     * Initializes the view model.
     *
     * @param detail    Detail to edit. Pass null to create a new detail.
     */
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
            type = DetailType.Text
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


    /**
     * Determines whether changes were made to the detail.
     *
     * @return  Whether changes were made.
     */
    fun areChangesMade(): Boolean {
        val detail: Detail? = this.detail
        if (detail == null) {
            //Detail created:
            return name != ""
                    || content != ""
                    || isObfuscated != false
                    || isVisible != true
                    || (icon != null && icon != DetailIcon.Text)
        }
        else {
            //Detail edited:
            return detail.name != name
                    || detail.content != content
                    || detail.metadata.isObfuscated != isObfuscated
                    || detail.metadata.isVisible != isVisible
                    || detail.icon != icon
        }
    }


    /**
     * Creates the detail with the data entered by the user. If the data entered is invalid, this
     * returns null.
     *
     * @return  Detail with data entered or null.
     */
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


    /**
     * Dismisses the help card.
     */
    fun dismissHelpCard() {
        HelpCard.Detail.setVisible(application, false)
        isHelpCardVisible = false
    }

}
