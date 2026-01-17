package de.christian2003.auth.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import de.christian2003.auth.ui.password.PasswordScreen
import de.christian2003.auth.viewmodels.PasswordViewModel
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

        masterPasswordDestination(
            onNavigateUp = {
                navController.navigateUp()
            },
            onContinue = {
                navController.navigate(RecoveryCodes)
            }
        )
    }
}


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
