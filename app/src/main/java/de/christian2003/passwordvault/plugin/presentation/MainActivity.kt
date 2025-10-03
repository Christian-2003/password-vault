package de.christian2003.passwordvault.plugin.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.christian2003.passwordvault.domain.security.ClipboardService
import de.christian2003.passwordvault.plugin.PasswordVaultApplication
import de.christian2003.passwordvault.plugin.infrastructure.db.PasswordVaultRepository
import de.christian2003.passwordvault.plugin.infrastructure.security.AndroidClipboardService
import de.christian2003.passwordvault.plugin.presentation.ui.theme.PasswordVaultTheme
import de.christian2003.passwordvault.plugin.presentation.view.accounts.AccountsScreen
import de.christian2003.passwordvault.plugin.presentation.view.accounts.AccountsViewModel
import de.christian2003.passwordvault.plugin.presentation.view.account.AccountScreen
import de.christian2003.passwordvault.plugin.presentation.view.account.AccountViewModel
import de.christian2003.passwordvault.plugin.presentation.view.main.MainScreen
import java.util.UUID
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid


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
    val navController: NavHostController = rememberNavController()
    val context: Context = LocalContext.current
    val repository: PasswordVaultRepository = (context.applicationContext as PasswordVaultApplication).getRepository()
    val clipboardService: ClipboardService = AndroidClipboardService(LocalClipboard.current.nativeClipboard)

    PasswordVaultTheme {
        NavHost(
            navController = navController,
            startDestination = "main",
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            composable("main") {
                MainScreen(
                    onNavigateToAccounts = {
                        navController.navigate("accounts")
                    },
                    onCreateNewAccount = {
                        navController.navigate("account/")
                    }
                )
            }

            composable("accounts") {
                val viewModel: AccountsViewModel = viewModel()
                viewModel.init(repository)
                AccountsScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    },
                    onNavigateToAccount = { id ->
                        val idAsString: String = id.toString()
                        navController.navigate("account/$idAsString")
                    }
                )
            }

            composable(
                route = "account/{accountId}",
                arguments = listOf(
                    navArgument("accountId") { type = NavType.StringType}
                )
            ) { backStackEntry ->
                val id: Uuid? = try {
                    UUID.fromString(backStackEntry.arguments!!.getString("accountId")).toKotlinUuid() //Wtf is this shit?
                } catch (_: Exception) {
                    null
                }
                val viewModel: AccountViewModel = viewModel()
                viewModel.init(
                    accountRepository = repository,
                    detailRepository = repository,
                    tagRepository = repository,
                    clipboardService = clipboardService,
                    id = id
                )
                AccountScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}
