package de.christian2003.auth.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import de.christian2003.auth.models.states.FinishScreenState
import de.christian2003.auth.models.states.PasswordScreenState
import de.christian2003.auth.models.states.RecoveryCodesScreenState
import de.christian2003.auth.ui.biometrics.BiometricsScreen
import de.christian2003.auth.ui.finish.FinishScreen
import de.christian2003.auth.ui.login.LoginScreen
import de.christian2003.auth.ui.password.PasswordScreen
import de.christian2003.auth.ui.recovery.RecoveryScreen
import de.christian2003.auth.ui.recoverycodes.RecoveryCodesScreen
import de.christian2003.auth.ui.settings.AuthSettingsScreen
import de.christian2003.auth.viewmodels.AuthSettingsViewModel
import de.christian2003.auth.viewmodels.BiometricsViewModel
import de.christian2003.auth.viewmodels.FinishViewModel
import de.christian2003.auth.viewmodels.LoginViewModel
import de.christian2003.auth.viewmodels.PasswordViewModel
import de.christian2003.auth.viewmodels.RecoveryCodesViewModel
import de.christian2003.auth.viewmodels.RecoveryViewModel
import de.christian2003.auth.viewmodels.SetupFlowSharedViewModel
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

@Serializable
internal data class FinishDestination(
    val state: FinishScreenState
)

@Serializable
internal object ChangePasswordFlowDestination


/**
 * Flow for the first-time master password setup.
 *
 * @param navController             Navigation controller.
 * @param onNotifyAuthSetupFinished Callback invoked to inform the nav host that the setup flow finished.
 * @param onBiometricAuth           Callback invoked for biometric authentication.
 */
