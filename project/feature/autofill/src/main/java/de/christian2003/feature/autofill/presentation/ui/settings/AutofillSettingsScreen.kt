package de.christian2003.feature.autofill.presentation.ui.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import de.christian2003.core.ui.composables.NavigationBarProtection
import de.christian2003.feature.autofill.presentation.viewmodels.AutofillSettingsViewModel
import de.christian2003.feature.autofill.R
import androidx.core.net.toUri
import de.christian2003.core.ui.composables.Headline
import de.christian2003.core.ui.composables.settings.LargeSettingsSwitch
import de.christian2003.core.ui.composables.settings.SettingsItemSwitch


/**
 * Screen displays autofill settings.
 *
 * @param viewModel     View model.
 * @param onNavigateUp  Callback invoked to navigate up the navigation stack.
 */
@Composable
internal fun AutofillSettingsScreen(
    viewModel: AutofillSettingsViewModel,
    onNavigateUp: () -> Unit
) {
    val context: Context = LocalContext.current
    val selectServiceLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            //Service was selected:
            viewModel.setIsAutofillEnabled(true)
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                onNavigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        val bottomPadding: Dp = innerPadding.calculateBottomPadding()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                )
                .verticalScroll(rememberScrollState())
        ) {
            LargeSettingsSwitch(
                label = stringResource(R.string.autofillSettings_enabledLabel),
                checked = viewModel.isAutofillEnabled && viewModel.isAutofillSelected,
                onCheckedChange = {
                    if (it) {
                        //Enable:
                        if (viewModel.isAutofillSelected) {
                            //Service is already selected:
                            viewModel.setIsAutofillEnabled(true)
                        }
                        else {
                            //Service needs to be selected:
                            val uri: Uri = "package: ${context.packageName}".toUri()
                            val intent = Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE, uri)
                            try {
                                selectServiceLauncher.launch(intent)
                            } catch (_: Exception) { }
                        }
                    }
                    else {
                        //Disable:
                        viewModel.setIsAutofillEnabled(false)
                    }
                },
                modifier = Modifier.padding(horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal))
            )

            Headline(stringResource(R.string.autofillSettings_furtherSettings_title))

            SettingsItemSwitch(
                title = stringResource(R.string.autofillSettings_furtherSettings_geocoderTitle),
                info = stringResource(R.string.autofillSettings_furtherSettings_geocoderInfo),
                checked = viewModel.isGeocoderEnabled,
                onCheckedChange = {
                    viewModel.setIsGeocoderEnabled(it)
                },
                isFirst = true,
                isLast = true,
                isEnabled = viewModel.isAutofillEnabled && viewModel.isAutofillSelected
            )

            Box(
                modifier = Modifier.height(bottomPadding)
            )
        }

        NavigationBarProtection(bottomPadding)
    }
}


/**
 * Top app bar for the screen.
 *
 * @param onNavigateUp  Callback invoked to navigate up the navigation stack.
 */
@Composable
private fun TopBar(
    onNavigateUp: () -> Unit
) {
    TopAppBar(
        title = {
            Text(stringResource(R.string.autofillSettings_title))
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
