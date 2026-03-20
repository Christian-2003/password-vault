package de.christian2003.feature.auth.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.christian2003.feature.auth.viewmodels.AuthSettingsViewModel
import de.christian2003.feature.auth.R
import de.christian2003.feature.auth.models.dialogs.AuthSettingsScreenDialog
import de.christian2003.feature.auth.models.other.AuthRecommendation
import de.christian2003.core.security.domain.entities.AuthMetadata
import de.christian2003.core.ui.composables.NavigationBarProtection
import de.christian2003.core.ui.composables.Shape
import de.christian2003.core.ui.composables.Tooltip
import de.christian2003.core.ui.composables.dialog.SimpleDialog
import de.christian2003.core.ui.theme.RobotoMono
import de.christian2003.core.ui.theme.isDarkTheme
import java.time.LocalDateTime


/**
 * Screen displays the settings for the authentication.
 *
 * @param viewModel                 View model.
 * @param onNavigateUp              Callback invoked to navigate up the navigation stack.
 * @param onNavigateToPassword      Callback invoked to change the master password.
 * @param onNavigateToBiometrics    Callback invoked to navigate to enable / disable biometrics.
 * @param onNavigateToRecoveryCodes Callback invoked to generate new recovery codes.
 */
@Composable
internal fun AuthSettingsScreen(
    viewModel: AuthSettingsViewModel,
    onNavigateUp: () -> Unit,
    onNavigateToPassword: () -> Unit,
    onNavigateToBiometrics: () -> Unit,
    onNavigateToRecoveryCodes: () -> Unit
) {
    val authMetadata: AuthMetadata? by viewModel.authMetadata.collectAsStateWithLifecycle(null)
    val areBiometricsConfigured: Boolean by viewModel.areBiometricsConfigured.collectAsStateWithLifecycle(false)

    Scaffold(
        topBar = {
            TopBar(
                onNavigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                )
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal))
        ) {
            //Recommended action:
            RecommendedActionCard(
                recommendation = viewModel.authRecommendation,
                onEditMasterPassword = onNavigateToPassword,
                onGenerateNewRecoveryCodes = onNavigateToRecoveryCodes,
                onToggleBiometrics = onNavigateToBiometrics,
                modifier = Modifier.padding(bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical) * 2)
            )

            //Master password:
            MasterPasswordSection(
                editedAt = authMetadata?.masterPasswordEditedAt,
                onEditMasterPassword = onNavigateToPassword,
                onGeneratePositiveColor = { negative, darkTheme ->
                    viewModel.generatePositiveColorFromNegativeColor(negative, darkTheme)
                },
                onGenerateNeutralColor = { seed, darkTheme ->
                    viewModel.generateNeutralColorFromSeedColor(seed, darkTheme)
                },
                onFormatTime = {
                    viewModel.formatTime(it)
                },
                modifier = Modifier.padding(bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
            )

            //Recovery codes:
            RecoveryCodesSection(
                editedAt = authMetadata?.recoveryCodesEditedAt,
                onGenerateNewRecoveryCodes = onNavigateToRecoveryCodes,
                onGeneratePositiveColor = { negative, darkTheme ->
                    viewModel.generatePositiveColorFromNegativeColor(negative, darkTheme)
                },
                onGenerateNeutralColor = { seed, darkTheme ->
                    viewModel.generateNeutralColorFromSeedColor(seed, darkTheme)
                },
                onFormatTime = {
                    viewModel.formatTime(it)
                },
                modifier = Modifier.padding(bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
            )

            //Biometrics:
            BiometricsSection(
                editedAt = authMetadata?.biometricsEditedAt,
                onToggleBiometrics = {
                    if (viewModel.areBiometricsAvailable) {
                        if (!areBiometricsConfigured) {
                            onNavigateToBiometrics()
                        }
                        else {
                            viewModel.dialog = AuthSettingsScreenDialog.ConfirmDisableBiometrics
                        }
                    }
                },
                areBiometricsAvailable = viewModel.areBiometricsAvailable,
                areBiometricsConfigured = areBiometricsConfigured,
                onGeneratePositiveColor = { negative, darkTheme ->
                    viewModel.generatePositiveColorFromNegativeColor(negative, darkTheme)
                },
                onGenerateNeutralColor = { seed, darkTheme ->
                    viewModel.generateNeutralColorFromSeedColor(seed, darkTheme)
                },
                onFormatTime = {
                    viewModel.formatTime(it)
                },
                modifier = Modifier.padding(bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
            )

            Box(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
        }

        NavigationBarProtection(innerPadding.calculateBottomPadding())

        when (viewModel.dialog) {
            AuthSettingsScreenDialog.ConfirmDisableBiometrics -> {
                ConfirmDisableBiometricsDialog(
                    onConfirm = {
                        viewModel.disableBiometrics()
                        viewModel.dialog = AuthSettingsScreenDialog.None
                    },
                    onDismiss = {
                        viewModel.dialog = AuthSettingsScreenDialog.None
                    }
                )
            }
            else -> { }
        }
    }
}


/**
 * Displays the card showing recommended actions to take for the user.
 *
 * @param recommendation                Recommendation.
 * @param onEditMasterPassword          Callback invoked to edit the master password.
 * @param onGenerateNewRecoveryCodes    Callback invoked to generate new codes.
 * @param onToggleBiometrics            Callback invoked to toggle biometrics.
 * @param modifier                      Modifier.
 */
@Composable
private fun RecommendedActionCard(
    recommendation: AuthRecommendation,
    onEditMasterPassword: () -> Unit,
    onGenerateNewRecoveryCodes: () -> Unit,
    onToggleBiometrics: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (recommendation == AuthRecommendation.None) {
        return
    }

    Column(
        horizontalAlignment = Alignment.End,
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraLarge)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(
                horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal),
                vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Shape(
                shape = MaterialShapes.VerySunny,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier
                    .padding(end = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
                    .size(dimensionResource(de.christian2003.core.ui.R.dimen.image_m))
            ) {
                Icon(
                    painter = painterResource(de.christian2003.core.ui.R.drawable.ic_recommendation),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_xs))
                )
            }
            Column {
                Text(
                    text = stringResource(R.string.authSettings_recommendation_title),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = when (recommendation) {
                        AuthRecommendation.ChangePassword -> stringResource(R.string.authSettings_recommendation_changePassword)
                        AuthRecommendation.RegenerateRecoveryCodes -> stringResource(R.string.authSettings_recommendation_regenerateRecoveryCodes)
                        AuthRecommendation.EnableBiometrics -> stringResource(R.string.authSettings_recommendation_enableBiometrics)
                        else -> ""
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        TextButton(
            onClick = {
                when (recommendation) {
                    AuthRecommendation.ChangePassword -> onEditMasterPassword()
                    AuthRecommendation.RegenerateRecoveryCodes -> onGenerateNewRecoveryCodes()
                    AuthRecommendation.EnableBiometrics -> onToggleBiometrics()
                    else -> { }
                }
            }
        ) {
            Text(
                text = when (recommendation) {
                    AuthRecommendation.ChangePassword -> stringResource(R.string.authSettings_masterPassword_editButton)
                    AuthRecommendation.RegenerateRecoveryCodes -> stringResource(R.string.authSettings_recoveryCodes_generateButton)
                    AuthRecommendation.EnableBiometrics -> stringResource(R.string.authSettings_biometrics_enableButton)
                    else -> ""
                }
            )
        }
    }
}


/**
 * Section displaying info about the master password..
 *
 * @param editedAt                      Time at which biometrics have been edited.
 * @param onEditMasterPassword          Callback invoked to edit the master password.
 * @param onGeneratePositiveColor       Callback invoked to generate a positive color.
 * @param onGenerateNeutralColor        Callback invoked to generate a neutral color.
 * @param onFormatTime                  Callback invoked to format a time.
 * @param modifier                      Modifier.
 */
@Composable
private fun MasterPasswordSection(
    editedAt: LocalDateTime?,
    onEditMasterPassword: () -> Unit,
    onGeneratePositiveColor: (Color, Boolean) -> Color,
    onGenerateNeutralColor: (Color, Boolean) -> Color,
    onFormatTime: (LocalDateTime) -> String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(
                horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
            )
    ) {
        SectionHeader(
            title = stringResource(R.string.authSettings_masterPassword_title),
            painter = painterResource(de.christian2003.core.ui.R.drawable.ic_password),
            isAvailable = true,
            isActive = true,
            activeLabelTooltip = stringResource(R.string.authSettings_masterPassword_activeLabelTooltip),
            onGeneratePositiveColor = onGeneratePositiveColor,
            onGenerateNeutralColor = onGenerateNeutralColor
        )
        SectionInnerContainer {
            Text(
                text = stringResource(R.string.authSettings_masterPassword_placeholderTitle),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Tooltip(
                tooltip = stringResource(R.string.authSettings_masterPassword_placeholderTooltip)
            ) {
                Text(
                    text = stringResource(R.string.authSettings_masterPassword_placeholder),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (editedAt != null) {
            Text(
                text = stringResource(R.string.authSettings_masterPassword_editedAtLabel, onFormatTime(editedAt)),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
            )
        }
        }
        TextButton(
            onClick = onEditMasterPassword,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(stringResource(R.string.authSettings_masterPassword_editButton))
        }
    }
}


/**
 * Section displaying info about recovery codes.
 *
 * @param editedAt                      Time at which biometrics have been edited.
 * @param onGenerateNewRecoveryCodes    Callback invoked to generate new codes.
 * @param onGeneratePositiveColor       Callback invoked to generate a positive color.
 * @param onGenerateNeutralColor        Callback invoked to generate a neutral color.
 * @param onFormatTime                  Callback invoked to format a time.
 * @param modifier                      Modifier.
 */
@Composable
private fun RecoveryCodesSection(
    editedAt: LocalDateTime?,
    onGenerateNewRecoveryCodes: () -> Unit,
    onGeneratePositiveColor: (Color, Boolean) -> Color,
    onGenerateNeutralColor: (Color, Boolean) -> Color,
    onFormatTime: (LocalDateTime) -> String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(
                horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
            )
    ) {
        SectionHeader(
            title = stringResource(R.string.authSettings_recoveryCodes_title),
            painter = painterResource(R.drawable.ic_recovery),
            isAvailable = true,
            isActive = true,
            activeLabelTooltip = stringResource(R.string.authSettings_recoveryCodes_activeLabelTooltip),
            onGeneratePositiveColor = onGeneratePositiveColor,
            onGenerateNeutralColor = onGenerateNeutralColor
        )
        SectionInnerContainer {
            Text(
                text = stringResource(R.string.authSettings_recoveryCodes_placeholderTitle),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Tooltip(
                tooltip = stringResource(R.string.authSettings_recoveryCodes_placeholderTooltip)
            ) {
                Column {
                    for (i: Int in 0 until 5) {
                        Text(
                            text = stringResource(R.string.authSettings_recoveryCodes_placeholder),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = RobotoMono
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            if (editedAt != null) {
                Text(
                    text = stringResource(R.string.authSettings_recoveryCodes_editedAtLabel, onFormatTime(editedAt)),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
                )
            }
        }
        TextButton(
            onClick = onGenerateNewRecoveryCodes,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(stringResource(R.string.authSettings_recoveryCodes_generateButton))
        }
    }
}


/**
 * Section displaying info about biometric authentication.
 *
 * @param areBiometricsAvailable    Whether biometrics are available.
 * @param areBiometricsConfigured   Whether biometrics are configured.
 * @param editedAt                  Time at which biometrics have been edited.
 * @param onToggleBiometrics        Callback invoked to toggle biometrics.
 * @param onGeneratePositiveColor   Callback invoked to generate a positive color.
 * @param onGenerateNeutralColor    Callback invoked to generate a neutral color.
 * @param onFormatTime              Callback invoked to format a time.
 * @param modifier                  Modifier.
 */
@Composable
private fun BiometricsSection(
    areBiometricsAvailable: Boolean,
    areBiometricsConfigured: Boolean,
    editedAt: LocalDateTime?,
    onToggleBiometrics: () -> Unit,
    onGeneratePositiveColor: (Color, Boolean) -> Color,
    onGenerateNeutralColor: (Color, Boolean) -> Color,
    onFormatTime: (LocalDateTime) -> String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(
                horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
            )
    ) {
        SectionHeader(
            title = stringResource(R.string.authSettings_biometrics_title),
            painter = painterResource(de.christian2003.core.ui.R.drawable.ic_biometrics),
            isAvailable = areBiometricsAvailable,
            isActive = areBiometricsConfigured,
            activeLabelTooltip = when {
                !areBiometricsAvailable -> stringResource(R.string.authSettings_biometrics_unavailableLabelTooltip)
                areBiometricsConfigured -> stringResource(R.string.authSettings_biometrics_activeLabelTooltip)
                else -> stringResource(R.string.authSettings_biometrics_inactiveLabelTooltip)
            },
            onGeneratePositiveColor = onGeneratePositiveColor,
            onGenerateNeutralColor = onGenerateNeutralColor
        )
        if (areBiometricsConfigured) {
            SectionInnerContainer {
                Text(
                    text = stringResource(R.string.authSettings_biometrics_commonBiometrics_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
                CommonlyUsedBiometricsRow(
                    painter = painterResource(de.christian2003.core.ui.R.drawable.ic_biometrics),
                    label = stringResource(R.string.authSettings_biometrics_commonBiometrics_fingerprint)
                )
                CommonlyUsedBiometricsRow(
                    painter = painterResource(R.drawable.ic_face),
                    label = stringResource(R.string.authSettings_biometrics_commonBiometrics_face)
                )
                CommonlyUsedBiometricsRow(
                    painter = painterResource(R.drawable.ic_heartbeat),
                    label = stringResource(R.string.authSettings_biometrics_commonBiometrics_heartbeat)
                )
                if (editedAt != null) {
                    Text(
                        text = stringResource(R.string.authSettings_biometrics_editedAtLabel, onFormatTime(editedAt)),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
                    )
                }
            }
        }
        TextButton(
            onClick = onToggleBiometrics,
            enabled = areBiometricsAvailable,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text = if (areBiometricsConfigured) {
                    stringResource(R.string.authSettings_biometrics_disableButton)
                } else {
                    stringResource(R.string.authSettings_biometrics_enableButton)
                }
            )
        }
    }
}


/**
 * Row displaying commonly used biometrics.
 *
 * @param painter   Icon painter.
 * @param label     Label.
 * @param modifier  Modifier.
 */
@Composable
private fun CommonlyUsedBiometricsRow(
    painter: Painter,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(top = 4.dp)
    ) {
        Icon(
            painter = painter,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
        )
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


/**
 * Inner container for the sections.
 *
 * @param modifier  Modifier.
 * @param content   Content.
 */
@Composable
private fun SectionInnerContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = dimensionResource(de.christian2003.core.ui.R.dimen.image_xs) + dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal),
                top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
            )
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(
                horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
            )
    ) {
        content()
    }
}


/**
 * Section header for 'Master password', 'Recovery codes' and 'Biometrics'.
 *
 * @param title                     Title for the section.
 * @param painter                   Icon for the section.
 * @param isAvailable               Whether the auth method is available.
 * @param isActive                  Whether the auth method is active.
 * @param activeLabelTooltip        Tooltip displayed for the active / inactive / unavailable label
 * @param onGeneratePositiveColor   Callback invoked to generate a positive color.
 * @param onGenerateNeutralColor    Callback invoked to generate a neutral color.
 * @param modifier                  Modifier.
 */
@Composable
private fun SectionHeader(
    title: String,
    painter: Painter,
    isAvailable: Boolean,
    isActive: Boolean,
    activeLabelTooltip: String,
    onGeneratePositiveColor: (Color, Boolean) -> Color,
    onGenerateNeutralColor: (Color, Boolean) -> Color,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(
            painter = painter,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(end = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
                .size(dimensionResource(de.christian2003.core.ui.R.dimen.image_xs))
        )
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
            ActiveIndicator(
                isAvailable = isAvailable,
                isActive = isActive,
                tooltipText = activeLabelTooltip,
                onGeneratePositiveColor = onGeneratePositiveColor,
                onGenerateNeutralColor = onGenerateNeutralColor
            )
        }
    }
}


/**
 * Displays the label showing whether an authentication method is active or not.
 *
 * @param isAvailable               Whether the authentication method is available.
 * @param isActive                  Whether the authentication method is active.
 * @param tooltipText               Text for the label tooltip.
 * @param onGeneratePositiveColor   Callback invoked to generate a positive color.
 * @param onGenerateNeutralColor    Callback invoked to generate a neutral color.
 */
@Composable
private fun ActiveIndicator(
    isAvailable: Boolean,
    isActive: Boolean,
    tooltipText: String,
    onGeneratePositiveColor: (Color, Boolean) -> Color,
    onGenerateNeutralColor: (Color, Boolean) -> Color
) {
    val foregroundColor: Color = when {
        isActive && isAvailable -> onGeneratePositiveColor(MaterialTheme.colorScheme.error, MaterialTheme.isDarkTheme())
        !isAvailable -> onGenerateNeutralColor(MaterialTheme.colorScheme.primary, MaterialTheme.isDarkTheme())
        else -> MaterialTheme.colorScheme.error
    }

    Tooltip(
        tooltip = tooltipText
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = when {
                    isActive && isAvailable -> painterResource(de.christian2003.core.ui.R.drawable.ic_check_filled)
                    else -> painterResource(R.drawable.ic_inactive)
                },
                contentDescription = "",
                tint = foregroundColor,
                modifier = Modifier
                    .padding(end = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal) / 2)
                    .size(dimensionResource(de.christian2003.core.ui.R.dimen.image_xxs))
            )
            Text(
                text = when {
                    isActive && isAvailable -> stringResource(R.string.authSettings_activeLabel)
                    !isAvailable -> stringResource(R.string.authSettings_unavailableLabel)
                    else -> stringResource(R.string.authSettings_inactiveLabel)
                },
                color = foregroundColor,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}


/**
 * Top bar for the screen.
 *
 * @param onNavigateUp  Callback invoked to navigate up the navigation stack.
 */
@Composable
private fun TopBar(
    onNavigateUp: () -> Unit
) {
    TopAppBar(
        title = {
            Text(stringResource(R.string.authSettings_title))
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigateUp
            ) {
                Icon(
                    painter = painterResource(de.christian2003.core.ui.R.drawable.ic_back),
                    contentDescription = ""
                )
            }
        }
    )
}


/**
 * Shows the dialog through which to confirm disabling the biometric authentication.
 *
 * @param onConfirm Callback invoked to confirm disabling.
 * @param onDismiss Callback invoked to dismiss the dialog without disabling biometrics.
 */
@Composable
private fun ConfirmDisableBiometricsDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    SimpleDialog(
        title = stringResource(R.string.authSettings_biometrics_confirmDisableDialog_title),
        text = stringResource(R.string.authSettings_biometrics_confirmDisableDialog_text),
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        dismissButtonText = stringResource(de.christian2003.core.ui.R.string.button_cancel),
        confirmButtonText = stringResource(de.christian2003.core.ui.R.string.button_confirm)
    )
}
