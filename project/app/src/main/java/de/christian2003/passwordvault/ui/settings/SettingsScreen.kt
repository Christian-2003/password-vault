package de.christian2003.passwordvault.ui.settings

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil.compose.rememberAsyncImagePainter
import de.christian2003.core.ui.composables.Headline
import de.christian2003.core.ui.composables.HeadlineIndentation
import de.christian2003.core.ui.composables.settings.SettingsItemButton
import de.christian2003.core.ui.composables.settings.SettingsItemSwitch
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
    onNavigateToAutofillSettings: () -> Unit,
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
                Headline(
                    title = stringResource(R.string.settings_customization),
                    indentation = HeadlineIndentation.PrefixIconLevel
                )
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
                Headline(
                    title = stringResource(R.string.settings_security),
                    indentation = HeadlineIndentation.PrefixIconLevel
                )
                SettingsItemButton(
                    title = stringResource(R.string.settings_security_authTitle),
                    info = stringResource(R.string.settings_security_authInfo),
                    prefixIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_auth),
                    endIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_next),
                    isFirst = true,
                    isLast = false,
                    onClick = onNavigateToAuthSettings
                )
                SettingsItemButton(
                    title = stringResource(R.string.settings_security_autofillTitle),
                    info = stringResource(R.string.settings_security_autofillInfo),
                    prefixIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_autofill),
                    endIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_next),
                    isFirst = false,
                    isLast = true,
                    onClick = onNavigateToAutofillSettings
                )
            }

            //Help:
            item {
                Headline(
                    title = stringResource(R.string.settings_help),
                    indentation = HeadlineIndentation.PrefixIconLevel
                )
                SettingsItemButton(
                    title = stringResource(R.string.settings_help_helpMessagesTitle),
                    info = stringResource(R.string.settings_help_helpMessagesInfo),
                    prefixIcon = painterResource(R.drawable.ic_help_outlined),
                    endIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_next),
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
                    prefixIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_dev),
                    endIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_next),
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
