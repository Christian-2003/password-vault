package de.christian2003.accounts.view.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import de.christian2003.accounts.R
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.sp
import de.christian2003.accounts.model.Detail
import de.christian2003.core.ui.composables.BottomSheetDialog
import de.christian2003.core.ui.composables.DeleteDialog
import de.christian2003.core.ui.composables.TextInput
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountView(
    viewModel: AccountViewModel,
    onNavigateUp: () -> Unit,
    onAddDetail: (UUID) -> Unit,
    onEditDetail: (UUID, UUID) -> Unit
) {
    val details by viewModel.details.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = viewModel.name.ifEmpty { stringResource(R.string.account_name_label) },
                        color = if (viewModel.name.isNotEmpty()) { MaterialTheme.colorScheme.onSurface } else { MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) },
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .clickable {
                                viewModel.isEditNameSheetVisible = !viewModel.isEditNameSheetVisible
                            }
                            .padding(
                                vertical = dimensionResource(de.christian2003.core.R.dimen.padding_vertical_small),
                                horizontal = dimensionResource(de.christian2003.core.R.dimen.padding_horizontal_small)
                            )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.save()
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
                actions = {
                    IconButton(
                        onClick = {
                            onAddDetail(viewModel.accountId)
                        }
                    ) {
                        Icon(
                            painter = painterResource(de.christian2003.core.R.drawable.ic_add),
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
        ) {
            GeneralSection(
                name = viewModel.name,
                description = viewModel.description,
                isCreatingNewAccount = viewModel.isCreatingNewAccount,
                onEditDescription = {
                    viewModel.isEditDescriptionSheetVisible = !viewModel.isEditDescriptionSheetVisible
                },
                onDeleteClicked = {
                    viewModel.isDeleteSheetVisible = !viewModel.isDeleteSheetVisible
                }
            )
            LazyColumn {
                items(details) { detail ->
                    DetailsListItem(
                        detail = detail,
                        onEditDetail = {
                            onEditDetail(detail.id, viewModel.accountId)
                        }
                    )
                }
            }
        }
        if (viewModel.isEditNameSheetVisible) {
            ContentEditor(
                value = viewModel.name,
                onValueChange = {
                    viewModel.name = it
                },
                title = stringResource(R.string.account_name_title),
                label = stringResource(R.string.account_name_label),
                info = stringResource(R.string.account_name_info),
                accountAbbreviation = if (viewModel.name.isNotEmpty()) { "" + viewModel.name[0] } else { "?" },
                onDismiss = {
                    viewModel.isEditNameSheetVisible = false
                },
                errorMessage = if (viewModel.name.isEmpty()) { stringResource(R.string.account_name_error) } else { null }
            )
        }
        if (viewModel.isEditDescriptionSheetVisible) {
            ContentEditor(
                value = viewModel.description,
                onValueChange = {
                    viewModel.description = it
                },
                title = stringResource(R.string.account_description_title),
                label = stringResource(R.string.account_description_label),
                info = stringResource(R.string.account_description_info),
                accountAbbreviation = if (viewModel.name.isNotEmpty()) { "" + viewModel.name[0] } else { "?" },
                onDismiss = {
                    viewModel.isEditDescriptionSheetVisible = false
                }
            )
        }
        if (viewModel.isDeleteSheetVisible) {
            DeleteDialog(
                message = stringResource(R.string.account_deleteAccount_message),
                onCancel = {
                    viewModel.isDeleteSheetVisible = false
                },
                onConfirm = {
                    viewModel.isDeleteSheetVisible = false
                    viewModel.delete()
                    onNavigateUp()
                },
                title = stringResource(R.string.account_deleteAccount_title)
            )
        }
    }
}


/**
 * Composable displays the general section containing information on the account.
 *
 * @param name                  Name of the account.
 * @param description           Description of the account.
 * @param isCreatingNewAccount  Flag indicates whether the view is displayed to create a new account.
 * @param onEditDescription     Callback invoked to edit the description.
 * @param onDeleteClicked       Callback invoked to delete the account.
 */
