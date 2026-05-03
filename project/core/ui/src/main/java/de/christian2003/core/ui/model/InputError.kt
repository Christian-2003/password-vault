package de.christian2003.core.ui.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.christian2003.core.ui.R


/**
 * Enum contains all possible input errors.
 *
 * @param errorTextId   ID of the string resource which stores the error message.
 */
enum class InputError(
    @param:StringRes private val errorTextId: Int
) {

    Blank(R.string.error_blankInput),
    InvalidUrl(R.string.error_invalidUrl),
    IllegalCharacters(R.string.error_illegalCharacters),
    IllegalFilename(R.string.error_illegalFilename),
    InvalidPassword(R.string.error_invalidPassword),
    PasswordsNotMatching(R.string.error_passwordsNotMatching);


    /**
     * Returns the error message for the input error.
     *
     * @return  Error message.
     */
    @Composable
    fun message(): String {
        return stringResource(errorTextId)
    }

}
