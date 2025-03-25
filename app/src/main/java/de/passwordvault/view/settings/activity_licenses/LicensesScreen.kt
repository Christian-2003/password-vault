package de.passwordvault.view.settings.activity_licenses

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import de.passwordvault.R
import de.passwordvault.model.licenses.License
import de.passwordvault.ui.composables.BottomSheetDialog


/**
 * View displays a list of all licenses used by the app.
 *
 * @param viewModel     View model for the view.
 * @param onNavigateUp  Callback invoked to navigate up on the navigation stack.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicensesScreen(
    viewModel: LicensesViewModel,
    onNavigateUp: () -> Unit
) {
    viewModel.loadLicenses()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_about_usage_dependencies),
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
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator()
            }
            else {
                LicensesList(
                    licenses = viewModel.licenses,
                    onLicenseClicked = { license ->
                        viewModel.loadLicenseText(license)
                    }
                )
            }

            if (viewModel.displayedLicenseName != null && viewModel.displayedLicenseText != null) {
                LicenseDialog(
                    licenseName = viewModel.displayedLicenseName!!,
                    licenseText = viewModel.displayedLicenseText!!,
                    onDismiss = {
                        viewModel.displayedLicenseName = null
                        viewModel.displayedLicenseText = null
                    }
                )
            }
        }
    }
}


/**
 * Composable displays a list of all licenses used.
 *
 * @param licenses          List of licenses to display.
 * @param onLicenseClicked  Callback invoked once a license is clicked.
 */
@Composable
private fun LicensesList(
    licenses: List<License>,
    onLicenseClicked: (License) -> Unit
) {
    LazyColumn {
        items(licenses) { license ->
            LicensesListRow(
                license = license,
                onLicenseClicked = onLicenseClicked
            )
        }
    }
}


/**
 * Composable displays a single license that is used by the app.
 *
 * @param license           License to display.
 * @param onLicenseClicked  Callback invoked once the license is clicked.
 */
@Composable
private fun LicensesListRow(
    license: License,
    onLicenseClicked: (License) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onLicenseClicked(license)
            }
            .padding(
                vertical = dimensionResource(R.dimen.space_vertical),
                horizontal = dimensionResource(R.dimen.space_horizontal)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = license.softwareName,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = license.licenseName,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Text(
            text = license.softwareVersion,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


/**
 * Composable displays a dialog through which a license can be displayed.
 *
 * @param licenseName       Name of the license displayed.
 * @param licenseText       Text of the license to display.
 * @param onDismiss         Callback to close the dialog without deleting the petrol entry.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LicenseDialog(
    licenseName: String,
    licenseText: String,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    BottomSheetDialog(
        sheetState = sheetState,
        title = licenseName,
        onDismiss = onDismiss,
        icon = painterResource(R.drawable.ic_license)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = dimensionResource(R.dimen.space_horizontal),
                    vertical = dimensionResource(R.dimen.space_vertical)
                ),
            text = licenseText,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
