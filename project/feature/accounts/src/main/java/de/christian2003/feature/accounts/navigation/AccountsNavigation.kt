package de.christian2003.feature.accounts.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import de.christian2003.feature.accounts.ui.account.AccountScreen
import de.christian2003.feature.accounts.ui.main.MainScreen
import de.christian2003.feature.accounts.viewmodels.AccountViewModel
import de.christian2003.feature.accounts.viewmodels.MainViewModel
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid


// =============== Public destinations ===============

@Serializable
object AccountsFlow


/**
 * Flow through which to view, create, edit or delete accounts.
 *
 * @param navController         Navigation controller.
 * @param onNavigateToSettings  Callback invoked to navigate to the app settings.
 * @param onNavigateToSearch    Callback invoked to navigate to the search screen.
 */
fun NavGraphBuilder.accountsFlow(
    navController: NavController,
    onNavigateToSettings: () -> Unit,
    onNavigateToSearch: () -> Unit
) {
    navigation<AccountsFlow>(
        startDestination = AccountsDestination
    ) {

        //Accounts:
        accountsDestination(
            onEditAccount = { accountId ->
                navController.navigate(AccountDestination(accountId.toString()))
            },
            onCreateNewAccount = {
                navController.navigate(AccountDestination(null))
            },
            onNavigateToSettings = onNavigateToSettings,
            onNavigateToSearch = onNavigateToSearch
        )

        //Create, edit or delete accounts:
        accountDestination(
            onNavigateUp = {
                navController.navigateUp()
            }
        )

    }
}



// =============== Internal destinations ===============

@Serializable
private object AccountsDestination

@Serializable
internal data class AccountDestination(
    val accountId: String?
)


/**
 * Navigation destination for the screen through which to view a list of all accounts.
 *
 * @param onEditAccount         Callback invoked to edit the account whose ID is provided.
 * @param onCreateNewAccount    Callback invoked to create a new account.
 * @param onNavigateToSettings  Callback invoked to navigate to the settings.
 * @param onNavigateToSearch    Callback invoked to navigate to the search screen.
 */
private fun NavGraphBuilder.accountsDestination(
    onEditAccount: (Uuid) -> Unit,
    onCreateNewAccount: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSearch: () -> Unit
) {
    composable<AccountsDestination> {
        val viewModel: MainViewModel = hiltViewModel()

        MainScreen(
            viewModel = viewModel,
            onEditAccount = onEditAccount,
            onCreateNewAccount = onCreateNewAccount,
            onNavigateToSettings =  onNavigateToSettings,
            onNavigateToSearch = onNavigateToSearch
        )
    }
}


/**
 * Navigation destination to view, edit or create accounts.
 *
 * @param onNavigateUp  Callback invoked to navigate up the navigation stack.
 */
private fun NavGraphBuilder.accountDestination(
    onNavigateUp: () -> Unit
) {
    composable<AccountDestination> {
        val viewModel: AccountViewModel = hiltViewModel()

        AccountScreen(
            viewModel = viewModel,
            onNavigateUp = onNavigateUp
        )
    }
}
