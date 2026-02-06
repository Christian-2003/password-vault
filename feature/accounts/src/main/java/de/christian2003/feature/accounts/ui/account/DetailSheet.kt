package de.christian2003.feature.accounts.ui.account

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import de.christian2003.core.ui.composables.Checkbox
import de.christian2003.core.ui.composables.Headline
import de.christian2003.core.ui.composables.HelpCard
import de.christian2003.core.ui.composables.TextInput
import de.christian2003.core.ui.composables.Tooltip
import de.christian2003.core.ui.composables.dialog.ConfirmDiscardDialog
import de.christian2003.data.accounts.domain.entities.Detail
import de.christian2003.data.accounts.domain.entities.DetailIcon
import de.christian2003.feature.accounts.viewmodels.DetailViewModel
import de.christian2003.feature.accounts.R
import de.christian2003.feature.accounts.models.other.DetailIconDrawable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


/**
 * Sheet displays a detail that can be edited (or created) by the user.
 *
 * @param viewModel View model for the sheet.
 * @param onDismiss Callback invoked to dismiss the sheet without saving.
 * @param onSave    Callback invoked to dismiss the sheet and save the detail passed.
 */
@Composable
fun DetailSheet(
    viewModel: DetailViewModel,
    onDismiss: () -> Unit,
    onSave: (Detail) -> Unit
) {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val nameFocusRequester: FocusRequester = remember { FocusRequester() }
    val contentFocusRequester: FocusRequester = remember { FocusRequester() }

    val invokeOnDismiss: () -> Unit = {
        if (viewModel.areChangesMade()) {
            viewModel.isDiscardDialogVisible = true
        }
        else {
            coroutineScope.launch {
                sheetState.hide()
            }.invokeOnCompletion {
                onDismiss()
            }
        }
    }

    if (viewModel.isCreatingNewDetail) {
        LaunchedEffect(Unit) {
            //Safe call required: When rotating the screen, the focus requester is not instantiated
            //for a very short period of time, during which this Launched effect is called. Without
            //this safe call, the app would crash throwing an IllegalStateException.
            nameFocusRequester?.requestFocus()
        }
    }

    ModalBottomSheet(
        onDismissRequest = invokeOnDismiss,
        sheetState = sheetState,
        dragHandle = null,
        sheetGesturesEnabled = false,
        properties = ModalBottomSheetProperties(
            shouldDismissOnBackPress = false
        )
    ) {
        BackHandler {
            invokeOnDismiss()
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(if (viewModel.isCreatingNewDetail) { stringResource(R.string.detail_titleCreate) } else { stringResource(R.string.detail_titleEdit) })
                },
                navigationIcon = {
                    Tooltip(
                        tooltip = stringResource(de.christian2003.core.ui.R.string.tooltip_closeWithoutSaving),
                        anchor = TooltipAnchorPosition.End
                    ) {
                        IconButton(
                            onClick = invokeOnDismiss
                        ) {
                            Icon(
                                painter = painterResource(de.christian2003.core.ui.R.drawable.ic_cancel),
                                contentDescription = ""
                            )
                        }
                    }
                },
                actions = {
                    TextButton(
                        enabled = viewModel.isDataValid.value,
                        onClick = {
                            val detail: Detail? = viewModel.createDetailToSave()
                            if (detail != null) {
                                coroutineScope.launch {
                                    sheetState.hide()
                                }.invokeOnCompletion {
                                    onSave(detail)
                                }
                            }
                        }
                    ) {
                        Text(stringResource(de.christian2003.core.ui.R.string.button_ok))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            )

            HorizontalDivider()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                AnimatedVisibility(viewModel.isHelpCardVisible) {
                    HelpCard(
                        text = stringResource(R.string.detail_help),
                        onDismiss = {
                            viewModel.dismissHelpCard()
                        },
                        modifier = Modifier.padding(
                            horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                            vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                        )
                    )
                }

                TextInput(
                    value = viewModel.name,
                    onValueChange = {
                        viewModel.name = it
                    },
                    label = stringResource(R.string.detail_nameLabel),
                    prefixIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_text),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            contentFocusRequester.requestFocus()
                        }
                    ),
                    focusRequester = nameFocusRequester,
                    modifier = Modifier
                        .padding(horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal))
                )
                TextInput(
                    value = viewModel.content,
                    onValueChange = {
                        viewModel.content = it
                    },
                    label = stringResource(R.string.detail_contentLabel),
                    prefixIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_content),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            this.defaultKeyboardAction(ImeAction.Done) //Hides the keyboard
                        }
                    ),
                    focusRequester = contentFocusRequester,
                    modifier = Modifier
                        .padding(
                            horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                            vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
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
            }
        }
    }

    if (viewModel.isDiscardDialogVisible) {
        ConfirmDiscardDialog(
            text = stringResource(R.string.detail_discardChanges),
            onDismiss = {
                viewModel.isDiscardDialogVisible = false
            },
            onConfirm = {
                coroutineScope.launch {
                    viewModel.isDiscardDialogVisible = false
                    sheetState.hide()
                }.invokeOnCompletion {
                    onDismiss()
                }
            }
        )
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
                start = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                end = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
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
                    painter = painterResource(DetailIconDrawable.getDrawableForDetailIcon(typeIcon).drawableRes),
                    contentDescription = "",
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}
