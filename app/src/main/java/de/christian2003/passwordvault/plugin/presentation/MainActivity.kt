package de.christian2003.passwordvault.plugin.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import de.christian2003.passwordvault.plugin.presentation.ui.theme.PasswordVaultTheme


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
    PasswordVaultTheme {
        Text("Hello World")
    }
}
