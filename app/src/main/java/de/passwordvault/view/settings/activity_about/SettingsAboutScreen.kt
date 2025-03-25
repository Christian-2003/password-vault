package de.passwordvault.view.settings.activity_about

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil.compose.rememberAsyncImagePainter
import de.passwordvault.BuildConfig
import de.passwordvault.R
import de.passwordvault.ui.composables.GenericTextButton
import de.passwordvault.ui.composables.Headline
import java.time.LocalDate


/**
 * Composable displays the screen containing settings about the app.
 *
 * @param viewModel             View model for the screen.
 * @param onNavigateUp          Callback invoked to navigate up on the navigation stack.
 * @param onNavigateToLicenses  Callback invoked to navigate to the app licenses.
 * @param onOpenUrl             Callback invoked to open a URL.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsAboutScreen(
    viewModel: SettingsAboutViewModel,
    onNavigateUp: () -> Unit,
    onNavigateToLicenses: () -> Unit,
    onOpenUrl: (String) -> Unit
) {
    val context: Context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_about),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onNavigateUp()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = ""
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            GeneralSection()
            HorizontalDivider()

            Headline(
                title = stringResource(R.string.settings_about_usage),
                indentToPrefixIcon = true
            )
            GenericTextButton(
                title = stringResource(R.string.settings_about_usage_dependencies),
                description = stringResource(R.string.settings_about_usage_dependencies_info),
                prefixIcon = painterResource(R.drawable.ic_license),
                suffixIcon = painterResource(R.drawable.ic_next),
                onClick = onNavigateToLicenses
            )
            AnimatedVisibility(viewModel.privacyPage != null) {
                GenericTextButton(
                    title = stringResource(R.string.settings_about_usage_privacypolicy),
                    description = stringResource(R.string.settings_about_usage_privacypolicy_info),
                    prefixIcon = painterResource(R.drawable.ic_privacy),
                    suffixIcon = painterResource(R.drawable.ic_external),
                    onClick = {
                        if (viewModel.privacyPage != null) {
                            onOpenUrl(viewModel.privacyPage!!.url)
                        }
                    }
                )
            }
            AnimatedVisibility(viewModel.tosPage != null) {
                GenericTextButton(
                    title = stringResource(R.string.settings_about_usage_tos),
                    description = stringResource(R.string.settings_about_usage_tos_info),
                    prefixIcon = painterResource(R.drawable.ic_legal),
                    suffixIcon = painterResource(R.drawable.ic_external),
                    onClick = {
                        if (viewModel.tosPage != null) {
                            onOpenUrl(viewModel.tosPage!!.url)
                        }
                    }
                )
            }
            HorizontalDivider()

            Headline(
                title = stringResource(R.string.settings_about_github),
                indentToPrefixIcon = true
            )
            GenericTextButton(
                title = stringResource(R.string.settings_about_github_repo),
                description = stringResource(R.string.settings_about_github_repo_info),
                prefixIcon = painterResource(R.drawable.ic_github),
                suffixIcon = painterResource(R.drawable.ic_external),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Christian-2003/password-vault"))
                    context.startActivity(intent)
                }
            )
            GenericTextButton(
                title = stringResource(R.string.settings_about_github_issues),
                description = stringResource(R.string.settings_about_github_issues_info),
                prefixIcon = painterResource(R.drawable.ic_bug),
                suffixIcon = painterResource(R.drawable.ic_external),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Christian-2003/password-vault/issues"))
                    context.startActivity(intent)
                }
            )
            HorizontalDivider()

            Headline(
                title = stringResource(R.string.settings_about_software),
                indentToPrefixIcon = true
            )
            GenericTextButton(
                title = stringResource(R.string.settings_about_software_more),
                description = stringResource(R.string.settings_about_software_more_info),
                prefixIcon = painterResource(R.drawable.ic_android),
                suffixIcon = painterResource(R.drawable.ic_external),
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", context.packageName, null)
                    intent.setData(uri)
                    context.startActivity(intent)
                }
            )
        }
    }
}


/**
 * Composable displays the general section of the screen which contains general info about the app.
 */
@Composable
private fun GeneralSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.space_horizontal),
                vertical = dimensionResource(R.dimen.space_vertical)
            )
    ) {
        Image(
            painter = rememberAsyncImagePainter(R.mipmap.ic_launcher),
            contentDescription = "",
            modifier = Modifier.size(dimensionResource(R.dimen.image_l))
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = dimensionResource(R.dimen.space_horizontal_between))
        ) {
            Text(
                text = stringResource(R.string.app_name),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = if (!BuildConfig.DEBUG) { BuildConfig.VERSION_NAME } else { BuildConfig.VERSION_NAME + " (Debug Build)" },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(R.string.settings_about_software_copyright).replace("{arg}", "" + LocalDate.now().year),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.space_vertical))
            )
        }
    }
}
