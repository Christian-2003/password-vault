package de.christian2003.feature.accounts.ui.account

import android.graphics.drawable.Drawable
import android.webkit.URLUtil
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import de.christian2003.core.ui.composables.EmptyPlaceholder
import de.christian2003.core.ui.composables.HelpCard
import de.christian2003.core.ui.composables.ListItemContainer
import de.christian2003.core.ui.composables.Shape
import de.christian2003.core.ui.composables.Tooltip
import de.christian2003.core.ui.composables.dialog.ConfirmDeleteDialog
import de.christian2003.core.ui.composables.dialog.ConfirmDiscardDialog
import de.christian2003.core.ui.composables.dialog.DialogWithHeroSection
import de.christian2003.core.ui.composables.dialog.EditValueDialog
import de.christian2003.core.ui.theme.isDarkTheme
import de.christian2003.feature.accounts.viewmodels.TargetViewModel
import de.christian2003.data.accounts.domain.entities.Target
import de.christian2003.feature.accounts.R
import de.christian2003.feature.accounts.models.dialogs.TargetSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.security.cert.X509Certificate


/**
 * Displays the sheet through which to edit the autofill targets for an account.
 *
 * @param viewModel View model for the sheet.
 * @param onDismiss Callback invoked to dismiss the sheet without saving anything.
 * @param onSave    Callback invoked to dismiss the sheet and save a list of targets.
 */
