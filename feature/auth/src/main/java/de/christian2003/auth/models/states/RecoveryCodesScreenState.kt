package de.christian2003.auth.models.states


/**
 * States for the screen through which the user receives new recovery codes.
 *
 * @property FirstTimeSetup     State for the screen displayed when the user opens the app for the
 *                              very first time and needs to setup a login.
 * @property RecoverPassword    State for the screen displayed when the user enters a new password
 *                              after successful recovery using recovery codes.
 */
enum class RecoveryCodesScreenState {

    FirstTimeSetup,
    RecoverPassword

}
