package de.christian2003.passwordvault.ui.settings

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import coil.compose.rememberAsyncImagePainter
import de.christian2003.core.ui.composables.Headline
import de.christian2003.core.ui.composables.ListItemContainer
import de.christian2003.core.ui.theme.ThemeContrast
import de.christian2003.passwordvault.R
import de.christian2003.passwordvault.models.dialogs.SettingsScreenDialog
import de.christian2003.passwordvault.viewmodels.SettingsViewModel
import java.time.LocalDate


@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateUp: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToAuthSettings: () -> Unit,
    onNavigateToDevSettings: () -> Unit,
    onUseGlobalThemeChange: (Boolean) -> Unit,
    onThemeContrastChange: (ThemeContrast) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.settings_title))
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
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding)
        ) {
            //General section:
            item {
                GeneralSection()
            }

            //Customization:
            item {
                Headline(stringResource(R.string.settings_customization))
                SettingsItemSwitch(
                    title = stringResource(R.string.settings_customization_globalThemeTitle),
                    info = stringResource(R.string.settings_customization_globalThemeInfo),
                    prefixIcon = painterResource(R.drawable.ic_theme),
                    checked = viewModel.useGlobalTheme,
                    isFirst = true,
                    isLast = viewModel.useGlobalTheme,
                    onCheckedChange = { enabled ->
                        viewModel.updateUseGlobalTheme(enabled)
                        onUseGlobalThemeChange(enabled)
                    }
                )
                AnimatedVisibility(!viewModel.useGlobalTheme) {
                    SettingsItemButton(
                        title = stringResource(R.string.settings_customization_contrastTitle),
                        info = stringResource(R.string.settings_customization_contrastInfo),
                        prefixIcon = painterResource(R.drawable.ic_contrast),
                        isLast = true,
                        onClick = {
                            viewModel.dialog = SettingsScreenDialog.Contrast
                        }
                    )
                }
            }

            //Security:
            item {
                Headline(stringResource(R.string.settings_security))
                SettingsItemButton(
                    title = stringResource(R.string.settings_security_authTitle),
                    info = stringResource(R.string.settings_security_authInfo),
                    prefixIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_auth),
                    endIcon = painterResource(R.drawable.ic_next),
                    isFirst = true,
                    isLast = true,
                    onClick = onNavigateToAuthSettings
                )
            }

            //Help:
            item {
                Headline(stringResource(R.string.settings_help))
                SettingsItemButton(
                    title = stringResource(R.string.settings_help_helpMessagesTitle),
                    info = stringResource(R.string.settings_help_helpMessagesInfo),
                    prefixIcon = painterResource(R.drawable.ic_help_outlined),
                    endIcon = painterResource(R.drawable.ic_next),
                    isFirst = true,
                    isLast = true,
                    onClick = onNavigateToHelp
                )
            }

            //Other:
            item {
                SettingsItemButton(
                    title = stringResource(R.string.settings_developmentTitle),
                    info = stringResource(R.string.settings_developmentInfo),
                    prefixIcon = painterResource(R.drawable.ic_dev),
                    endIcon = painterResource(R.drawable.ic_next),
                    isFirst = true,
                    isLast = true,
                    onClick = onNavigateToDevSettings
                )
            }
        }
    }

    when (viewModel.dialog) {
        SettingsScreenDialog.Contrast -> {
            ContrastDialog(
                contrast = viewModel.themeContrast,
                onDismiss = {
                    viewModel.dialog = SettingsScreenDialog.None
                },
                onSave = { contrast ->
                    viewModel.dialog = SettingsScreenDialog.None
                    viewModel.updateThemeContrast(contrast)
                    onThemeContrastChange(contrast)
                }
            )
        }
        else -> { /* Do not show any dialog */ }
    }
}


/**
 * Composable displays an item button.
 *
 * @param title         Title for the setting.
 * @param info          Info for the setting.
 * @param onClick       Callback to invoke when the item button is clicked.
 * @param endIcon       Optional end icon.
 * @param isFirst       Whether this is the first list item.
 * @param isLast        Whether this is the last list item.
 * @param prefixIcon    Optional prefix icon.
 * @param badgeCount    Optional count shown in a badge.
 */
@Composable
fun SettingsItemButton(
    title: String,
    info: String,
    onClick: () -> Unit,
    endIcon: Painter? = null,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    prefixIcon: Painter? = null,
    badgeCount: Int = 0
) {
    ListItemContainer(
        isFirst = isFirst,
        isLast = isLast
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                }
                .padding(
                    horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal),
                    vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (prefixIcon != null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(end = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
                        .size(dimensionResource(de.christian2003.core.ui.R.dimen.image_xs))
                ) {
                    Icon(
                        painter = prefixIcon,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        contentDescription = "",
                        modifier = Modifier.fillMaxSize()
                    )
                    if (badgeCount > 0) {
                        Badge(
                            modifier = Modifier.offset(
                                x = dimensionResource(de.christian2003.core.ui.R.dimen.image_xs) / 3,
                                y = dimensionResource(de.christian2003.core.ui.R.dimen.image_xs) / -3
                            )
                        ) {
                            Text(badgeCount.toString())
                        }
                    }
                }
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (endIcon != null) {
                        Icon(
                            painter = endIcon,
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = "",
                            modifier = Modifier
                                .padding(start = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal) / 2)
                                .size(dimensionResource(de.christian2003.core.ui.R.dimen.image_xxs))
                        )
                    }
                }
                Text(
                    text = info,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}


/**
 * Composable displays an item switch.
 *
 * @param title             Title for the setting.
 * @param info              Info for the setting.
 * @param checked           Whether the switch is checked.
 * @param onCheckedChange   Callback invoked once the switch is (un)checked.
 * @param prefixIcon        Optional prefix icon.
 */
@Composable
fun SettingsItemSwitch(
    title: String,
    info: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    prefixIcon: Painter? = null
) {
    ListItemContainer(
        isFirst = isFirst,
        isLast = isLast
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onCheckedChange(!checked)
                }
                .padding(
                    vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical),
                    horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (prefixIcon != null) {
                Icon(
                    painter = prefixIcon,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(end = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
                        .size(dimensionResource(de.christian2003.core.ui.R.dimen.image_xs))
                )
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = info,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.padding(start = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
            )
        }
    }
}


/**
 * Displays the general information which contains info about the app.
 */
@Composable
private fun GeneralSection() {
    val context: Context = LocalContext.current
    val version: String? = context.packageManager.getPackageInfo(context.packageName, 0).versionName
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                end = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
            )
            .clip(MaterialTheme.shapes.extraLarge)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(
                horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal),
                vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
            )
    ) {
        Image(
            painter = rememberAsyncImagePainter(de.christian2003.core.ui.R.mipmap.launcher),
            contentDescription = "",
            modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_l))
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
        ) {
            Text(
                text = stringResource(de.christian2003.core.ui.R.string.app_name),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLargeEmphasized
            )
            if (version != null) {
                Text(
                    text = version,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = stringResource(R.string.settings_about_copyright, LocalDate.now().year.toString()),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
            )
        }
    }
}
