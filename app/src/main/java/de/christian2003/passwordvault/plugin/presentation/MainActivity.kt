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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.christian2003.passwordvault.plugin.PasswordVaultApplication
import de.christian2003.passwordvault.plugin.infrastructure.db.PasswordVaultRepository
import de.christian2003.passwordvault.plugin.presentation.ui.theme.PasswordVaultTheme
import de.christian2003.passwordvault.plugin.presentation.view.entries.EntriesScreen
import de.christian2003.passwordvault.plugin.presentation.view.entries.EntriesViewModel
import de.christian2003.passwordvault.plugin.presentation.view.entry.EntryScreen
import de.christian2003.passwordvault.plugin.presentation.view.entry.EntryViewModel
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

    PasswordVaultTheme {
        NavHost(
            navController = navController,
            startDestination = "main",
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            composable("main") {
                MainScreen(
                    onNavigateToEntries = {
                        navController.navigate("entries")
                    },
                    onCreateNewEntry = {
                        navController.navigate("entry/")
                    }
                )
            }
            composable("entries") {
                val viewModel: EntriesViewModel = viewModel()
                viewModel.init(repository)
                EntriesScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    },
                    onNavigateToEntry = { id ->
                        val idAsString: String = id.toString()
                        navController.navigate("entry/$idAsString")
                    }
                )
            }
            composable(
                route = "entry/{entryId}",
                arguments = listOf(
                    navArgument("entryId") { type = NavType.StringType}
                )
            ) { backStackEntry ->
                val id: Uuid? = try {
                    UUID.fromString(backStackEntry.arguments!!.getString("entryId")).toKotlinUuid() //Wtf is this shit?
                } catch (_: Exception) {
                    null
                }
                val viewModel: EntryViewModel = viewModel()
                viewModel.init(
                    entryRepository = repository,
                    id = id
                )
                EntryScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}
