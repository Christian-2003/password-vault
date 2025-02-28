package de.christian2003.passwordvault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.christian2003.accounts.database.AccountsRepository
import de.christian2003.accounts.database.AccountsDatabase
import de.christian2003.accounts.view.account.AccountView
import de.christian2003.accounts.view.account.AccountViewModel
import de.christian2003.accounts.view.accountslist.AccountsListView
import de.christian2003.accounts.view.accountslist.AccountsListViewModel
import de.christian2003.accounts.view.detail.DetailView
import de.christian2003.accounts.view.detail.DetailViewModel
import de.christian2003.core.ui.theme.PasswordVaultTheme
import java.util.UUID


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PasswordVault()
        }
    }
}


@Composable
fun PasswordVault() {
    val navController = rememberNavController()
    val database = AccountsDatabase.getInstance(LocalContext.current)
    val repository = AccountsRepository(database.accountDao, database.detailDao)

    PasswordVaultTheme {
        NavHost(
            navController = navController,
            startDestination = "AccountsListView"
        ) {
            composable("AccountsListView") {
                val viewModel: AccountsListViewModel = viewModel()
                viewModel.init(repository)

                AccountsListView(
                    viewModel = viewModel,
                    onAddAccountClicked = {
                        navController.navigate("AccountView/")
                    },
                    onEditAccountClicked = { account ->
                        navController.navigate("AccountView/${account.id}")
                    }
                )
            }
            composable("AccountView/{id}") { backStackEntry ->
                val id: UUID? = try {
                    UUID.fromString(backStackEntry.arguments?.getString("id"))
                } catch (e: Exception) {
                    null
                }
                val viewModel: AccountViewModel = viewModel()
                viewModel.init(repository, id)

                AccountView(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    },
                    onAddDetail = {
                        navController.navigate("DetailView/")
                    },
                    onEditDetail = { detailId ->
                        navController.navigate("DetailView/$detailId")
                    }
                )
            }
            composable("DetailView/{id}") { backStackEntry ->
                val id: UUID? = try {
                    UUID.fromString(backStackEntry.arguments?.getString("id"))
                } catch (e: Exception) {
                    null
                }
                val viewModel: DetailViewModel = viewModel()
                viewModel.init(repository, id)

                DetailView(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}
