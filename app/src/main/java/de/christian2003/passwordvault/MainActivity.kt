package de.christian2003.passwordvault

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
                        navController.navigate("AccountView")
                    },
                    onEditAccountClicked = { account ->
                        val encodedId = Uri.encode(account.id.toString())
                        navController.navigate("AccountView?id=$encodedId")
                    }
                )
            }
            composable(
                route = "AccountView?id={id}",
                arguments = listOf(
                    navArgument("id") { nullable = true; type = NavType.StringType; defaultValue = null }
                )
            ) { backStackEntry ->
                val id: UUID? = try {
                    UUID.fromString(Uri.decode(backStackEntry.arguments?.getString("id")))
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
                    onAddDetail = { accountId ->
                        val accountIdEncoded = Uri.encode(accountId.toString())
                        navController.navigate("DetailView?accountId=$accountIdEncoded")
                    },
                    onEditDetail = { detailId, accountId ->
                        val detailIdEncoded = Uri.encode(detailId.toString())
                        val accountIdEncoded = Uri.encode(accountId.toString())
                        navController.navigate("DetailView?detailId=$detailIdEncoded&accountId=$accountIdEncoded")
                    }
                )
            }
            composable(
                route = "DetailView?detailId={detailId}&accountId={accountId}",
                arguments = listOf(
                    navArgument("detailId") { nullable = true; type = NavType.StringType; defaultValue = null },
                    navArgument("accountId") { nullable = false; type = NavType.StringType }
                )
            ) { backStackEntry ->
                val detailId: UUID? = try {
                    UUID.fromString(Uri.decode(backStackEntry.arguments?.getString("detailId")))
                } catch (e: Exception) {
                    null
                }
                val accountId: UUID? = try {
                    UUID.fromString(Uri.decode(backStackEntry.arguments?.getString("accountId")))
                } catch (e: Exception) {
                    null
                }
                if (accountId != null) {
                    val viewModel: DetailViewModel = viewModel()
                    viewModel.init(repository, accountId, detailId)

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
}
