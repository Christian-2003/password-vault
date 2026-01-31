package de.christian2003.auth.models.states


/**
 * Enum stores the states for the screen through which to finish changes to auth.
 *
 * @property FirstTimeSetup             Screen is shown for the first-time app setup.
 * @property RecoverPassword            Screen is shown for the recovery of the master password.
 * @property ChangePassword             Screen is shown to change the master password.
 * @property GenerateNewRecoveryCodes   Screen is shown to generate new recovery codes.
 */
enum class FinishScreenState {

    FirstTimeSetup,
    RecoverPassword,
    ChangePassword,
    GenerateNewRecoveryCodes

}