@Composable
internal fun TargetSheet(
    viewModel: TargetViewModel,
    onDismiss: () -> Unit,
    onSave: (List<Target>) -> Unit
) {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val invokeOnDismiss: () -> Unit = {
        if (viewModel.areChangesMade()) {
            viewModel.showDiscardChangesDialog()
        }
        else {
            coroutineScope.launch {
                sheetState.hide()
            }.invokeOnCompletion {
                onDismiss()
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        sheetGesturesEnabled = false,
        properties = ModalBottomSheetProperties(
            shouldDismissOnBackPress = false
        ),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        BackHandler {
            invokeOnDismiss()
        }

        Scaffold(
            topBar = {
                TopBar(
                    onDismiss = invokeOnDismiss,
                    onSave = {
                        coroutineScope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            onSave(viewModel.targets)
                        }
                    }
                )
            },
            bottomBar = {
                BottomBar(
                    onSelectPackagesClick = {
                        viewModel.showSelectPackageDialog()
                    },
                    onSelectWebsiteClick = {
                        viewModel.showSelectWebsiteDialog()
                    }
                )
            }
        ) { innerPadding ->
            TargetsList(
                targets = viewModel.targets,
                isHelpCardVisible = viewModel.isHelpCardVisible,
                onRemoveTarget = { target ->
                    viewModel.targetToRemove = target
                },
                onShowCertificateHelp = {
                    viewModel.showCertificatesDoNotMatchDialog()
                },
                onShowCertificateDetails = { target ->
                    viewModel.showCertificateDetailsDialog(target)
                },
                onQueryLocalizedPackageName = { packageName ->
                    viewModel.getLocalizedPackageName(packageName)
                },
                onQueryPackageIcon = { packageName ->
                    viewModel.getPackageIcon(packageName)
                },
                onDismissHelpCard = {
                    viewModel.dismissHelpCard()
                },
                onQueryIsTargetValid = { target ->
                    viewModel.isTargetValid(target)
                },
                onGeneratePositiveColor = { negativeColor, darkTheme ->
                    viewModel.generatePositiveColor(negativeColor, darkTheme)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }

    val targetToRemove: Target? = viewModel.targetToRemove
    if (targetToRemove != null) {
        ConfirmDeleteDialog(
            title = stringResource(R.string.target_remove_title),
            text = if (targetToRemove.isAndroidApp()) {
                stringResource(R.string.target_remove_textPackage, viewModel.getLocalizedPackageName(targetToRemove.name) ?: targetToRemove.name)
            } else {
                stringResource(R.string.target_remove_textWebsite, targetToRemove.name)
            },
            confirmButtonText = stringResource(de.christian2003.core.ui.R.string.button_remove),
            onDismiss = {
                viewModel.dismissRemoveTargetDialog()
            },
            onConfirm = {
                viewModel.dismissRemoveTargetDialog(targetToRemove)
            }
        )
    }

    when (viewModel.dialog) {
        TargetSheetDialog.CertificatesDoNotMatch -> {
            DialogWithHeroSection(
                title = stringResource(R.string.target_packages_certDialog_title),
                text = AnnotatedString.fromHtml(stringResource(R.string.target_packages_certDialog_text)),
                dismissButtonText = stringResource(de.christian2003.core.ui.R.string.button_ok),
                onDismiss = {
                    viewModel.dismissCertificatesDoNotMatchDialog()
                }
            ) {
                Image(
                    painter = painterResource(R.drawable.error_certificate),
                    contentDescription = "",
                    modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_xxl))
                )
            }
        }
        TargetSheetDialog.CertificateDetails -> {
            val certificateToDisplay: X509Certificate? = viewModel.certificateToDisplay
            if (certificateToDisplay != null) {
                CertificateDetailsDialog(
                    certificate = certificateToDisplay,
                    onFormatDate = { date ->
                        viewModel.formatDate(date)
                    },
                    onGeneratePositiveColor = { negativeColor, darkTheme ->
                        viewModel.generatePositiveColor(negativeColor, darkTheme)
                    },
                    onDismiss = {
                        viewModel.dismissCertificateDetailsDialog()
                    }
                )
            }
        }
        TargetSheetDialog.SelectPackage -> {
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
        TargetSheetDialog.SelectWebsite -> {
            val errorBlankInput: String = stringResource(de.christian2003.core.ui.R.string.error_blankInput)
            val errorInvalidUrl: String = stringResource(de.christian2003.core.ui.R.string.error_invalidUrl)
            EditValueDialog(
                value = "https://",
                onValidateValue = { value ->
                    if (value.isBlank()) {
                        errorBlankInput
                    }
                    else if (!(URLUtil.isValidUrl(value) && !value.toUri().host.isNullOrEmpty())) {
                        errorInvalidUrl
                    }
                    else {
                        null
                    }
                },
                title = stringResource(R.string.target_website_title),
                label = stringResource(R.string.target_website_label),
                onDismiss = {
                    viewModel.dismissSelectWebsiteDialog()
                },
                onSave = { url ->
                    viewModel.dismissSelectWebsiteDialog(url)
                }
            )
        }
        TargetSheetDialog.DiscardChanges -> {
            ConfirmDiscardDialog(
                text = stringResource(R.string.target_discardChanges),
                onDismiss = {
                    viewModel.dismissDiscardChangesDialog()
                },
                onConfirm = {
                    coroutineScope.launch {
                        viewModel.dismissDiscardChangesDialog()
                        sheetState.hide()
                    }.invokeOnCompletion {
                        onDismiss()
                    }
                }
            )
        }
        else -> { }
    }
}


/**
 * Displays a list of all targets.
 *
 * @param targets                       List of targets to display.
 * @param isHelpCardVisible             Whether the help card is visible.
 * @param onRemoveTarget                Callback invoked to remove a target.
 * @param onShowCertificateHelp         Callback invoked to show help about certificate issues.
 * @param onShowCertificateDetails      Callback invoked to show the details about the signing
 *                                      certificate of the target.
 * @param onQueryLocalizedPackageName   Callback invoked to query the localized name for an
 *                                      installed package
 * @param onQueryPackageIcon            Callback invoked to query the icon for an installed package.
 * @param onDismissHelpCard             Callback invoked to dismiss the help card.
 * @param onQueryIsTargetValid          Callback invoked to query whether a target is valid.
 * @param onGeneratePositiveColor       Callback invoked to generate a positive color.
 * @param modifier                      Modifier.
 */
@Composable
private fun TargetsList(
    targets: List<Target>,
    isHelpCardVisible: Boolean,
    onRemoveTarget: (Target) -> Unit,
    onShowCertificateHelp: () -> Unit,
    onShowCertificateDetails: (Target) -> Unit,
    onQueryLocalizedPackageName: (String) -> String?,
    onQueryPackageIcon: (String) -> Drawable?,
    onDismissHelpCard: () -> Unit,
    onQueryIsTargetValid: (Target) -> Boolean,
    onGeneratePositiveColor: (Color, Boolean) -> Color,
    modifier: Modifier = Modifier
) {
    if (targets.isEmpty()) {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
        ) {
            AnimatedVisibility(isHelpCardVisible) {
                HelpCard(
                    text = stringResource(R.string.target_help),
                    onDismiss = onDismissHelpCard,
                    modifier = Modifier.padding(
                        start = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                        end = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                        bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                    )
                )
            }
            val modifier: Modifier = if (isHelpCardVisible) { Modifier } else { Modifier.weight(1f) }
            EmptyPlaceholder(
                title = stringResource(R.string.target_emptyPlaceholder_title),
                subtitle = stringResource(R.string.target_emptyPlaceholder_subtitle),
                painter = painterResource(R.drawable.el_targets),
                modifier = modifier
            )
        }
    }
    else {
        LazyColumn(
            modifier = modifier
        ) {
            item {
                AnimatedVisibility(isHelpCardVisible) {
                    HelpCard(
                        text = stringResource(R.string.target_help),
                        onDismiss = onDismissHelpCard,
                        modifier = Modifier.padding(
                            start = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                            end = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                            bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                        )
                    )
                }
            }
            itemsIndexed(targets) { index, target ->
                if (target.isAndroidApp()) {
                    TargetsListRowPackage(
                        target = target,
                        isFirst = index == 0,
                        isLast = index == targets.size - 1,
                        onRemove = onRemoveTarget,
                        onShowCertificateHelp = onShowCertificateHelp,
                        onShowCertificateDetails = onShowCertificateDetails,
                        onQueryLocalizedPackageName = onQueryLocalizedPackageName,
                        onQueryPackageIcon = onQueryPackageIcon,
                        onQueryIsTargetValid = onQueryIsTargetValid,
                        onGeneratePositiveColor = onGeneratePositiveColor
                    )
                }
                else {
                    TargetsListRowWebsite(
                        target = target,
                        isFirst = index == 0,
                        isLast = index == targets.size - 1,
                        onRemove = onRemoveTarget
                    )
                }
            }
        }
    }
}


/**
 * List row for the targets list to display an Android app target.
 *
 * @param target                        Android app target to display.
 * @param isFirst                       Whether the android app is the first in the list.
 * @param isLast                        Whether the android app is the last in the list.
 * @param onRemove                      Callback invoked to remove the target.
 * @param onShowCertificateHelp         Callback invoked to show help about certificate issues.
 * @param onShowCertificateDetails      Callback invoked to show the certificate details of the
 *                                      package.
 * @param onQueryLocalizedPackageName   Callback invoked to query the localized name for an
 *                                      installed package
 * @param onQueryPackageIcon            Callback invoked to query the icon for an installed package.
 * @param onQueryIsTargetValid          Callback invoked to query whether a target is valid.
 * @param onGeneratePositiveColor       Callback invoked to generate a positive color.
 */
@Composable
private fun TargetsListRowPackage(
    target: Target,
    isFirst: Boolean,
    isLast: Boolean,
    onRemove: (Target) -> Unit,
    onShowCertificateHelp: () -> Unit,
    onShowCertificateDetails: (Target) -> Unit,
    onQueryLocalizedPackageName: (String) -> String?,
    onQueryPackageIcon: (String) -> Drawable?,
    onQueryIsTargetValid: (Target) -> Boolean,
    onGeneratePositiveColor: (Color, Boolean) -> Color
) {
    val localizedPackageName: String = onQueryLocalizedPackageName(target.name) ?: ""
    val isValid: Boolean = onQueryIsTargetValid(target)
    val packageIcon: Drawable? = onQueryPackageIcon(target.name)
    val positiveColor: Color = onGeneratePositiveColor(MaterialTheme.colorScheme.error, MaterialTheme.isDarkTheme())

    ListItemContainer(
        isFirst = isFirst,
        isLast = isLast
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal),
                    top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical),
                    end = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal) - 12.dp,
                    bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                )
        ) {
            if (packageIcon == null) {
                Shape(
                    shape = MaterialShapes.Clover8Leaf,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_m))
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_packages),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_xs))
                    )
                }
            }
            else  {
                Image(
                    painter = rememberDrawablePainter(packageIcon),
                    contentDescription = "",
                    modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_m))
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal) - 4.dp)
            ) {
                Text(
                    text = localizedPackageName.ifBlank { target.name },
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 4.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .clickable {
                            if (!isValid) {
                                onShowCertificateHelp()
                            }
                            else {
                                onShowCertificateDetails(target)
                            }
                        }
                        .padding(horizontal = 4.dp)
                ) {
                    Icon(
                        painter = if (!isValid) {
                            painterResource(de.christian2003.core.ui.R.drawable.ic_error)
                        } else {
                            painterResource(de.christian2003.core.ui.R.drawable.ic_verified)
                        },
                        contentDescription = "",
                        tint = if (!isValid) {
                            MaterialTheme.colorScheme.error
                        } else {
                            positiveColor
                        },
                        modifier = Modifier
                            .padding(end = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal) / 2)
                            .size(dimensionResource(de.christian2003.core.ui.R.dimen.image_xxs))
                    )
                    Text(
                        text = if (!isValid) {
                            stringResource(R.string.target_packages_signingCertsNotMatching)
                        } else {
                            stringResource(R.string.target_packages_signingCertsValid)
                        },
                        color = if (!isValid) {
                            MaterialTheme.colorScheme.error
                        } else {
                            positiveColor
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Tooltip(
                tooltip = stringResource(de.christian2003.core.ui.R.string.tooltip_removeTargetAndroidApp, localizedPackageName),
                anchor = TooltipAnchorPosition.Start
            ) {
                IconButton(
                    onClick = {
                        onRemove(target)
                    }
                ) {
                    Icon(
                        painter = painterResource(de.christian2003.core.ui.R.drawable.ic_cancel),
                        contentDescription = ""
                    )
                }
            }
        }
    }
}


