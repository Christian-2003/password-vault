package de.christian2003.passwordvault.plugin.presentation.view.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import de.christian2003.passwordvault.R
import de.christian2003.passwordvault.domain.entry.DetailIcon
import de.christian2003.passwordvault.plugin.presentation.ui.composables.Checkbox
import de.christian2003.passwordvault.plugin.presentation.ui.composables.Headline
import de.christian2003.passwordvault.plugin.presentation.ui.composables.TextInput

@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onNavigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (viewModel.isCreatingNewDetail) { stringResource(R.string.detail_titleCreate) } else { stringResource(R.string.detail_titleEdit) })
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
                .verticalScroll(rememberScrollState())
        ) {
            TextInput(
                value = viewModel.name,
                onValueChange = {
                    viewModel.name = it
                },
                label = stringResource(R.string.detail_nameLabel),
                prefixIcon = painterResource(R.drawable.ic_name),
                modifier = Modifier.padding(
                    horizontal = dimensionResource(R.dimen.margin_horizontal)
                )
            )
            TextInput(
                value = viewModel.content,
                onValueChange = {
                    viewModel.content = it
                },
                label = stringResource(R.string.detail_contentLabel),
                prefixIcon = painterResource(R.drawable.ic_content),
                modifier = Modifier.padding(
                    horizontal = dimensionResource(R.dimen.margin_horizontal),
                    vertical = dimensionResource(R.dimen.padding_vertical)
                ),
                visualTransformation = if (viewModel.isObfuscated) { PasswordVisualTransformation() } else { VisualTransformation.None }
            )
            Checkbox(
                checked = viewModel.isObfuscated,
                onCheckedChange = {
                    viewModel.isObfuscated = it
                },
                title = stringResource(R.string.detail_obfuscatedTitle),
                text = stringResource(R.string.detail_obfuscatedText)
            )
            Checkbox(
                checked = viewModel.isVisible,
                onCheckedChange = {
                    viewModel.isVisible = it
                },
                title = stringResource(R.string.detail_visibleTitle),
                text = stringResource(R.string.detail_visibleText)
            )
            Headline(title = stringResource(R.string.detail_iconsTitle))
            IconSelection(
                selected = if (viewModel.icon != null) { viewModel.icon!! } else { viewModel.type.defaultIcon },
                onSelectedChange = {
                    viewModel.icon = it
                }
            )
            Button(
                onClick = {
                    viewModel.save()
                    onNavigateUp()
                },
                enabled = viewModel.isDataValid.value,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(
                        horizontal = dimensionResource(R.dimen.margin_horizontal),
                        vertical = dimensionResource(R.dimen.padding_vertical)
                    )
            ) {
                Text(stringResource(R.string.button_save))
            }
        }
    }
}


/**
 * Displays (multiple) rows of icon buttons from which the user can select one icon for the detail.
 *
 * @param selected          Icon selected currently.
 * @param onSelectedChange  Callback invoked once the selection changes.
 */
@Composable
private fun IconSelection(
    selected: DetailIcon,
    onSelectedChange: (DetailIcon) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = dimensionResource(R.dimen.margin_horizontal),
                end = dimensionResource(R.dimen.margin_horizontal),
                bottom = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        DetailIcon.entries.forEach { typeIcon ->
            IconToggleButton(
                checked = typeIcon == selected,
                onCheckedChange = {
                    if (it) {
                        onSelectedChange(typeIcon)
                    }
                },
                colors = IconButtonDefaults.iconToggleButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    checkedContainerColor = MaterialTheme.colorScheme.primary,
                    checkedContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .padding(4.dp)
                    .size(56.dp)
            ) {
                Icon(
                    painter = painterResource(typeIcon.drawableResourceId),
                    contentDescription = "",
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}