@Composable
fun GeneralSection(
    name: String,
    description: String,
    isCreatingNewAccount: Boolean,
    onEditDescription: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                end = dimensionResource(de.christian2003.core.R.dimen.padding_horizontal_activity)
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(
                    start = dimensionResource(de.christian2003.core.R.dimen.padding_horizontal_activity),
                    top = dimensionResource(de.christian2003.core.R.dimen.padding_vertical_activity),
                    bottom = dimensionResource(de.christian2003.core.R.dimen.padding_vertical)
                )
                .size(dimensionResource(de.christian2003.core.R.dimen.image_large))
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Text(
                text = if (name.isNotEmpty()) { "" + name[0] } else { "?" },
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp
            )
        }
        Column {
            Text(
                text = description.ifEmpty { stringResource(R.string.account_description_label) },
                color = if (description.isNotEmpty()) { MaterialTheme.colorScheme.onSurface } else { MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) },
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(
                        top = dimensionResource(de.christian2003.core.R.dimen.padding_vertical_activity) - dimensionResource(de.christian2003.core.R.dimen.padding_vertical_small),
                        start = dimensionResource(de.christian2003.core.R.dimen.padding_horizontal) - dimensionResource(de.christian2003.core.R.dimen.padding_horizontal_small)
                    )
                    .clip(MaterialTheme.shapes.small)
                    .clickable {
                        onEditDescription()
                    }
                    .padding(
                        vertical = dimensionResource(de.christian2003.core.R.dimen.padding_vertical_small),
                        horizontal = dimensionResource(de.christian2003.core.R.dimen.padding_horizontal_small)
                    )
            )
            if (!isCreatingNewAccount) {
                Button(
                    modifier = Modifier
                        .padding(
                            start = dimensionResource(de.christian2003.core.R.dimen.padding_horizontal),
                            top = dimensionResource(de.christian2003.core.R.dimen.padding_vertical_between),
                            end = dimensionResource(de.christian2003.core.R.dimen.padding_horizontal),
                            bottom = dimensionResource(de.christian2003.core.R.dimen.padding_vertical)
                        ),
                    onClick = onDeleteClicked,
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text(stringResource(R.string.account_button_delete))
                }
            }
        }
    }
    HorizontalDivider()
}


/**
 * Composable displays a modal bottom sheet dialog through which the user can edit content (e.g. a
 * name or description).
 *
 * @param value                 Value to edit.
 * @param onValueChange         Callback invoked once the value changes.
 * @param title                 Title for the dialog.
 * @param label                 Label for the text input.
 * @param info                  Info text to display.
 * @param accountAbbreviation   Abbreviation of the account name to display.
 * @param onDismiss             Callback invoked once the dialog is dismissed.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ContentEditor(
    value: String,
    onValueChange: (String) -> Unit,
    title: String,
    label: String,
    info: String,
    accountAbbreviation: String,
    onDismiss: () -> Unit,
    errorMessage: String? = null
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    BottomSheetDialog(
        sheetState = sheetState,
        title = title,
        iconComposable = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(dimensionResource(de.christian2003.core.R.dimen.image_small))
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Text(
                    text = accountAbbreviation,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(de.christian2003.core.R.dimen.padding_horizontal_activity))
        ) {
            Text(
                text = info,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = dimensionResource(de.christian2003.core.R.dimen.padding_vertical))
            )
            TextInput(
                value = value,
                onValueChange = onValueChange,
                label = label,
                errorMessage = errorMessage,
            )
            FlowRow(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(
                        top = dimensionResource(de.christian2003.core.R.dimen.padding_vertical),
                        bottom = dimensionResource(de.christian2003.core.R.dimen.padding_vertical_activity)
                    ),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            onDismiss()
                        }
                    },
                    enabled = value.isNotEmpty()
                ) {
                    Text(text = stringResource(R.string.account_button_ok))
                }
            }
        }
    }
}


@Composable
fun DetailsListItem(
    detail: Detail,
    onEditDetail: (Detail) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onEditDetail(detail)
            }
            .padding(
                horizontal = dimensionResource(de.christian2003.core.R.dimen.padding_horizontal_activity),
                vertical = dimensionResource(de.christian2003.core.R.dimen.padding_horizontal_between)
            )
    ) {
        Text(detail.name)
        Text(detail.content)
    }
}