/**
 * List row for the targets list to display a website target.
 *
 * @param target    Website target to display.
 * @param isFirst   Whether the website is the first in the list.
 * @param isLast    Whether the website is the last in the list.
 * @param onRemove  Callback invoked to remove the target.
 */
@Composable
private fun TargetsListRowWebsite(
    target: Target,
    isFirst: Boolean,
    isLast: Boolean,
    onRemove: (Target) -> Unit
) {
    ListItemContainer(
        isFirst = isFirst,
        isLast = isLast
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal),
                    top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical),
                    end = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal) - 12.dp,
                    bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                )
        ) {
            Shape(
                shape = MaterialShapes.Clover8Leaf,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_m))
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_website),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_xs))
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
            ) {
                Text(
                    text = target.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = target.url.toString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Tooltip(
                tooltip = stringResource(de.christian2003.core.ui.R.string.tooltip_removeTargetWebsite, target.name),
                anchor = TooltipAnchorPosition.Start
            ) {
                IconButton(
                    onClick = {
                        onRemove(target)
                    }
                ) {
                    Icon(
                        painter = painterResource(de.christian2003.core.ui.R.drawable.ic_cancel),
                        contentDescription = ""
                    )
                }
            }
        }
    }
}


/**
 * Dialog through which to select installed Android packages.
 *
 * @param packageNames              List of the package names for all installed Android packages.
 * @param selectedPackages          Set of the package names of all selected packages.
 * @param getLocalizedPackageName   Callback invoked to query a localized name for an installed package.
 * @param getPackageIcon            Callback invoked to query the icon for an installed package.
 * @param onDismiss                 Callback invoked to dismiss the dialog.
 * @param onSave                    Callback invoked to save a set of installed packages.
 */
