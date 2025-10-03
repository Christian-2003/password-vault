package de.christian2003.passwordvault.plugin.presentation.view.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun MainScreen(
    onNavigateToAccounts: () -> Unit,
    onCreateNewAccount: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Button(
                onClick = onNavigateToAccounts
            ) {
                Text("All entries")
            }
            Button(
                onClick = onCreateNewAccount
            ) {
                Text("Create entry")
            }
        }
    }
}
