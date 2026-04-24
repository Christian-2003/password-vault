package de.christian2003.signinginfoinspector

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.christian2003.signinginfoinspector.ui.theme.SigningInfoInspectorTheme
import de.christian2003.signinginfoinspector.ui.view.MainScreen
import de.christian2003.signinginfoinspector.ui.view.PackageScreen
import de.christian2003.signinginfoinspector.ui.viewmodel.MainViewModel
import de.christian2003.signinginfoinspector.ui.viewmodel.PackageViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SigningInfoInspector()
        }
    }
}

@Composable
private fun SigningInfoInspector() {
    val navController = rememberNavController()
    SigningInfoInspectorTheme {
        NavHost(
            navController = navController,
            startDestination = "main"
        ) {
            composable("main") {
                val viewModel: MainViewModel = viewModel()
                MainScreen(
                    viewModel = viewModel,
                    onNavigateToPackage = { packageName ->
                        navController.navigate("package/$packageName")
                    }
                )
            }
            composable("package/{packageName}") { backStackEntry ->
                val packageName: String? = backStackEntry.arguments?.getString("packageName")
                if (packageName == null) {
                    navController.navigateUp()
                    return@composable
                }
                val viewModel: PackageViewModel = viewModel()
                viewModel.init(packageName)
                PackageScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}
