package de.christian2003.feature.accounts.ui.account

import android.graphics.drawable.Drawable
import android.webkit.URLUtil
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import de.christian2003.core.ui.composables.ContextAction
import de.christian2003.core.ui.composables.ContextActionDivider
import de.christian2003.core.ui.composables.ContextActions
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
import de.christian2003.feature.accounts.models.states.TargetSheetState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.security.cert.X509Certificate
import kotlin.uuid.Uuid


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
        when (viewModel.state) {
            TargetSheetState.Multiselect -> viewModel.dismissMultiselectState()
            TargetSheetState.Default -> {
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
        containerColor = when (viewModel.state) {
            TargetSheetState.Default -> MaterialTheme.colorScheme.surface
            else -> MaterialTheme.colorScheme.surfaceContainer
        }
    ) {
        BackHandler {
            invokeOnDismiss()
        }

        Scaffold(
            topBar = {
                TopBar(
                    state = viewModel.state,
                    selectedTargetsCount = viewModel.selectedTargets.size,
                    onSelectAllTargets = {
                        viewModel.selectAllTargets()
                    },
                    onDeleteSelectedTargets = {
                        viewModel.showConfirmRemoveTargetDialog()
                    },
                    onDismissMultiselectState = {
                        viewModel.dismissMultiselectState()
                    },
                    onDismissDialog = invokeOnDismiss,
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
                state = viewModel.state,
                targets = viewModel.targets,
                isHelpCardVisible = viewModel.isHelpCardVisible,
                onRemoveTarget = { target ->
                    viewModel.showConfirmRemoveTargetDialog(target)
                },
                onEditTargetUrl = { target ->
                    viewModel.showEditWebsiteDialog(target)
                },
                onShowCertificateHelp = {
                    viewModel.showCertificatesDoNotMatchDialog()
                },
                onShowCertificateDetails = { target ->
                    viewModel.showCertificateDetailsDialog(target)
                },
                onSelectPackages = {
                    viewModel.showSelectPackageDialog()
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
                onQueryIsSelected = { target ->
                    viewModel.selectedTargets.contains(target.id)
                },
                onStartMultiselect = { target ->
                    viewModel.startMultiselectState(target)
                },
                onToggleSelection = { target ->
                    if (viewModel.selectedTargets.contains(target.id)) {
                        viewModel.selectedTargets.remove(target.id)
                    }
                    else {
                        viewModel.selectedTargets.add(target.id)
                    }
                },
                onGeneratePositiveColor = { negativeColor, darkTheme ->
                    viewModel.generatePositiveColor(negativeColor, darkTheme)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(innerPadding)
            )
        }
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
        TargetSheetDialog.SelectWebsite, TargetSheetDialog.EditWebsite -> {
            val errorBlankInput: String = stringResource(de.christian2003.core.ui.R.string.error_blankInput)
            val errorInvalidUrl: String = stringResource(de.christian2003.core.ui.R.string.error_invalidUrl)
            val targetToEdit: Target? = viewModel.targetForDialog
            EditValueDialog(
                value = targetToEdit?.url?.toString() ?: "https://",
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
                title = if (targetToEdit == null) {
                    stringResource(R.string.target_website_title)
                } else {
                    stringResource(R.string.target_context_website_edit)
                },
                label = stringResource(R.string.target_website_label),
                onDismiss = {
                    if (targetToEdit == null) {
                        viewModel.dismissSelectWebsiteDialog()
                    }
                    else {
                        viewModel.dismissEditWebsiteDialog()
                    }
                },
                onSave = { url ->
                    if (targetToEdit == null) {
                        viewModel.dismissSelectWebsiteDialog(url)
                    }
                    else {
                        viewModel.dismissEditWebsiteDialog(url)
                    }
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
        TargetSheetDialog.ConfirmRemoveTarget -> {
            val targetToRemove: Target? = viewModel.targetForDialog
            ConfirmDeleteDialog(
                title = if (targetToRemove != null) {
                    pluralStringResource(R.plurals.target_remove_title, 1)
                } else {
                    pluralStringResource(R.plurals.target_remove_title, viewModel.selectedTargets.size)
                },
                text = when {
                    targetToRemove != null && targetToRemove.isAndroidApp() -> {
                        stringResource(R.string.target_remove_textPackage, viewModel.getLocalizedPackageName(targetToRemove.name) ?: targetToRemove.name)
                    }
                    targetToRemove != null -> {
                        stringResource(R.string.target_remove_textWebsite, targetToRemove.name)
                    }
                    viewModel.selectedTargets.size == 1 -> {
                        val targetId: Uuid? = viewModel.selectedTargets.firstOrNull()
                        val target: Target? = viewModel.targets.firstOrNull { t -> t.id == targetId }
                        val targetName: String? = if (target != null && target.isAndroidApp()) {
                            viewModel.getLocalizedPackageName(target.name)
                        } else {
                            target?.name
                        }
                        pluralStringResource(R.plurals.target_remove_textMultiselect, viewModel.selectedTargets.size, targetName ?: "")
                    }
                    else -> {
                        pluralStringResource(R.plurals.target_remove_textMultiselect, viewModel.selectedTargets.size, viewModel.selectedTargets.size)
                    }
                },
                confirmButtonText = stringResource(de.christian2003.core.ui.R.string.button_remove),
                onDismiss = {
                    viewModel.dismissConfirmRemoveTargetDialog(false)
                },
                onConfirm = {
                    viewModel.dismissConfirmRemoveTargetDialog(true)
                }
            )
        }
        else -> { }
    }
}


/**
 * Displays a list of all targets.
 *
 * @param state                         State of the sheet.
 * @param targets                       List of targets to display.
 * @param isHelpCardVisible             Whether the help card is visible.
 * @param onRemoveTarget                Callback invoked to remove a target.
 * @param onEditTargetUrl               Callback invoked to edit the URL of a target.
 * @param onShowCertificateHelp         Callback invoked to show help about certificate issues.
 * @param onShowCertificateDetails      Callback invoked to show the details about the signing
 *                                      certificate of the target.
 * @param onSelectPackages              Callback invoked to show the dialog to selected installed
 *                                      Android packages.
 * @param onQueryLocalizedPackageName   Callback invoked to query the localized name for an
 *                                      installed package
 * @param onQueryPackageIcon            Callback invoked to query the icon for an installed package.
 * @param onDismissHelpCard             Callback invoked to dismiss the help card.
 * @param onQueryIsTargetValid          Callback invoked to query whether a target is valid.
 * @param onQueryIsSelected             Callback invoked to query whether the target is selected.
 * @param onStartMultiselect            Callback invoked to start multiselect with the provided target.
 * @param onToggleSelection             Callback invoked to toggle whether this target is selected.
 * @param onGeneratePositiveColor       Callback invoked to generate a positive color.
 * @param modifier                      Modifier.
 */
@Composable
private fun TargetsList(
    state: TargetSheetState,
    targets: List<Target>,
    isHelpCardVisible: Boolean,
    onRemoveTarget: (Target) -> Unit,
    onEditTargetUrl: (Target) -> Unit,
    onShowCertificateHelp: () -> Unit,
    onShowCertificateDetails: (Target) -> Unit,
    onSelectPackages: () -> Unit,
    onQueryLocalizedPackageName: (String) -> String?,
    onQueryPackageIcon: (String) -> Drawable?,
    onDismissHelpCard: () -> Unit,
    onQueryIsTargetValid: (Target) -> Boolean,
    onQueryIsSelected: (Target) -> Boolean,
    onStartMultiselect: (Target) -> Unit,
    onToggleSelection: (Target) -> Unit,
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
                        state = state,
                        target = target,
                        isFirst = index == 0,
                        isLast = index == targets.size - 1,
                        onRemove = onRemoveTarget,
                        onShowCertificateHelp = onShowCertificateHelp,
                        onShowCertificateDetails = onShowCertificateDetails,
                        onSelectPackages = onSelectPackages,
                        onQueryLocalizedPackageName = onQueryLocalizedPackageName,
                        onQueryPackageIcon = onQueryPackageIcon,
                        onQueryIsTargetValid = onQueryIsTargetValid,
                        onQueryIsSelected = onQueryIsSelected,
                        onStartMultiselect = onStartMultiselect,
                        onToggleSelection = onToggleSelection,
                        onGeneratePositiveColor = onGeneratePositiveColor
                    )
                }
                else {
                    TargetsListRowWebsite(
                        state = state,
                        target = target,
                        isFirst = index == 0,
                        isLast = index == targets.size - 1,
                        onRemove = onRemoveTarget,
                        onEdit = onEditTargetUrl,
                        onQueryIsSelected = onQueryIsSelected,
                        onStartMultiselect = onStartMultiselect,
                        onToggleSelection = onToggleSelection
                    )
                }
            }
        }
    }
}


/**
 * List row for the targets list to display an Android app target.
 *
 * @param state                         State of the sheet.
 * @param target                        Android app target to display.
 * @param isFirst                       Whether the android app is the first in the list.
 * @param isLast                        Whether the android app is the last in the list.
 * @param onRemove                      Callback invoked to remove the target.
 * @param onShowCertificateHelp         Callback invoked to show help about certificate issues.
 * @param onShowCertificateDetails      Callback invoked to show the certificate details of the
 *                                      package.
 * @param onSelectPackages              Callback invoked to show the dialog to select installed
 *                                      Android packages.
 * @param onQueryLocalizedPackageName   Callback invoked to query the localized name for an
 *                                      installed package
 * @param onQueryPackageIcon            Callback invoked to query the icon for an installed package.
 * @param onQueryIsTargetValid          Callback invoked to query whether a target is valid.
 * @param onQueryIsSelected             Callback invoked to query whether the target is selected.
 * @param onStartMultiselect            Callback invoked to start multiselect with the provided target.
 * @param onToggleSelection             Callback invoked to toggle whether this target is selected.
 * @param onGeneratePositiveColor       Callback invoked to generate a positive color.
 */
@Composable
private fun TargetsListRowPackage(
    state: TargetSheetState,
    target: Target,
    isFirst: Boolean,
    isLast: Boolean,
    onRemove: (Target) -> Unit,
    onShowCertificateHelp: () -> Unit,
    onShowCertificateDetails: (Target) -> Unit,
    onSelectPackages: () -> Unit,
    onQueryLocalizedPackageName: (String) -> String?,
    onQueryPackageIcon: (String) -> Drawable?,
    onQueryIsTargetValid: (Target) -> Boolean,
    onQueryIsSelected: (Target) -> Boolean,
    onStartMultiselect: (Target) -> Unit,
    onToggleSelection: (Target) -> Unit,
    onGeneratePositiveColor: (Color, Boolean) -> Color
) {
    val localizedPackageName: String = onQueryLocalizedPackageName(target.name) ?: ""
    val isValid: Boolean = onQueryIsTargetValid(target)
    val packageIcon: Drawable? = onQueryPackageIcon(target.name)
    val positiveColor: Color = onGeneratePositiveColor(MaterialTheme.colorScheme.error, MaterialTheme.isDarkTheme())
    val isSelected: Boolean = onQueryIsSelected(target)

    ListItemContainer(
        isFirst = isFirst,
        isLast = isLast,
        isSelected = isSelected
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        if (state == TargetSheetState.Multiselect) {
                            onToggleSelection(target)
                        }
                    },
                    onLongClick = {
                        when (state) {
                            TargetSheetState.Default -> onStartMultiselect(target)
                            TargetSheetState.Multiselect -> onToggleSelection(target)
                        }
                    }
                )
                .padding(
                    start = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal),
                    top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical),
                    end = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal) - 12.dp,
                    bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                )
        ) {
            if (packageIcon == null || (isSelected && state == TargetSheetState.Multiselect)) {
                Shape(
                    shape = MaterialShapes.Clover8Leaf,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.secondaryContainer
                    },
                    modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_m))
                ) {
                    Icon(
                        painter = if (isSelected) {
                            painterResource(de.christian2003.core.ui.R.drawable.ic_check)
                        } else {
                            painterResource(R.drawable.ic_packages)
                        },
                        contentDescription = "",
                        tint = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        },
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

                var rowModifier: Modifier = Modifier.clip(MaterialTheme.shapes.small)
                if (state == TargetSheetState.Default) {
                    rowModifier = rowModifier.clickable {
                        if (!isValid) {
                           onShowCertificateHelp()
                        }
                        else {
                           onShowCertificateDetails(target)
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = rowModifier.padding(horizontal = 4.dp)
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
            if (state == TargetSheetState.Default) {
                ContextActions(
                    actions = listOf(
                        ContextAction(
                            text = stringResource(R.string.target_context_package_edit),
                            icon = painterResource(de.christian2003.core.ui.R.drawable.ic_edit),
                            onClick = onSelectPackages
                        ),
                        ContextAction(
                            text = stringResource(R.string.target_context_package_remove),
                            icon = painterResource(de.christian2003.core.ui.R.drawable.ic_delete),
                            onClick = {
                                onRemove(target)
                            }
                        ),
                        ContextActionDivider(),
                        ContextAction(
                            text = if (isValid) {
                                stringResource(R.string.target_context_package_showCertDetails)
                            } else {
                                stringResource(R.string.target_context_package_showCertError)
                            },
                            icon = painterResource(de.christian2003.core.ui.R.drawable.ic_info_outlined),
                            onClick = {
                                if (isValid) {
                                    onShowCertificateDetails(target)
                                } else {
                                    onShowCertificateHelp()
                                }
                            }
                        ),
                    )
                )
            }
        }
    }
}


/**
 * List row for the targets list to display a website target.
 *
 * @param state                 State of the sheet.
 * @param target                Website target to display.
 * @param isFirst               Whether the website is the first in the list.
 * @param isLast                Whether the website is the last in the list.
 * @param onRemove              Callback invoked to remove the target.
 * @param onEdit                Callback invoked to edit this website target.
 * @param onQueryIsSelected     Callback invoked to query whether the target is selected.
 * @param onStartMultiselect    Callback invoked to start multiselect with the provided target.
 * @param onToggleSelection     Callback invoked to toggle whether this target is selected.
 */
@Composable
private fun TargetsListRowWebsite(
    state: TargetSheetState,
    target: Target,
    isFirst: Boolean,
    isLast: Boolean,
    onRemove: (Target) -> Unit,
    onEdit: (Target) -> Unit,
    onQueryIsSelected: (Target) -> Boolean,
    onStartMultiselect: (Target) -> Unit,
    onToggleSelection: (Target) -> Unit
) {
    val isSelected: Boolean = onQueryIsSelected(target)

    ListItemContainer(
        isFirst = isFirst,
        isLast = isLast,
        isSelected = isSelected
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        if (state == TargetSheetState.Multiselect) {
                            onToggleSelection(target)
                        }
                    },
                    onLongClick = {
                        when (state) {
                            TargetSheetState.Default -> onStartMultiselect(target)
                            TargetSheetState.Multiselect -> onToggleSelection(target)
                        }
                    }
                )
                .padding(
                    start = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal),
                    top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical),
                    end = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal) - 12.dp,
                    bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                )
        ) {
            Shape(
                shape = MaterialShapes.Clover8Leaf,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.secondaryContainer
                },
                modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_m))
            ) {
                Icon(
                    painter = if (isSelected) {
                        painterResource(de.christian2003.core.ui.R.drawable.ic_check)
                    } else {
                        painterResource(R.drawable.ic_website)
                    },
                    contentDescription = "",
                    tint = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    },
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
            if (state == TargetSheetState.Default) {
                ContextActions(
                    actions = listOf(
                        ContextAction(
                            text = stringResource(R.string.target_context_website_edit),
                            icon = painterResource(de.christian2003.core.ui.R.drawable.ic_edit),
                            onClick = {
                                onEdit(target)
                            }
                        ),
                        ContextAction(
                            text = stringResource(R.string.target_context_website_remove),
                            icon = painterResource(de.christian2003.core.ui.R.drawable.ic_delete),
                            onClick = {
                                onRemove(target)
                            }
                        )
                    )
                )
            }
        }
    }
}


