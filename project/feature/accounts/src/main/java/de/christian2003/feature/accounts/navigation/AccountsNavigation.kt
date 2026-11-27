package de.christian2003.feature.accounts.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import de.christian2003.feature.accounts.ui.account.AccountScreen
import de.christian2003.feature.accounts.ui.accounts.AccountsView
import de.christian2003.feature.accounts.viewmodels.AccountViewModel
import de.christian2003.feature.accounts.viewmodels.AccountsViewModel
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
 * @param viewWrapper           Wrapper for the view.
 */
fun NavGraphBuilder.accountsDestination(
    onEditAccount: (Uuid) -> Unit,
    onCreateNewAccount: () -> Unit,
    viewWrapper: @Composable (
        content: @Composable (innerPadding: PaddingValues) -> Unit
    ) -> Unit
) {
    composable<AccountsDestination> {
        val viewModel: AccountsViewModel = hiltViewModel()

        viewWrapper { innerPadding ->
            AccountsView(
                viewModel = viewModel,
                innerPadding = innerPadding,
                onEditAccount = onEditAccount,
                onCreateNewAccount = onCreateNewAccount,
            )
        }
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
