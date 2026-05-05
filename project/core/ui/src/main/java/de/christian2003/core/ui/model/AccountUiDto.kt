package de.christian2003.core.ui.model

import android.graphics.drawable.Drawable
import kotlin.uuid.Uuid


/**
 * DTO for accounts in the user interface.
 *
 * @param id            ID of the account.
 * @param name          Name of the account.
 * @param description   Description of the account.
 * @param icon          Optional account icon.
 */
data class AccountUiDto(
    val id: Uuid,
    val name: String,
    val description: String,
    val icon: Drawable?
)
