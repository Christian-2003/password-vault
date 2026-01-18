package de.christian2003.auth.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import de.christian2003.auth.ui.biometrics.BiometricsScreen
import de.christian2003.auth.ui.password.PasswordScreen
import de.christian2003.auth.ui.recoverycodes.RecoveryCodesScreen
import de.christian2003.auth.viewmodels.BiometricsViewModel
import de.christian2003.auth.viewmodels.PasswordViewModel
import de.christian2003.auth.viewmodels.RecoveryCodesViewModel
import kotlinx.serialization.Serializable


@Serializable object SetupFlow
@Serializable object MasterPassword
@Serializable object Login
@Serializable object Biometrics
@Serializable object RecoveryCodes
@Serializable object Recovery


/**
 * Flow for the first-time master password setup
 */
fun NavGraphBuilder.setupFlowDestination(
    navController: NavController
) {
    navigation<SetupFlow>(
        startDestination = MasterPassword
    ) {

        //Master password setup:
        masterPasswordDestination(
            onNavigateUp = {
                navController.navigateUp()
            },
            onContinue = {
                navController.navigate(RecoveryCodes)
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
                //TODO: Add next step
            }
        )

    }
}


/**
 * Navigation destination for the screen to set a new paster password.
 * 
 * @param onNavigateUp  Callback invoked to navigate up the navigation stack.
 * @param onContinue    Callback invoked to navigate to the next setup step.
 */
fun NavGraphBuilder.masterPasswordDestination(
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
fun NavGraphBuilder.recoveryCodesDestination(
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
 * @param onNavigateUp  Callback invoked to navigate up the navigation stack.
 * @param onContinue    Callback invoked to navigate to the next setup step.
 */
fun NavGraphBuilder.biometricsDestination(
    onNavigateUp: () -> Unit,
    onContinue: () -> Unit
) {
    composable<Biometrics> {
        val viewModel: BiometricsViewModel = hiltViewModel()

        BiometricsScreen(
            viewModel = viewModel,
            onNavigateUp = onNavigateUp,
            onContinue = onContinue
        )
    }
}
