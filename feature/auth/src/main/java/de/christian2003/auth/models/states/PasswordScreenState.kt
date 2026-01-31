package de.christian2003.auth.models.states


/**
 * States for the screen through which to enter the password.
 *
 * @property FirstTimeSetup     State for the screen displayed when the user opens the app for the
 *                              very first time and needs to setup a login.
 * @property ChangePassword     State for the screen displayed when the user changes the password
 *                              through the app settings.
 * @property RecoverPassword    State for the screen displayed when the user enters a new password
 *                              after successful recovery using recovery codes.
 */
enum class PasswordScreenState {

    FirstTimeSetup,
    ChangePassword,
    RecoverPassword

}
