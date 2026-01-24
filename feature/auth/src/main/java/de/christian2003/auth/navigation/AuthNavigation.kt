package de.christian2003.auth.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import de.christian2003.auth.models.password.PasswordScreenState
import de.christian2003.auth.models.recoverycodes.RecoveryCodesScreenState
import de.christian2003.auth.ui.biometrics.BiometricsScreen
import de.christian2003.auth.ui.login.LoginScreen
import de.christian2003.auth.ui.password.PasswordScreen
import de.christian2003.auth.ui.recovery.RecoveryScreen
import de.christian2003.auth.ui.recoverycodes.RecoveryCodesScreen
import de.christian2003.auth.ui.settings.AuthSettingsScreen
import de.christian2003.auth.viewmodels.AuthSettingsViewModel
import de.christian2003.auth.viewmodels.BiometricsViewModel
import de.christian2003.auth.viewmodels.LoginViewModel
import de.christian2003.auth.viewmodels.PasswordViewModel
import de.christian2003.auth.viewmodels.RecoveryCodesViewModel
import de.christian2003.auth.viewmodels.RecoveryViewModel
import kotlinx.serialization.Serializable


@Serializable
object SetupFlow

@Serializable
object RecoveryFlow

@Serializable
object AuthSettingsFlow

@Serializable
object Login

@Serializable
data class MasterPassword(
    val state: PasswordScreenState
)

@Serializable
private object Biometrics

@Serializable
data class RecoveryCodes(
    val state: RecoveryCodesScreenState
)

@Serializable
private object Recovery

@Serializable
private object AuthSettings


/**
 * Flow for the first-time master password setup.
 *
 * @param navController             Navigation controller.
 * @param onNotifyAuthSetupFinished Callback invoked to inform the nav host that the setup flow finished.
 * @param onSetupBiometricAuth      Callback invoked to setup biometric authentication.
 */
fun NavGraphBuilder.setupFlowDestination(
    navController: NavController,
    onNotifyAuthSetupFinished: () -> Unit,
    onSetupBiometricAuth: suspend () -> Boolean
) {
    navigation<SetupFlow>(
        startDestination = MasterPassword(PasswordScreenState.FirstTimeSetup)
    ) {

        //Master password setup:
        masterPasswordDestination(
            onNavigateUp = {
                navController.navigateUp()
            },
            onContinue = {
                navController.navigate(RecoveryCodes(RecoveryCodesScreenState.FirstTimeSetup))
            }
        )

        //Recovery codes setup:
        recoveryCodesDestination(
            onNavigateUp = {
                navController.navigateUp()
            },
            onContinue = {
                navController.navigate(Biometrics)
            }
        )

        //Biometrics setup:
        biometricsDestination(
            onNavigateUp = {
                navController.navigateUp()
            },
            onContinue = {
                onNotifyAuthSetupFinished()
                navController.popBackStack(SetupFlow, true)
            },
            onSetupBiometricAuth = onSetupBiometricAuth
        )

    }
}


/**
 * Flow for the recovery of the master password.
 *
 * @param navController Navigation controller.
 */
fun NavGraphBuilder.recoveryFlowDestination(
    navController: NavController
) {
    navigation<RecoveryFlow>(
        startDestination = Recovery
    ) {

        //Recovery using recovery codes:
        recoveryDestination(
            onNavigateUp = {
                navController.navigateUp()
            },
            onContinue = {
                navController.navigate(MasterPassword(state = PasswordScreenState.RecoverPassword))
            }
        )

        //Master password setup:
        masterPasswordDestination(
            onNavigateUp = {
                navController.navigateUp()
            },
            onContinue = {
                navController.navigate(RecoveryCodes(RecoveryCodesScreenState.RecoverPassword))
            }
        )

        //Recovery codes setup:
        recoveryCodesDestination(
            onNavigateUp = {
                navController.navigateUp()
            },
            onContinue = {
                navController.popBackStack(RecoveryFlow, true)
            }
        )

    }
}


/**
 * Flow for the authentication settings.
 *
 * @param navController Navigation controller.
 */
fun NavGraphBuilder.authSettingsFlowDestination(
    navController: NavController
) {
    navigation<AuthSettingsFlow>(
        startDestination = AuthSettings
    ) {

        //Base page for the auth settings:
        authSettingsDestination(
            onNavigateUp = {
                navController.navigateUp()
            },
            onNavigateToPassword = {
                navController.navigate(MasterPassword(PasswordScreenState.ChangePassword))
            },
            onNavigateToBiometrics = {
                navController.navigate(Biometrics)
            },
            onNavigateToRecoveryCodes = {
                navController.navigate(RecoveryCodes(RecoveryCodesScreenState.RecoverPassword))
            }
        )

        //Change master password:
        masterPasswordDestination(
            onNavigateUp = {
                navController.navigateUp()
            },
            onContinue = {
                navController.navigate(RecoveryCodes(RecoveryCodesScreenState.RecoverPassword))
            }
        )

        //Enable / disable biometrics:
        biometricsDestination(
            onNavigateUp = {
                navController.navigateUp()
            },
            onContinue = {
                //TODO
            },
            onSetupBiometricAuth = {
                //TODO
                false
            }
        )

        //Generate new recovery codes:
        recoveryCodesDestination(
            onNavigateUp = {
                navController.navigateUp()
            },
            onContinue = {
                navController.navigateUp()
            }
        )

    }
}


