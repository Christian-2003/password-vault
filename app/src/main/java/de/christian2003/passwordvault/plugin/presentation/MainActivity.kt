package de.christian2003.passwordvault.plugin.presentation

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
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
import de.christian2003.passwordvault.application.usecases.acount.CreateAccountUseCase
import de.christian2003.passwordvault.application.usecases.acount.DeleteAccountUseCase
import de.christian2003.passwordvault.application.usecases.acount.GetAccountByIdUseCase
import de.christian2003.passwordvault.application.usecases.acount.GetAllAccountDescriptorsUseCase
import de.christian2003.passwordvault.application.usecases.acount.UpdateAccountUseCase
import de.christian2003.passwordvault.domain.security.ClipboardService
import de.christian2003.passwordvault.plugin.PasswordVaultApplication
import de.christian2003.passwordvault.plugin.infrastructure.db.PasswordVaultRepository
import de.christian2003.passwordvault.plugin.infrastructure.security.AndroidClipboardService
import de.christian2003.passwordvault.plugin.presentation.ui.theme.PasswordVaultTheme
import de.christian2003.passwordvault.plugin.presentation.view.account.AccountScreen
import de.christian2003.passwordvault.plugin.presentation.view.account.AccountViewModel
import de.christian2003.passwordvault.plugin.presentation.view.help.HelpScreen
import de.christian2003.passwordvault.plugin.presentation.view.help.HelpViewModel
import de.christian2003.passwordvault.plugin.presentation.view.main.MainScreen
import de.christian2003.passwordvault.plugin.presentation.view.main.MainViewModel
import de.christian2003.passwordvault.plugin.presentation.view.settings.SettingsScreen
import de.christian2003.passwordvault.plugin.presentation.view.settings.SettingsViewModel
import java.util.UUID
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //App content:
        enableEdgeToEdge(
            navigationBarStyle = if (isNightMode()) {
                SystemBarStyle.dark(
                    scrim = android.graphics.Color.TRANSPARENT
                )
            } else {
                SystemBarStyle.light(
                    scrim = android.graphics.Color.TRANSPARENT,
                    darkScrim = android.graphics.Color.TRANSPARENT
                )
            }
        )
        setContent {
            PasswordVault()
        }
    }


    private fun isNightMode(): Boolean {
        val currentMode: Int = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentMode == Configuration.UI_MODE_NIGHT_YES
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
                val viewModel: MainViewModel = viewModel()
                viewModel.init(
                    getAllAccountDescriptorsUseCase = GetAllAccountDescriptorsUseCase(
                        accountRepository = repository
                    ),
                    deleteAccountUseCase = DeleteAccountUseCase(
                        accountRepository = repository
                    )
                )

                MainScreen(
                    viewModel = viewModel,
                    onEditAccount = { id ->
                        val idAsString: String = id.toString()
                        navController.navigate("account/$idAsString")
                    },
                    onCreateNewAccount = {
                        navController.navigate("account/")
                    },
                    onNavigateToSettings = {
                        navController.navigate("settings")
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
                    getAccountByIdUseCase = GetAccountByIdUseCase(repository),
                    createAccountUseCase = CreateAccountUseCase(repository, repository),
                    updateAccountUseCase = UpdateAccountUseCase(repository, repository),
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


            composable("settings") {
                val viewModel: SettingsViewModel = viewModel()
                viewModel.init()

                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    },
                    onNavigateToHelp = {
                        navController.navigate("help")
                    }
                )
            }


            composable("help") {
                val viewModel: HelpViewModel = viewModel()
                viewModel.init()

                HelpScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}