fun NavGraphBuilder.setupFlowDestination(
    navController: NavController,
    onNotifyAuthSetupFinished: () -> Unit,
    onBiometricAuth: suspend () -> Boolean
) {
    navigation<SetupFlow>(
        startDestination = MasterPassword(PasswordScreenState.FirstTimeSetup)
    ) {
        val queryBackStackEntry: () -> NavBackStackEntry? = {
            navController.getParentBackStackEntry(SetupFlow)
        }

        //Master password setup:
        masterPasswordDestination(
            onQueryBackStackEntry = queryBackStackEntry,
            onNavigateUp = {
                navController.navigateUp()
            },
            onContinue = {
                navController.navigate(RecoveryCodes(RecoveryCodesScreenState.FirstTimeSetup))
            }
        )

        //Recovery codes setup:
        recoveryCodesDestination(
            onQueryBackStackEntry = queryBackStackEntry,
            onNavigateUp = {
                navController.navigateUp()
            },
            onContinue = {
                navController.navigate(Biometrics)
            }
        )

        //Biometrics setup:
        biometricsDestination(
            onQueryBackStackEntry = queryBackStackEntry,
            onNavigateUp = {
                navController.navigateUp()
            },
            onContinue = {
                navController.navigate(FinishDestination(state = FinishScreenState.FirstTimeSetup))
            },
            onBiometricAuth = onBiometricAuth
        )

        //Finish setup:
        finishDestination(
            onQueryBackStackEntry = queryBackStackEntry,
            onNavigateUp = {
                navController.navigateUp()
            },
            onFinish = {
                onNotifyAuthSetupFinished()
                navController.popBackStack(SetupFlow, true)
            }
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
        val queryBackStackEntry: () -> NavBackStackEntry? = {
            navController.getParentBackStackEntry(RecoveryFlow)
        }

        //Recovery using recovery codes:
        recoveryDestination(
            onQueryBackStackEntry = queryBackStackEntry,
            onNavigateUp = {
                navController.navigateUp()
            },
            onContinue = {
                navController.navigate(MasterPassword(state = PasswordScreenState.RecoverPassword))
            }
        )

        //Master password setup:
        masterPasswordDestination(
            onQueryBackStackEntry = queryBackStackEntry,
            onNavigateUp = {
                navController.navigateUp()
            },
            onContinue = {
                navController.navigate(RecoveryCodes(RecoveryCodesScreenState.RecoverPassword))
            }
        )

        //Recovery codes setup:
        recoveryCodesDestination(
            onQueryBackStackEntry = queryBackStackEntry,
            onNavigateUp = {
                navController.navigateUp()
            },
            onContinue = {
                navController.navigate(FinishDestination(state = FinishScreenState.RecoverPassword))
            }
        )

        //Finish recovery:
        finishDestination(
            onQueryBackStackEntry = queryBackStackEntry,
            onNavigateUp = {
                navController.navigateUp()
            },
            onFinish = {
                navController.popBackStack(RecoveryFlow, true)
            }
        )

    }
}


internal fun NavGraphBuilder.changePasswordFlowDestination(
    navController: NavController
) {
    navigation<ChangePasswordFlowDestination>(
        startDestination = MasterPassword(state = PasswordScreenState.ChangePassword)
    ) {
        val queryBackStackEntry: () -> NavBackStackEntry? = {
            navController.getParentBackStackEntry(ChangePasswordFlowDestination)
        }

        //Change password:
        masterPasswordDestination(
            onQueryBackStackEntry = queryBackStackEntry,
            onNavigateUp = {
                navController.navigateUp()
            },
            onContinue = {
                navController.navigate(FinishDestination(state = FinishScreenState.ChangePassword))
            }
        )

        //Finish changing password:
        finishDestination(
            onQueryBackStackEntry = queryBackStackEntry,
            onNavigateUp = {
                navController.navigateUp()
            },
            onFinish = {
                navController.popBackStack(ChangePasswordFlowDestination, true)
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
        val queryBackStackEntry: () -> NavBackStackEntry? = {
            navController.getParentBackStackEntry(AuthSettingsFlow)
        }

        //Base page for the auth settings:
        authSettingsDestination(
            onNavigateUp = {
                navController.navigateUp()
            },
            onNavigateToPassword = {
                navController.navigate(ChangePasswordFlowDestination)
            },
            onNavigateToBiometrics = {
                navController.navigate(Biometrics)
            },
            onNavigateToRecoveryCodes = {
                navController.navigate(RecoveryCodes(RecoveryCodesScreenState.RecoverPassword))
            }
        )

        //Change master password:
        changePasswordFlowDestination(
            navController = navController
        )

        //Enable / disable biometrics:
        biometricsDestination(
            onQueryBackStackEntry = queryBackStackEntry,
            onNavigateUp = {
                navController.navigateUp()
            },
            onContinue = {
                //TODO
            },
            onBiometricAuth = {
                //TODO
                false
            }
        )

        //Generate new recovery codes:
        recoveryCodesDestination(
            onQueryBackStackEntry = queryBackStackEntry,
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
    onQueryBackStackEntry: () -> NavBackStackEntry?,
    onNavigateUp: () -> Unit,
    onContinue: () -> Unit
) {
    composable<MasterPassword> {
        val parentBackStackEntry: NavBackStackEntry? = onQueryBackStackEntry()
        if (parentBackStackEntry != null) {
            val sharedViewModel: SetupFlowSharedViewModel = hiltViewModel(parentBackStackEntry)
            val viewModel: PasswordViewModel = hiltViewModel()

            PasswordScreen(
                viewModel = viewModel,
                sharedViewModel = sharedViewModel,
                onNavigateUp = onNavigateUp,
                onContinue = onContinue
            )
        }
    }
}


/**
 * Navigation destination for the screen for the setup of the recovery codes.
 *
 * @param onNavigateUp  Callback invoked to navigate up the navigation stack.
 * @param onContinue    Callback invoked to navigate to the next setup step.
 */
private fun NavGraphBuilder.recoveryCodesDestination(
    onQueryBackStackEntry: () -> NavBackStackEntry?,
    onNavigateUp: () -> Unit,
    onContinue: () -> Unit
) {
    composable<RecoveryCodes> {
        val parentBackStackEntry: NavBackStackEntry? = onQueryBackStackEntry()
        if (parentBackStackEntry != null) {
            val viewModel: RecoveryCodesViewModel = hiltViewModel()
            val sharedViewModel: SetupFlowSharedViewModel = hiltViewModel(parentBackStackEntry)

            RecoveryCodesScreen(
                viewModel = viewModel,
                sharedViewModel = sharedViewModel,
                onNavigateUp = onNavigateUp,
                onContinue = onContinue
            )
        }
    }
}


/**
 * Navigation destination for the screen for the setup of the biometric authentication.
 *
 * @param onNavigateUp      Callback invoked to navigate up the navigation stack.
 * @param onContinue        Callback invoked to navigate to the next setup step.
 * @param onBiometricAuth   Callback invoked for biometric authentication.
 */
private fun NavGraphBuilder.biometricsDestination(
    onQueryBackStackEntry: () -> NavBackStackEntry?,
    onNavigateUp: () -> Unit,
    onContinue: () -> Unit,
    onBiometricAuth: suspend () -> Boolean
) {
    composable<Biometrics> {
        val parentBackStackEntry: NavBackStackEntry? = onQueryBackStackEntry()
        if (parentBackStackEntry != null) {
            val viewModel: BiometricsViewModel = hiltViewModel()
            val sharedViewModel: SetupFlowSharedViewModel = hiltViewModel(parentBackStackEntry)

            BiometricsScreen(
                viewModel = viewModel,
                sharedViewModel = sharedViewModel,
                onNavigateUp = onNavigateUp,
                onContinue = onContinue,
                onBiometricAuth = onBiometricAuth
            )
        }
    }
}


private fun NavGraphBuilder.finishDestination(
    onQueryBackStackEntry: () -> NavBackStackEntry?,
    onNavigateUp: () -> Unit,
    onFinish: () -> Unit
) {
    composable<FinishDestination> {
        val parentBackStackEntry: NavBackStackEntry? = onQueryBackStackEntry()
        if (parentBackStackEntry != null) {
            val viewModel: FinishViewModel = hiltViewModel()
            val sharedViewModel: SetupFlowSharedViewModel = hiltViewModel(parentBackStackEntry)

            FinishScreen(
                viewModel = viewModel,
                sharedViewModel = sharedViewModel,
                onNavigateUp = onNavigateUp,
                onFinish = onFinish
            )
        }
    }
}


/**
 * Navigation destination for the recovery screen through which to enter a recovery code if the
 * user forgets their master password.
 *
 * @param onQueryBackStackEntry Callback invoked to query the back stack entry of the parent.
 * @param onNavigateUp          Callback invoked to navigate up the navigation stack.
 * @param onContinue            Callback invoked to navigate to the next recovery step.
 */
private fun NavGraphBuilder.recoveryDestination(
    onQueryBackStackEntry: () -> NavBackStackEntry?,
    onNavigateUp: () -> Unit,
    onContinue: () -> Unit
) {
    composable<Recovery> {
        val parentBackStackEntry: NavBackStackEntry? = onQueryBackStackEntry()
        if (parentBackStackEntry != null) {
            val viewModel: RecoveryViewModel = hiltViewModel()
            val sharedViewModel: SetupFlowSharedViewModel = hiltViewModel(parentBackStackEntry)

            RecoveryScreen(
                viewModel = viewModel,
                sharedViewModel = sharedViewModel,
                onNavigateUp = onNavigateUp,
                onContinue = onContinue
            )
        }
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


private inline fun <reified T : Any> NavController.getParentBackStackEntry(route: T): NavBackStackEntry? {
    return try {
        getBackStackEntry(route)
    } catch (_: Exception) {
        null
    }
}
