package de.christian2003.passwordvault.plugin.presentation.view.settings

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import coil.compose.rememberAsyncImagePainter
import de.christian2003.passwordvault.R
import de.christian2003.passwordvault.plugin.presentation.ui.composables.Headline
import java.time.LocalDate


@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateUp: () -> Unit,
    onNavigateToHelp: () -> Unit
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
                            painter = painterResource(R.drawable.ic_back),
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

            //Help:
            item {
                HorizontalDivider()
                Headline(
                    title = stringResource(R.string.settings_help),
                    indentToPrefixIcon = true
                )
                SettingsItemButton(
                    title = stringResource(R.string.settings_help_helpMessagesTitle),
                    info = stringResource(R.string.settings_help_helpMessagesInfo),
                    prefixIcon = painterResource(R.drawable.ic_help_outlined),
                    endIcon = painterResource(R.drawable.ic_next),
                    onClick = onNavigateToHelp
                )
            }
        }
    }
}


/**
 * Composable displays an item button.
 *
 * @param title     Title for the setting.
 * @param info      Info for the setting.
 * @param onClick   Callback to invoke when the item button is clicked.
 */
@Composable
fun SettingsItemButton(
    title: String,
    info: String,
    onClick: () -> Unit,
    endIcon: Painter? = null,
    prefixIcon: Painter? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(
                vertical = dimensionResource(R.dimen.padding_vertical),
                horizontal = dimensionResource(R.dimen.margin_horizontal)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (prefixIcon != null) {
            Icon(
                painter = prefixIcon,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                contentDescription = "",
                modifier = Modifier
                    .padding(end = dimensionResource(R.dimen.padding_horizontal))
                    .size(dimensionResource(R.dimen.image_xs))
            )
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
                            .padding(start = dimensionResource(R.dimen.padding_horizontal) / 2)
                            .size(dimensionResource(R.dimen.image_xxs))
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
                horizontal = dimensionResource(R.dimen.margin_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical)
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
                .padding(start = dimensionResource(R.dimen.padding_horizontal))
        ) {
            Text(
                text = stringResource(R.string.app_name),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
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
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_vertical))
            )
        }
    }
}