/**
 * Navigation destination for the screen through which to login to the app.
 *
 * @param onContinue            Callback invoked on successful login.
 * @param onNavigateToRecovery  Callback invoked to navigate to the recovery of the master password.
 * @param onBiometricUnlock     Callback invoked to perform a biometric unlock of the master key.
 */
fun NavGraphBuilder.loginDestination(
    onContinue: () -> Unit,
    onNavigateToRecovery: () -> Unit,
    onBiometricUnlock: suspend () -> Boolean
) {
    composable<Login> {
        val viewModel: LoginViewModel = hiltViewModel()

        LoginScreen(
            viewModel = viewModel,
            onContinue = onContinue,
            onNavigateToRecovery = onNavigateToRecovery,
            onBiometricUnlock = onBiometricUnlock
        )
    }
}


/**
 * Navigation destination for the screen to set a new paster password.
 * 
 * @param onNavigateUp  Callback invoked to navigate up the navigation stack.
 * @param onContinue    Callback invoked to navigate to the next setup step.
 */
private fun NavGraphBuilder.masterPasswordDestination(
    onNavigateUp: () -> Unit,
    onContinue: () -> Unit
) {
    composable<MasterPassword> {
        val viewModel: PasswordViewModel = hiltViewModel()

        PasswordScreen(
            viewModel = viewModel,
            onNavigateUp = onNavigateUp,
            onContinue = onContinue
        )
    }
}


/**
 * Navigation destination for the screen for the setup of the recovery codes.
 *
 * @param onNavigateUp  Callback invoked to navigate up the navigation stack.
 * @param onContinue    Callback invoked to navigate to the next setup step.
 */
private fun NavGraphBuilder.recoveryCodesDestination(
    onNavigateUp: () -> Unit,
    onContinue: () -> Unit
) {
    composable<RecoveryCodes> {
        val viewModel: RecoveryCodesViewModel = hiltViewModel()

        RecoveryCodesScreen(
            viewModel = viewModel,
            onNavigateUp = onNavigateUp,
            onContinue = onContinue
        )
    }
}


/**
 * Navigation destination for the screen for the setup of the biometric authentication.
 *
 * @param onNavigateUp          Callback invoked to navigate up the navigation stack.
 * @param onContinue            Callback invoked to navigate to the next setup step.
 * @param onSetupBiometricAuth  Callback invoked to setup biometric authentication.
 */
private fun NavGraphBuilder.biometricsDestination(
    onNavigateUp: () -> Unit,
    onContinue: () -> Unit,
    onSetupBiometricAuth: suspend () -> Boolean
) {
    composable<Biometrics> {
        val viewModel: BiometricsViewModel = hiltViewModel()

        BiometricsScreen(
            viewModel = viewModel,
            onNavigateUp = onNavigateUp,
            onContinue = onContinue,
            onSetupBiometricAuth = onSetupBiometricAuth
        )
    }
}


/**
 * Navigation destination for the recovery screen through which to enter a recovery code if the
 * user forgets their master password.
 *
 * @param onNavigateUp  Callback invoked to navigate up the navigation stack.
 * @param onContinue    Callback invoked to navigate to the next recovery step.
 */
private fun NavGraphBuilder.recoveryDestination(
    onNavigateUp: () -> Unit,
    onContinue: () -> Unit
) {
    composable<Recovery> {
        val viewModel: RecoveryViewModel = hiltViewModel()

        RecoveryScreen(
            viewModel = viewModel,
            onNavigateUp = onNavigateUp,
            onContinue = onContinue
        )
    }
}


/**
 * Navigation destination for the screen which displays authentication settings.
 *
 * @param onNavigateUp              Callback invoked to navigate up the navigation stack.
 * @param onNavigateToPassword      Callback invoked to change the master password.
 * @param onNavigateToBiometrics    Callback invoked to navigate to enable / disable biometrics.
 * @param onNavigateToRecoveryCodes Callback invoked to generate new recovery codes.
 */
private fun NavGraphBuilder.authSettingsDestination(
    onNavigateUp: () -> Unit,
    onNavigateToPassword: () -> Unit,
    onNavigateToBiometrics: () -> Unit,
    onNavigateToRecoveryCodes: () -> Unit
) {
    composable<AuthSettings> {
        val viewModel: AuthSettingsViewModel = hiltViewModel()

        AuthSettingsScreen(
            viewModel = viewModel,
            onNavigateUp = onNavigateUp,
            onNavigateToPassword = onNavigateToPassword,
            onNavigateToBiometrics = onNavigateToBiometrics,
            onNavigateToRecoveryCodes = onNavigateToRecoveryCodes
        )
    }
}
