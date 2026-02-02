package de.christian2003.auth.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import de.christian2003.auth.models.states.BiometricsScreenState
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


// =============== Public destinations ===============

@Serializable
object SetupFlow

@Serializable
object AuthSettingsFlow

@Serializable
object RecoveryFlow

@Serializable
object LoginDestination


/**
 * Flow for the first-time master password setup.
 *
 * @param navController             Navigation controller.
 * @param onNotifyAuthSetupFinished Callback invoked to inform the nav host that the setup flow finished.
 * @param onBiometricAuth           Callback invoked for biometric authentication.
 */
fun NavGraphBuilder.setupFlow(
    navController: NavController,
    onNotifyAuthSetupFinished: () -> Unit,
    onBiometricAuth: suspend () -> Boolean
) {
    navigation<SetupFlow>(
        startDestination = PasswordDestination(PasswordScreenState.FirstTimeSetup)
    ) {
        val queryBackStackEntry: () -> NavBackStackEntry? = {
            navController.getParentBackStackEntry(SetupFlow)
        }

        //Master password setup:
        passwordDestination(
            onQueryBackStackEntry = queryBackStackEntry,
            onNavigateUp = {
                navController.navigateUp()
            },
            onContinue = {
                navController.navigate(RecoveryCodesDestination(RecoveryCodesScreenState.FirstTimeSetup))
            }
        )

        //Recovery codes setup:
        recoveryCodesDestination(
            onQueryBackStackEntry = queryBackStackEntry,
            onNavigateUp = {
                navController.navigateUp()
            },
            onContinue = {
                navController.navigate(BiometricsDestination(state = BiometricsScreenState.FirstTimeSetup))
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
            onFinish = {
                onNotifyAuthSetupFinished()
                navController.popBackStack(SetupFlow, true)
            }
        )

    }
}


/**
 * Flow for the authentication settings.
 *
 * @param navController     Navigation controller.
 * @param onBiometricAuth   Callback invoked for biometric authentication.
 */
fun NavGraphBuilder.authSettingsFlow(
    navController: NavController,
    onBiometricAuth: suspend () -> Boolean
) {
    navigation<AuthSettingsFlow>(
        startDestination = AuthSettingsDestination
    ) {

        //Base page for the auth settings:
        authSettingsDestination(
            onNavigateUp = {
                navController.navigateUp()
            },
            onNavigateToPassword = {
                navController.navigate(ChangePasswordFlow)
            },
            onNavigateToBiometrics = {
                navController.navigate(EnableBiometricsFlow)
            },
            onNavigateToRecoveryCodes = {
                navController.navigate(GenerateNewRecoveryCodesFlow)
            }
        )

        //Change master password:
        changePasswordFlow(
            navController = navController
        )

        //Generate new recovery codes:
        generateNewRecoveryCodesFlow(
            navController = navController
        )

        //Enable biometrics flow:
        enableBiometricsFlow(
            navController = navController,
            onBiometricAuth = onBiometricAuth
        )

    }
}


/**
 * Flow for the recovery of the master password.
 *
 * @param navController Navigation controller.
 */
fun NavGraphBuilder.recoveryFlow(
    navController: NavController
) {
    navigation<RecoveryFlow>(
        startDestination = RecoveryDestination
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
                navController.navigate(PasswordDestination(state = PasswordScreenState.RecoverPassword))
            }
        )

        //Master password setup:
        passwordDestination(
            onQueryBackStackEntry = queryBackStackEntry,
            onNavigateUp = {
                navController.navigateUp()
            },
            onContinue = {
                navController.navigate(RecoveryCodesDestination(RecoveryCodesScreenState.RecoverPassword))
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
            onFinish = {
                navController.popBackStack(RecoveryFlow, true)
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
    composable<LoginDestination> {
        val viewModel: LoginViewModel = hiltViewModel()

        LoginScreen(
            viewModel = viewModel,
            onContinue = onContinue,
            onNavigateToRecovery = onNavigateToRecovery,
            onBiometricUnlock = onBiometricUnlock
        )
    }
}



// =============== Internal destinations ===============

@Serializable
private object ChangePasswordFlow

@Serializable
private object GenerateNewRecoveryCodesFlow

@Serializable
private object EnableBiometricsFlow

@Serializable
private object RecoveryDestination

@Serializable
private object AuthSettingsDestination

@Serializable
internal data class PasswordDestination(
    val state: PasswordScreenState
)

@Serializable
internal data class BiometricsDestination(
    val state: BiometricsScreenState
)

@Serializable
internal data class RecoveryCodesDestination(
    val state: RecoveryCodesScreenState
)

@Serializable
internal data class FinishDestination(
    val state: FinishScreenState
)


/**
 * Flow to change the master password.
 *
 * @param navController Navigation controller.
 */
private fun NavGraphBuilder.changePasswordFlow(
    navController: NavController
) {
    navigation<ChangePasswordFlow>(
        startDestination = PasswordDestination(state = PasswordScreenState.ChangePassword)
    ) {
        val queryBackStackEntry: () -> NavBackStackEntry? = {
            navController.getParentBackStackEntry(ChangePasswordFlow)
        }

        //Change password:
        passwordDestination(
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
            onFinish = {
                navController.popBackStack(ChangePasswordFlow, true)
            }
        )

    }
}


/**
 * Flow to generate new recovery codes.
 *
 * @param navController Navigation controller.
 */
private fun NavGraphBuilder.generateNewRecoveryCodesFlow(
    navController: NavController
) {
    navigation<GenerateNewRecoveryCodesFlow>(
        startDestination = PasswordDestination(state = PasswordScreenState.GenerateNewRecoveryCodes)
    ) {
        val queryBackStackEntry: () -> NavBackStackEntry? = {
            navController.getParentBackStackEntry(GenerateNewRecoveryCodesFlow)
        }

        //Authenticate:
        passwordDestination(
            onQueryBackStackEntry = queryBackStackEntry,
            onNavigateUp = {
                navController.navigateUp()
            },
            onContinue = {
                navController.navigate(RecoveryCodesDestination(state = RecoveryCodesScreenState.GenerateNewRecoveryCodes))
            }
        )

        //New recovery codes:
        recoveryCodesDestination(
            onQueryBackStackEntry = queryBackStackEntry,
            onNavigateUp = {
                navController.navigateUp()
            },
            onContinue = {
                navController.navigate(FinishDestination(state = FinishScreenState.GenerateNewRecoveryCodes))
            }
        )

        //Finish:
        finishDestination(
            onQueryBackStackEntry = queryBackStackEntry,
            onFinish = {
                navController.popBackStack(GenerateNewRecoveryCodesFlow, true)
            }
        )

    }
}


/**
 * Flow to enable the biometric authentication.
 *
 * @param navController     Navigation controller.
 * @param onBiometricAuth   Callback invoked to perform biometric authentication.
 */
private fun NavGraphBuilder.enableBiometricsFlow(
    navController: NavController,
    onBiometricAuth: suspend () -> Boolean
) {
    navigation<EnableBiometricsFlow>(
        startDestination = PasswordDestination(state = PasswordScreenState.EnableBiometrics)
    ) {
        val queryBackStackEntry: () -> NavBackStackEntry? = {
            navController.getParentBackStackEntry(EnableBiometricsFlow)
        }

        //Authenticate:
        passwordDestination(
            onQueryBackStackEntry = queryBackStackEntry,
            onNavigateUp = {
                navController.navigateUp()
            },
            onContinue = {
                navController.navigate(BiometricsDestination(state = BiometricsScreenState.EnableBiometrics))
            }
        )

        //Biometrics:
        biometricsDestination(
            onQueryBackStackEntry = queryBackStackEntry,
            onNavigateUp = {
                navController.navigateUp()
            },
            onContinue = {
                navController.navigate(FinishDestination(state = FinishScreenState.EnableBiometrics))
            },
            onBiometricAuth = onBiometricAuth
        )

        //Finish:
        finishDestination(
            onQueryBackStackEntry = queryBackStackEntry,
            onFinish = {
                navController.popBackStack(EnableBiometricsFlow, true)
            }
        )

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
    composable<RecoveryDestination> {
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
    composable<AuthSettingsDestination> {
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


/**
 * Navigation destination for the screen to set a new paster password.
 *
 * @param onQueryBackStackEntry Callback invoked to query the parent back stack.
 * @param onNavigateUp      Callback invoked to navigate up the navigation stack.
 * @param onContinue        Callback invoked to navigate to the next setup step.
 */
private fun NavGraphBuilder.passwordDestination(
    onQueryBackStackEntry: () -> NavBackStackEntry?,
    onNavigateUp: () -> Unit,
    onContinue: () -> Unit
) {
    composable<PasswordDestination> {
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
 * Navigation destination for the screen for the setup of the biometric authentication.
 *
 * @param onQueryBackStackEntry Callback invoked to query the parent back stack.
 * @param onNavigateUp          Callback invoked to navigate up the navigation stack.
 * @param onContinue            Callback invoked to navigate to the next setup step.
 * @param onBiometricAuth       Callback invoked for biometric authentication.
 */
private fun NavGraphBuilder.biometricsDestination(
    onQueryBackStackEntry: () -> NavBackStackEntry?,
    onNavigateUp: () -> Unit,
    onContinue: () -> Unit,
    onBiometricAuth: suspend () -> Boolean
) {
    composable<BiometricsDestination> {
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


/**
 * Navigation destination for the screen for the setup of the recovery codes.
 *
 * @param onQueryBackStackEntry Callback invoked to query the parent back stack.
 * @param onNavigateUp          Callback invoked to navigate up the navigation stack.
 * @param onContinue            Callback invoked to navigate to the next setup step.
 */
private fun NavGraphBuilder.recoveryCodesDestination(
    onQueryBackStackEntry: () -> NavBackStackEntry?,
    onNavigateUp: () -> Unit,
    onContinue: () -> Unit
) {
    composable<RecoveryCodesDestination> {
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
 * Navigation destination for the screen to finish changing auth data through flows.
 *
 * @param onQueryBackStackEntry Callback invoked to query the parent back stack.
 * @param onFinish              Callback invoked to finish.
 */
private fun NavGraphBuilder.finishDestination(
    onQueryBackStackEntry: () -> NavBackStackEntry?,
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
                onFinish = onFinish
            )
        }
    }
}



// =============== Helpers ===============

/**
 * Returns the parent back stack entry for the specified route.
 *
 * @param route Route of the parent.
 * @return      Back stack entry of the parent.
 */
private inline fun <reified T : Any> NavController.getParentBackStackEntry(route: T): NavBackStackEntry? {
    return try {
        getBackStackEntry(route)
    } catch (_: Exception) {
        null
    }
}
