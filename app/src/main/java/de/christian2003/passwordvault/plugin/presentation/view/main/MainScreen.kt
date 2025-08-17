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
    onNavigateToEntries: () -> Unit,
    onCreateNewEntry: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Button(
                onClick = onNavigateToEntries
            ) {
                Text("All entries")
            }
            Button(
                onClick = onCreateNewEntry
            ) {
                Text("Create entry")
            }
        }
    }
}
