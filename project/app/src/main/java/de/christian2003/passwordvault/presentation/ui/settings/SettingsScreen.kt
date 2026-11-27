package de.christian2003.passwordvault.presentation.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.net.toUri
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import de.christian2003.core.ui.composables.Headline
import de.christian2003.core.ui.composables.HeadlineIndentation
import de.christian2003.core.ui.composables.ListItemContainer
import de.christian2003.core.ui.composables.settings.SettingsItemButton
import de.christian2003.core.ui.composables.settings.SettingsItemSwitch
import de.christian2003.core.ui.theme.ThemeContrast
import de.christian2003.passwordvault.R
import de.christian2003.passwordvault.domain.entities.LegalPage
import de.christian2003.passwordvault.domain.entities.OtherSoftware
import de.christian2003.passwordvault.presentation.models.dialogs.SettingsScreenDialog
import de.christian2003.passwordvault.presentation.viewmodels.SettingsViewModel
import java.time.LocalDate


@Composable
internal fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateUp: () -> Unit,
    onNavigateToHelpMessages: () -> Unit,
    onNavigateToHelpPages: () -> Unit,
    onNavigateToAuthSettings: () -> Unit,
    onNavigateToAutofillSettings: () -> Unit,
    onNavigateToExport: () -> Unit,
    onNavigateToImport: () -> Unit,
    onNavigateToDevSettings: () -> Unit,
    onUseGlobalThemeChange: (Boolean) -> Unit,
    onThemeContrastChange: (ThemeContrast) -> Unit
) {
    val context: Context = LocalContext.current

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

            //Data
            item {
                Headline(
                    title = stringResource(R.string.settings_data),
                    indentation = HeadlineIndentation.PrefixIconLevel
                )
                SettingsItemButton(
                    title = stringResource(R.string.settings_data_exportTitle),
                    info = stringResource(R.string.settings_data_exportInfo),
                    prefixIcon = painterResource(R.drawable.ic_upload),
                    endIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_next),
                    isFirst = true,
                    isLast = false,
                    onClick = onNavigateToExport
                )
                SettingsItemButton(
                    title = stringResource(R.string.settings_data_importTitle),
                    info = stringResource(R.string.settings_data_importInfo),
                    prefixIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_download),
                    endIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_next),
                    isFirst = false,
                    isLast = true,
                    onClick = onNavigateToImport
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
                    isLast = false,
                    onClick = onNavigateToHelpMessages
                )
                SettingsItemButton(
                    title = stringResource(R.string.settings_help_helpPagesTitle),
                    info = stringResource(R.string.settings_help_helpPagesInfo),
                    prefixIcon = painterResource(R.drawable.ic_article),
                    endIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_next),
                    isFirst = false,
                    isLast = false,
                    onClick = onNavigateToHelpPages
                )
                SettingsItemButton(
                    title = stringResource(R.string.settings_developmentTitle),
                    info = stringResource(R.string.settings_developmentInfo),
                    prefixIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_dev),
                    endIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_next),
                    isFirst = false,
                    isLast = true,
                    onClick = onNavigateToDevSettings
                )
            }

            //About:
            item {
                Headline(
                    title = stringResource(R.string.settings_about),
                    indentation = HeadlineIndentation.PrefixIconLevel
                )
                val privacyPolicyPage: LegalPage? = viewModel.privacyPolicyPage
                if (privacyPolicyPage != null) {
                    SettingsItemButton(
                        title = stringResource(R.string.legal_privacyPolicy_title),
                        info = stringResource(R.string.legal_privacyPolicy_info),
                        prefixIcon = painterResource(R.drawable.ic_privacy),
                        endIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_external),
                        isFirst = true,
                        isLast = false,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, privacyPolicyPage.pageUrl)
                            context.startActivity(intent)
                        }
                    )
                }
                val termsOfServicesPage: LegalPage? = viewModel.termsOfServicesPage
                if (termsOfServicesPage != null) {
                    SettingsItemButton(
                        title = stringResource(R.string.legal_termsOfService_title),
                        info = stringResource(R.string.legal_termsOfService_info),
                        prefixIcon = painterResource(R.drawable.ic_tos),
                        endIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_external),
                        isFirst = false,
                        isLast = false,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, termsOfServicesPage.pageUrl)
                            context.startActivity(intent)
                        }
                    )
                }
                SettingsItemButton(
                    title = stringResource(R.string.settings_about_repositoryTitle),
                    info = stringResource(R.string.settings_about_repositoryInfo),
                    prefixIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_dev),
                    endIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_external),
                    isFirst = false,
                    isLast = false,
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, "https://github.com/Christian-2003/password-vault".toUri())
                        context.startActivity(intent)
                    }
                )
                SettingsItemButton(
                    title = stringResource(R.string.settings_about_issuesTitle),
                    info = stringResource(R.string.settings_about_issuesInfo),
                    prefixIcon = painterResource(R.drawable.ic_bug),
                    endIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_external),
                    isFirst = false,
                    isLast = false,
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, "https://github.com/Christian-2003/password-vault/issues".toUri())
                        context.startActivity(intent)
                    }
                )
                SettingsItemButton(
                    title = stringResource(R.string.settings_about_systemTitle),
                    info = stringResource(R.string.settings_about_systemInfo),
                    prefixIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_android),
                    endIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_external),
                    isFirst = false,
                    isLast = true,
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", context.packageName, null)
                        intent.setData(uri)
                        context.startActivity(intent)
                    }
                )
            }

            //Other software to advertise:
            item {
                OtherSoftwareSection(
                    otherSoftware = viewModel.otherSoftware,
                    imageLoader = viewModel.imageLoader,
                    onClick = { software ->
                        val intent = Intent(Intent.ACTION_VIEW, software.websiteUrl)
                        context.startActivity(intent)
                    },
                    onQueryImageRequest = { url -> viewModel.requestImage(url) }
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


@Composable
private fun OtherSoftwareSection(
    otherSoftware: List<OtherSoftware>,
    imageLoader: ImageLoader,
    onClick: (OtherSoftware) -> Unit,
    onQueryImageRequest: (Uri) -> ImageRequest
) {
    Headline(
        title = stringResource(R.string.settings_otherSoftware),
        indentation = HeadlineIndentation.PrefixIconLevel
    )
    otherSoftware.forEachIndexed { index, software ->
        ListItemContainer(
            isFirst = index == 0,
            isLast = index == otherSoftware.size - 1
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onClick(software)
                    }
                    .padding(
                        horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal),
                        vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = onQueryImageRequest(software.iconUrl),
                    imageLoader = imageLoader,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(end = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
                        .size(dimensionResource(de.christian2003.core.ui.R.dimen.image_xs))
                )
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = software.softwareName,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Icon(
                            painter = painterResource(de.christian2003.core.ui.R.drawable.ic_external),
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = "",
                            modifier = Modifier
                                .padding(start = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal) / 2)
                                .size(dimensionResource(de.christian2003.core.ui.R.dimen.image_xxs))
                        )
                    }

                    val scheme: String? = software.websiteUrl.scheme
                    val host: String? = software.websiteUrl.host
                    if (!scheme.isNullOrBlank() && !host.isNullOrBlank()) {
                        Text(
                            text = "$scheme://$host",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
