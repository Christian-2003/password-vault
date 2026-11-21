package de.christian2003.feature.auth.models.states

import androidx.annotation.Keep


/**
 * Enum stores the states for the screen through which to enable biometrics.
 *
 * @property FirstTimeSetup     Screen is shown for the first time app setup.
 * @property EnableBiometrics   Screen is shown for the user to enable biometric auth.
 */
@Keep //Prevent R8 from removing enum: Enum is used in Jetpack Compose typesafe navigation
internal enum class BiometricsScreenState {

    FirstTimeSetup,
    EnableBiometrics

}
