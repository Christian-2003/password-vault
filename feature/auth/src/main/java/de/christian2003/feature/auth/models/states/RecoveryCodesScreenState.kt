package de.christian2003.feature.auth.models.states


/**
 * States for the screen through which the user receives new recovery codes.
 *
 * @property FirstTimeSetup             State for the screen displayed when the user opens the app
 *                                      for the very first time and needs to setup a login.
 * @property RecoverPassword            State for the screen displayed when the user enters a new
 *                                      password after successful recovery using recovery codes.
 * @property GenerateNewRecoveryCodes   State for the screen displayed when the user generates new
 *                                      recovery codes.
 */
internal enum class RecoveryCodesScreenState {

    FirstTimeSetup,
    RecoverPassword,
    GenerateNewRecoveryCodes

}
