package de.christian2003.passwordvault.plugin.presentation.view.devsettings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import de.christian2003.passwordvault.R
import de.christian2003.passwordvault.plugin.presentation.view.settings.SettingsItemButton
import de.christian2003.passwordvault.plugin.presentation.view.settings.SettingsItemSwitch


/**
 * Settings for developers.
 *
 * @param viewModel                     View model.
 * @param onNavigateUp                  Callback invoked to navigate up the navigation stack.
 * @param onEnableScreenshotsChanged    Callback invoked to inform the host activity that the flag
 *                                      indicating whether screenshots on sensitive screens are
 *                                      enabled has changed.
 */
@Composable
fun DevSettingsScreen(
    viewModel: DevSettingsViewModel,
    onNavigateUp: () -> Unit,
    onEnableScreenshotsChanged: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.settings_dev_title))
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
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                SettingsItemButton(
                    title = stringResource(R.string.settings_dev_deleteMasterPasswordTitle),
                    info = stringResource(R.string.settings_dev_deleteMasterPasswordInfo),
                    prefixIcon = painterResource(R.drawable.ic_password),
                    onClick = {
                        viewModel.deleteMasterPassword()
                    }
                )
                SettingsItemSwitch(
                    title = stringResource(R.string.settings_dev_skipBiometricsTitle),
                    info = stringResource(R.string.settings_dev_skipBiometricsInfo),
                    prefixIcon = painterResource(R.drawable.ic_biometrics),
                    checked = viewModel.isSkipBiometrics,
                    onCheckedChange = {
                        viewModel.setIsSkipBiometrics(it)
                    }
                )
                SettingsItemSwitch(
                    title = stringResource(R.string.settings_dev_enableScreenshotsTitle),
                    info = stringResource(R.string.settings_dev_enableScreenshotsInfo),
                    prefixIcon = painterResource(R.drawable.ic_dev),
                    checked = viewModel.isEnableScreenshots,
                    onCheckedChange = {
                        viewModel.setIsEnableScreenshots(it)
                        onEnableScreenshotsChanged()
                    }
                )
            }
        }
    }
}
