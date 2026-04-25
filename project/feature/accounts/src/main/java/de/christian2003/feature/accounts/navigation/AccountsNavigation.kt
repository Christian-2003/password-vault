package de.christian2003.feature.accounts.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import de.christian2003.feature.accounts.ui.account.AccountScreen
import de.christian2003.feature.accounts.ui.main.MainScreen
import de.christian2003.feature.accounts.viewmodels.AccountViewModel
import de.christian2003.feature.accounts.viewmodels.MainViewModel
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid


// =============== Public destinations ===============

@Serializable
object AccountsDestination

@Serializable
data class AccountDestination(
    val accountId: String?
)


/**
 * Navigation destination for the screen through which to view a list of all accounts.
 *
 * @param onEditAccount         Callback invoked to edit the account whose ID is provided.
 * @param onCreateNewAccount    Callback invoked to create a new account.
 * @param onNavigateToSettings  Callback invoked to navigate to the settings.
 * @param onNavigateToSearch    Callback invoked to navigate to the search screen.
 * @param onNavigateToFiles     Callback invoked to navigate to the files.
 * @param onNavigateToAnalysis  Callback invoked to navigate to the password security analysis.
 */
fun NavGraphBuilder.accountsDestination(
    onEditAccount: (Uuid) -> Unit,
    onCreateNewAccount: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToFiles: () -> Unit,
    onNavigateToAnalysis: () -> Unit
) {
    composable<AccountsDestination> {
        val viewModel: MainViewModel = hiltViewModel()

        MainScreen(
            viewModel = viewModel,
            onEditAccount = onEditAccount,
            onCreateNewAccount = onCreateNewAccount,
            onNavigateToSettings =  onNavigateToSettings,
            onNavigateToSearch = onNavigateToSearch,
            onNavigateToFiles = onNavigateToFiles,
            onNavigateToAnalysis = onNavigateToAnalysis
        )
    }
}


/**
 * Navigation destination to view, edit or create accounts.
 *
 * @param onNavigateUp  Callback invoked to navigate up the navigation stack.
 */
fun NavGraphBuilder.accountDestination(
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
