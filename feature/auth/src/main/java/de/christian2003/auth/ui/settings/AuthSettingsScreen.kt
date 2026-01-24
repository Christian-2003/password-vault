package de.christian2003.auth.ui.settings

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import de.christian2003.auth.viewmodels.AuthSettingsViewModel
import de.christian2003.auth.R
import de.christian2003.ui.composables.NavigationBarProtection
import de.christian2003.ui.composables.Tooltip
import de.christian2003.ui.theme.RobotoMono


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
fun AuthSettingsScreen(
    viewModel: AuthSettingsViewModel,
    onNavigateUp: () -> Unit,
    onNavigateToPassword: () -> Unit,
    onNavigateToBiometrics: () -> Unit,
    onNavigateToRecoveryCodes: () -> Unit
) {
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
                .padding(horizontal = dimensionResource(de.christian2003.ui.R.dimen.margin_horizontal))
        ) {
            //Master password:
            MasterPasswordSection(
                onEditMasterPassword = onNavigateToPassword,
                modifier = Modifier.padding(bottom = dimensionResource(de.christian2003.ui.R.dimen.padding_vertical))
            )

            //Recovery codes:
            RecoveryCodesSection(
                onGenerateNewRecoveryCodes = onNavigateToRecoveryCodes,
                modifier = Modifier.padding(bottom = dimensionResource(de.christian2003.ui.R.dimen.padding_vertical))
            )

            //Biometrics:
            BiometricsSection(
                onToggleBiometrics = onNavigateToBiometrics,
                areBiometricsConfigured = viewModel.areBiometricsConfigured,
                modifier = Modifier.padding(bottom = dimensionResource(de.christian2003.ui.R.dimen.padding_vertical))
            )

            Box(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
        }

        NavigationBarProtection(innerPadding.calculateBottomPadding())
    }
}


@Composable
private fun MasterPasswordSection(
    onEditMasterPassword: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(
                horizontal = dimensionResource(de.christian2003.ui.R.dimen.margin_horizontal),
                vertical = dimensionResource(de.christian2003.ui.R.dimen.padding_vertical)
            )
    ) {
        SectionHeader(
            title = stringResource(R.string.authSettings_masterPassword_title),
            painter = painterResource(de.christian2003.ui.R.drawable.ic_password),
            isActive = true,
            activeLabelTooltip = stringResource(R.string.authSettings_masterPassword_activeLabelTooltip),
            modifier = Modifier.padding(bottom = dimensionResource(de.christian2003.ui.R.dimen.padding_vertical))
        )
        Row {
            Box(
                modifier = Modifier.width(dimensionResource(de.christian2003.ui.R.dimen.image_xs) + dimensionResource(de.christian2003.ui.R.dimen.padding_horizontal))
            )
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Tooltip(
                    tooltip = stringResource(R.string.authSettings_masterPassword_placeholderTooltip)
                ) {
                    Text(
                        text = stringResource(R.string.authSettings_masterPassword_placeholder),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                TextButton(
                    onClick = onEditMasterPassword,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(stringResource(R.string.authSettings_masterPassword_editButton))
                }
            }
        }
    }
}


@Composable
private fun RecoveryCodesSection(
    onGenerateNewRecoveryCodes: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(
                horizontal = dimensionResource(de.christian2003.ui.R.dimen.margin_horizontal),
                vertical = dimensionResource(de.christian2003.ui.R.dimen.padding_vertical)
            )
    ) {
        SectionHeader(
            title = stringResource(R.string.authSettings_biometrics_title),
            painter = painterResource(R.drawable.ic_recovery),
            isActive = true,
            activeLabelTooltip = stringResource(R.string.authSettings_recoveryCodes_activeLabelTooltip),
            modifier = Modifier.padding(bottom = dimensionResource(de.christian2003.ui.R.dimen.padding_vertical))
        )
        Row {
            Box(
                modifier = Modifier.width(dimensionResource(de.christian2003.ui.R.dimen.image_xs) + dimensionResource(de.christian2003.ui.R.dimen.padding_horizontal))
            )
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
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
                TextButton(
                    onClick = onGenerateNewRecoveryCodes,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(stringResource(R.string.authSettings_recoveryCodes_generateButton))
                }
            }
        }
    }
}


@Composable
private fun BiometricsSection(
    onToggleBiometrics: () -> Unit,
    areBiometricsConfigured: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(
                horizontal = dimensionResource(de.christian2003.ui.R.dimen.margin_horizontal),
                vertical = dimensionResource(de.christian2003.ui.R.dimen.padding_vertical)
            )
    ) {
        SectionHeader(
            title = stringResource(R.string.authSettings_biometrics_title),
            painter = painterResource(de.christian2003.ui.R.drawable.ic_biometrics),
            isActive = areBiometricsConfigured,
            activeLabelTooltip = if (areBiometricsConfigured) {
                stringResource(R.string.authSettings_biometrics_activeLabelTooltip)
            } else {
                stringResource(R.string.authSettings_biometrics_inactiveLabelTooltip)
            }
        )
        TextButton(
            onClick = onToggleBiometrics,
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


@Composable
private fun SectionHeader(
    title: String,
    painter: Painter,
    isActive: Boolean,
    activeLabelTooltip: String,
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
                .padding(end = dimensionResource(de.christian2003.ui.R.dimen.padding_horizontal))
                .size(dimensionResource(de.christian2003.ui.R.dimen.image_xs))
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
                isActive = isActive,
                tooltipText = activeLabelTooltip
            )
        }
    }
}


/**
 * Displays the label showing whether an authentication method is active or not.
 *
 * @param isActive      Whether the authentication method is active.
 * @param tooltipText   Text for the label tooltip.
 */
@Composable
private fun ActiveIndicator(
    isActive: Boolean,
    tooltipText: String
) {
    val foregroundColor: Color = if (isActive) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.error
    }

    Tooltip(
        tooltip = tooltipText
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = if (isActive) {
                    painterResource(R.drawable.ic_active)
                } else {
                    painterResource(R.drawable.ic_inactive)
                },
                contentDescription = "",
                tint = foregroundColor,
                modifier = Modifier
                    .padding(end = dimensionResource(de.christian2003.ui.R.dimen.padding_horizontal) / 2)
                    .size(dimensionResource(de.christian2003.ui.R.dimen.image_xxs))
            )
            Text(
                text = if (isActive) {
                    stringResource(R.string.authSettings_activeLabel)
                } else {
                    stringResource(R.string.authSettings_inactiveLabel)
                },
                color = foregroundColor,
                style = MaterialTheme.typography.labelMedium
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
                    painter = painterResource(de.christian2003.ui.R.drawable.ic_back),
                    contentDescription = ""
                )
            }
        }
    )
}