/**
 * Top bar for the sheet.
 *
 * @param state                     State of the sheet.
 * @param selectedTargetsCount      Number of targets currently selected.
 * @param onSelectAllTargets        Callback invoked to select all targets.
 * @param onDeleteSelectedTargets   Callback invoked to delete all selected targets.
 * @param onDismissMultiselectState Callback invoked to dismiss the multiselect state.
 * @param onDismissDialog           Callback invoked to dismiss the sheet without saving.
 * @param onSave                    Callback invoked to dismiss the sheet and save changes.
 */
@Composable
private fun TopBar(
    state: TargetSheetState,
    selectedTargetsCount: Int,
    onSelectAllTargets: () -> Unit,
    onDeleteSelectedTargets: () -> Unit,
    onDismissMultiselectState: () -> Unit,
    onDismissDialog: () -> Unit,
    onSave: () -> Unit
) {
    when (state) {
        TargetSheetState.Default -> {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.target_titleDefault))
                },
                navigationIcon = {
                    Tooltip(
                        tooltip = stringResource(de.christian2003.core.ui.R.string.tooltip_closeWithoutSaving),
                        anchor = TooltipAnchorPosition.End
                    ) {
                        IconButton(
                            onClick = onDismissDialog
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
        TargetSheetState.Multiselect -> {
            TopAppBar(
                title = {
                    Text(
                        text = pluralStringResource(R.plurals.target_titleReorder_targets, selectedTargetsCount, selectedTargetsCount),
                        color = MaterialTheme.colorScheme.primary,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    Tooltip(
                        tooltip = stringResource(de.christian2003.core.ui.R.string.tooltip_closeMultiselect),
                        anchor = TooltipAnchorPosition.End
                    ) {
                        IconButton(
                            onClick = onDismissMultiselectState
                        ) {
                            Icon(
                                painter = painterResource(de.christian2003.core.ui.R.drawable.ic_cancel),
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                },
                actions = {
                    Tooltip(
                        tooltip = stringResource(de.christian2003.core.ui.R.string.tooltip_selectAllTargets),
                        anchor = TooltipAnchorPosition.End
                    ) {
                        IconButton(
                            onClick = onSelectAllTargets
                        ) {
                            Icon(
                                painter = painterResource(de.christian2003.core.ui.R.drawable.ic_selectall),
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                    Tooltip(
                        tooltip = stringResource(de.christian2003.core.ui.R.string.tooltip_deleteSelectedTargets),
                        anchor = TooltipAnchorPosition.End
                    ) {
                        IconButton(
                            onClick = onDeleteSelectedTargets,
                            enabled = selectedTargetsCount > 0
                        ) {
                            Icon(
                                painter = painterResource(de.christian2003.core.ui.R.drawable.ic_delete),
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            )
        }
    }
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
