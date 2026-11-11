package de.christian2003.filesysteminspector

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.christian2003.filesysteminspector.ui.theme.FilesystemInspectorTheme
import de.christian2003.filesysteminspector.ui.view.MainScreen
import de.christian2003.filesysteminspector.ui.viewmodel.MainViewModel


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
    FilesystemInspectorTheme {
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
        }
    }
}