@Composable
private fun SelectPackageDialog(
    packageNames: List<String>?,
    selectedPackages: Set<String>,
    getLocalizedPackageName: (String) -> String?,
    getPackageIcon: (String) -> Drawable?,
    onDismiss: () -> Unit,
    onSave: (Set<String>) -> Unit
) {
    val mutableSetSaver = Saver<MutableSet<String>, List<String>>(
        save = { set -> set.toList() },
        restore = { list -> list.toMutableSet() }
    )
    val mutableSelectedPackages: MutableSet<String> = rememberSaveable(saver = mutableSetSaver) { mutableStateSetOf() }
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
                    CircularProgressIndicator(
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
                                        top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical),
                                        end = 12.dp, //24 - 12 = 12
                                        bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                                    )
                            ) {
                                Image(
                                    painter = rememberDrawablePainter(getPackageIcon(packageName)),
                                    contentDescription = "",
                                    modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_s))
                                )
                                Text(
                                    text = getLocalizedPackageName(packageName) ?: packageName,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyLarge,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .padding(start = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
                                        .weight(1f)
                                )
                                Checkbox(
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
                        Text(stringResource(de.christian2003.core.ui.R.string.button_cancel))
                    }
                    TextButton(
                        onClick = {
                            onSave(mutableSelectedPackages)
                        },
                        modifier = Modifier.padding(start = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
                    ) {
                        Text(stringResource(de.christian2003.core.ui.R.string.button_ok))
                    }
                }
            }
        }
    }
}


/**
 * Top bar for the sheet.
 *
 * @param onDismiss Callback invoked to dismiss the sheet without saving.
 * @param onSave    Callback invoked to dismiss the sheet and save changes.
 */
@Composable
private fun TopBar(
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    TopAppBar(
        title = {
            Text(stringResource(R.string.target_title))
        },
        navigationIcon = {
            Tooltip(
                tooltip = stringResource(de.christian2003.core.ui.R.string.tooltip_closeWithoutSaving),
                anchor = TooltipAnchorPosition.End
            ) {
                IconButton(
                    onClick = onDismiss
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
                onClick = onSave
            ) {
                Text(stringResource(de.christian2003.core.ui.R.string.button_ok))
            }
        }
    )
}


/**
 * Bottom bar for the sheet.
 *
 * @param onSelectPackagesClick Callback invoked to select an installed app.
 * @param onSelectWebsiteClick  Callback invoked to select a website.
 */
@Composable
private fun BottomBar(
    onSelectPackagesClick: () -> Unit,
    onSelectWebsiteClick: () -> Unit
) {
    BottomAppBar {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .padding(
                        //Horizontal padding of bottom app bar: 4 dp
                        horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal) - 4.dp
                    )
            ) {
                TextButton(
                    onClick = onSelectPackagesClick
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_packages),
                            contentDescription = "",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(stringResource(de.christian2003.core.ui.R.string.button_selectTargetPackage))
                    }
                }
                TextButton(
                    onClick = onSelectWebsiteClick,
                    modifier = Modifier.padding(start = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_website),
                            contentDescription = "",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(stringResource(de.christian2003.core.ui.R.string.button_selectTargetWebsite))
                    }
                }
            }
        }
    }
}
