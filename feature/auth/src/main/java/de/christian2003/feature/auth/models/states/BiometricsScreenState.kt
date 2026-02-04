package de.christian2003.feature.auth.models.states


/**
 * Enum stores the states for the screen through which to enable biometrics.
 *
 * @property FirstTimeSetup     Screen is shown for the first time app setup.
 * @property EnableBiometrics   Screen is shown for the user to enable biometric auth.
 */
internal enum class BiometricsScreenState {

    FirstTimeSetup,
    EnableBiometrics

}
