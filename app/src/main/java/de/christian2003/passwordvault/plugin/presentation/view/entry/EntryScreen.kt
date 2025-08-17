package de.christian2003.passwordvault.plugin.presentation.view.entry

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import de.christian2003.passwordvault.R


@Composable
fun EntryScreen(
    viewModel: EntryViewModel,
    onNavigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Entry")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateUp
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = ""
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = viewModel.name,
                onValueChange = {
                    viewModel.name = it
                },
                label = {
                    Text("Name")
                }
            )
            OutlinedTextField(
                value = viewModel.description,
                onValueChange = {
                    viewModel.description = it
                },
                label = {
                    Text("Description")
                }
            )
            Button(
                onClick = {
                    viewModel.save()
                    onNavigateUp()
                }
            ) {
                Text("Save")
            }
        }
    }
}
