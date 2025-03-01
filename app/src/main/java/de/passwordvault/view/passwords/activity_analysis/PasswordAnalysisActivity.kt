package de.passwordvault.view.passwords.activity_analysis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import de.passwordvault.ui.theme.PasswordVaultTheme

class PasswordAnalysisActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PasswordVaultTheme {
                PasswordAnalysisScreen()
            }
        }
    }

}
