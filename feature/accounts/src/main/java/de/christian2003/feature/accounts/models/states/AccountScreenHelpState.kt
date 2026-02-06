package de.christian2003.feature.accounts.models.states


/**
 * Enum contains fields that indicate which help message should be shown to the user within the
 * screen through which to edit / create accounts.
 */
enum class AccountScreenHelpState {
    Name,
    Description,
    Details,
    Targets,
    Save,
    CloseMultiselect,
    CloseReorder
}
