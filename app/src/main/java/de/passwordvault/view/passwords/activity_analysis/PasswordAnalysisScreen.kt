package de.passwordvault.view.passwords.activity_analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.passwordvault.ui.theme.LocalPasswordVaultColors


@Composable
fun PasswordAnalysisScreen(

) {
    Scaffold(

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LocalPasswordVaultColors.current.backgroundAppBar)
                .padding(innerPadding)
        ) {
            Text(
                text = "Hello, World",
                color = LocalPasswordVaultColors.current.red
            )
            Text(
                text = "Hello, World",
                color = LocalPasswordVaultColors.current.yellow
            )
            Text(
                text = "Hello, World",
                color = LocalPasswordVaultColors.current.green
            )
        }
    }
}
