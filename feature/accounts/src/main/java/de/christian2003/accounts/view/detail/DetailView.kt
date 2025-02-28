package de.christian2003.accounts.view.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import de.christian2003.accounts.R
import de.christian2003.core.ui.composables.TextInput


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailView(
    viewModel: DetailViewModel,
    onNavigateUp: () -> Unit
) {
    DisposableEffect(Unit) {
        onDispose {
            viewModel.save()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if(viewModel.isCreatingNewDetail) { stringResource(R.string.detail_title_create) } else { stringResource(R.string.detail_title_edit) },
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onNavigateUp()
                        }
                    ) {
                        Icon(
                            painter = painterResource(de.christian2003.core.R.drawable.ic_back),
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = ""
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            //Enter name:
            TextInput(
                value = viewModel.name,
                onValueChange = {
                    viewModel.name = it
                },
                label = stringResource(R.string.detail_name_label),
                prefixIcon = painterResource(de.christian2003.core.R.drawable.ic_title),
                errorMessage = if (viewModel.name.isEmpty()) { stringResource(R.string.detail_name_error) } else { null },
                modifier = Modifier
                    .padding(
                        start = dimensionResource(de.christian2003.core.R.dimen.padding_horizontal_activity),
                        top = dimensionResource(de.christian2003.core.R.dimen.padding_vertical_activity),
                        end = dimensionResource(de.christian2003.core.R.dimen.padding_horizontal_activity)
                    )
            )

            //Enter content:
            TextInput(
                value = viewModel.content,
                onValueChange = {
                    viewModel.content = it
                },
                label = stringResource(R.string.detail_content_label),
                prefixIcon = painterResource(de.christian2003.core.R.drawable.ic_description),
                errorMessage = if (viewModel.content.isEmpty()) { stringResource(R.string.detail_content_error) } else { null },
                modifier = Modifier.padding(
                    horizontal = dimensionResource(de.christian2003.core.R.dimen.padding_horizontal_activity),
                    vertical = dimensionResource(de.christian2003.core.R.dimen.padding_vertical)
                )
            )
        }
    }
}
