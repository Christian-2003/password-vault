package de.christian2003.passwordvault.presentation.ui.licenses

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import de.christian2003.core.ui.composables.HelpCard
import de.christian2003.core.ui.composables.ListItemContainer
import de.christian2003.core.ui.composables.NavigationBarProtection
import de.christian2003.core.ui.composables.Shape
import de.christian2003.passwordvault.R
import de.christian2003.passwordvault.presentation.models.licenses.LicenseUiDto
import de.christian2003.passwordvault.presentation.viewmodels.LicensesViewModel


/**
 * Screen through which users can see all software used by Password Vault.
 *
 * @param viewModel     View model.
 * @param onNavigateUp  Callback to navigate up the nav stack.
 */
@Composable
internal fun LicensesScreen(
    viewModel: LicensesViewModel,
    onNavigateUp: () -> Unit
) {
    val licenses: List<LicenseUiDto>? = viewModel.licenses

    Scaffold(
        topBar = {
            TopBar(
                onNavigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        val bottomPadding: Dp = innerPadding.calculateBottomPadding()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                )
        ) {
            item {
                AnimatedVisibility(viewModel.isHelpCardVisible) {
                    HelpCard(
                        text = stringResource(R.string.licenses_help),
                        onDismiss = {
                            viewModel.dismissHelpCard()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                                top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical),
                                end = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal)
                            )
                    )
                }
                Box(modifier = Modifier.height(dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)))
            }
            if (licenses != null) {
                itemsIndexed(licenses) { index, license ->
                    LicenseListItem(
                        license = license,
                        isFirst = index == 0,
                        isLast = index == licenses.size - 1,
                        onClick = {
                            viewModel.showLicense(it)
                        }
                    )
                }
            }
            item {
                Box(modifier = Modifier.height(bottomPadding))
            }
        }

        NavigationBarProtection(bottomPadding)
    }

    val displayedLicense: LicenseUiDto? = viewModel.displayedLicense
    val displayedLicenseText: String? = viewModel.displayedLicenseText
    if (displayedLicense != null && !displayedLicenseText.isNullOrBlank()) {
        LicenseDialog(
            softwareName = displayedLicense.displayName,
            licenseText = displayedLicenseText,
            onDismiss = {
                viewModel.showLicense(null)
            }
        )
    }
}


/**
 * Single license displayed in a list.
 *
 * @param license   License to display.
 * @param isFirst   Whether this is the first license in the list.
 * @param isLast    Whether this is the last license in the list.
 * @param onClick   Callback when the license is clicked.
 */
@Composable
private fun LicenseListItem(
    license: LicenseUiDto,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: (LicenseUiDto) -> Unit
) {
    ListItemContainer(
        isFirst = isFirst,
        isLast = isLast
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick(license)
                }
                .padding(
                    horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal),
                    vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                )
        ) {
            Shape(
                shape = MaterialShapes.Cookie12Sided,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .padding(end = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
                    .size(dimensionResource(de.christian2003.core.ui.R.dimen.image_m))
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_license),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = "",
                    modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_xs))
                )
            }
            Column {
                Text(
                    text = license.displayName,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${license.artifactGroup} » ${license.artifactName}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


/**
 * Top bar for the screen.
 *
 * @param onNavigateUp  Callback to navigate up the nav stack.
 */
@Composable
private fun TopBar(
    onNavigateUp: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.licenses_title),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
