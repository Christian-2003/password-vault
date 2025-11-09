package de.christian2003.passwordvault.plugin.presentation.view.password


/**
 * Flow in which the password screen is launched.
 *
 * @property Setup      Setup flow for the first-time setup of the master password
 * @property Recovery   Recovery flow to recover the master password using alternative identification,
 *                      such as security questions.
 * @property None       No flow. The screen is shown to change the master password using the
 *                      current master password.
 */
enum class PasswordScreenFlow {
    Setup,
    Recovery,
    None
}
