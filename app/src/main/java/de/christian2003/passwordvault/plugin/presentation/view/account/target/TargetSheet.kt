package de.christian2003.passwordvault.plugin.presentation.view.account.target

import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import de.christian2003.passwordvault.R
import de.christian2003.passwordvault.domain.model.target.Target
import de.christian2003.passwordvault.plugin.presentation.ui.composables.Checkbox
import de.christian2003.passwordvault.plugin.presentation.ui.composables.EmptyPlaceholder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun TargetSheet(
    viewModel: TargetViewModel,
    onDismiss: () -> Unit,
    onSave: (List<Target>) -> Unit
) {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        sheetGesturesEnabled = false
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.target_title))
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                sheetState.hide()
                            }.invokeOnCompletion {
                                onDismiss()
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_cancel),
                            contentDescription = ""
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                sheetState.hide()
                            }.invokeOnCompletion {
                                onSave(viewModel.targets)
                            }
                        }
                    ) {
                        Text(stringResource(R.string.button_ok))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
                    .copy(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            )
            HorizontalDivider()

            TargetsList(
                targets = viewModel.targets,
                onRemoveTarget = { target ->
                    viewModel.targets.remove(target)
                },
                onQueryLocalizedPackageName = { packageName ->
                    viewModel.getLocalizedPackageName(packageName)
                },
                onQueryPackageIcon = { packageName ->
                    viewModel.getPackageIcon(packageName)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            HorizontalDivider()
            TextButton(
                onClick = {
                    viewModel.isSelectPackageDialogVisible = true
                },
                modifier = Modifier
                    .padding(
                        horizontal = dimensionResource(R.dimen.margin_horizontal),
                        vertical = dimensionResource(R.dimen.padding_vertical)
                    )
                    .align(Alignment.End)
            ) {
                Text(stringResource(R.string.button_selectTargetPackage))
            }
        }
    }

    if (viewModel.isSelectPackageDialogVisible) {
        if (viewModel.allInstalledPackages == null) {
            viewModel.loadAllInstalledPackages()
        }
        SelectPackageDialog(
            packageNames = viewModel.allInstalledPackages,
            selectedPackages = viewModel.getAllSelectedPackages(),
            getLocalizedPackageName = { packageName ->
                viewModel.getLocalizedPackageName(packageName)
            },
            getPackageIcon = { packageName ->
                viewModel.getPackageIcon(packageName)
            },
            onDismiss = {
                viewModel.dismissSelectPackageDialog()
            },
            onSave = { selectedPackages ->
                viewModel.dismissSelectPackageDialog(selectedPackages)
            }
        )
    }
}


@Composable
private fun TargetsList(
    targets: List<Target>,
    onRemoveTarget: (Target) -> Unit,
    onQueryLocalizedPackageName: (String) -> String?,
    onQueryPackageIcon: (String) -> Drawable?,
    modifier: Modifier = Modifier
) {
    if (targets.isEmpty()) {
        EmptyPlaceholder(
            title = stringResource(R.string.target_emptyPlaceholder_title),
            subtitle = stringResource(R.string.target_emptyPlaceholder_subtitle),
            painter = painterResource(R.drawable.el_targets),
            modifier = modifier
                .verticalScroll(rememberScrollState())
        )
    }
    else {
        LazyColumn(
            modifier = modifier
        ) {
            items(targets) { target ->
                if (target.isAndroidApp()) {
                    TargetsListRowPackage(
                        target = target,
                        onRemove = onRemoveTarget,
                        onQueryLocalizedPackageName = onQueryLocalizedPackageName,
                        onQueryPackageIcon = onQueryPackageIcon
                    )
                }
                else {
                    TargetsListRowWebsite(
                        target = target,
                        onRemove = onRemoveTarget
                    )
                }
            }
        }
    }
}


@Composable
private fun TargetsListRowPackage(
    target: Target,
    onRemove: (Target) -> Unit,
    onQueryLocalizedPackageName: (String) -> String?,
    onQueryPackageIcon: (String) -> Drawable?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.margin_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        Image(
            painter = rememberDrawablePainter(onQueryPackageIcon(target.name)),
            contentDescription = "",
            modifier = Modifier.size(dimensionResource(R.dimen.image_m))
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = dimensionResource(R.dimen.padding_horizontal))
        ) {
            Text(
                text = onQueryLocalizedPackageName(target.name) ?: "",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = target.url.toString(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
        IconButton(
            onClick = {
                onRemove(target)
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_cancel),
                contentDescription = ""
            )
        }
    }
}


@Composable
private fun TargetsListRowWebsite(
    target: Target,
    onRemove: (Target) -> Unit
) {
    //Once websites are supported targets:
    //Fill this composable with the code to display a website target
}


@Composable
private fun SelectPackageDialog(
    packageNames: List<String>?,
    selectedPackages: Set<String>,
    getLocalizedPackageName: (String) -> String?,
    getPackageIcon: (String) -> Drawable?,
    onDismiss: () -> Unit,
    onSave: (Set<String>) -> Unit
) {
    val mutableSelectedPackages: MutableSet<String> = remember { mutableStateSetOf() }
    mutableSelectedPackages.addAll(selectedPackages)

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 24.dp,
                        bottom = 24.dp
                    )
            ) {
                Text(
                    text = stringResource(R.string.target_packages_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(
                        start = 24.dp,
                        end = 24.dp,
                        bottom = 16.dp
                    )
                )
                HorizontalDivider()
                if (packageNames == null) {
                    LoadingIndicator(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(24.dp)
                    )
                }
                else {
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        items(packageNames) { packageName ->
                            val isSelected: Boolean = mutableSelectedPackages.contains(packageName)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isSelected) {
                                            mutableSelectedPackages.remove(packageName)
                                        }
                                        else {
                                            mutableSelectedPackages.add(packageName)
                                        }
                                    }
                                    .padding(
                                        start = 24.dp,
                                        top = dimensionResource(R.dimen.padding_vertical),
                                        end = 12.dp, //24 - 12 = 12
                                        bottom = dimensionResource(R.dimen.padding_vertical)
                                    )
                            ) {
                                Image(
                                    painter = rememberDrawablePainter(getPackageIcon(packageName)),
                                    contentDescription = "",
                                    modifier = Modifier.size(dimensionResource(R.dimen.image_s))
                                )
                                Text(
                                    text = getLocalizedPackageName(packageName) ?: packageName,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyLarge,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .padding(start = dimensionResource(R.dimen.padding_horizontal))
                                        .weight(1f)
                                )
                                androidx.compose.material3.Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = {
                                        if (isSelected) {
                                            mutableSelectedPackages.remove(packageName)
                                        }
                                        else {
                                            mutableSelectedPackages.add(packageName)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                HorizontalDivider()
                FlowRow(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(
                            start = 24.dp,
                            top = 16.dp,
                            end = 24.dp
                        )
                ) {
                    TextButton(
                        onClick = {
                            onDismiss()
                        }
                    ) {
                        Text(stringResource(R.string.button_cancel))
                    }
                    TextButton(
                        onClick = {
                            onSave(mutableSelectedPackages)
                        },
                        modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_horizontal))
                    ) {
                        Text(stringResource(R.string.button_ok))
                    }
                }
            }
        }
    }
}
