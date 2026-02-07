package de.christian2003.autofillservicetester

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.christian2003.autofillservicetester.ui.theme.AutofillServiceTesterTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AutofillServiceTesterTheme {
                val navController = rememberNavController()

                NavHost(navController, "main") {

                    composable("main") {
                        MainScreen(
                            onContinue = {
                                navController.navigate("finish")
                            }
                        )
                    }

                    composable("finish") {
                        FinishScreen()
                    }

                }
            }
        }
    }
}
