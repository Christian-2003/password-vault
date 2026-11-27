package de.christian2003.passwordvault.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import de.christian2003.core.ui.theme.ThemeContrast
import de.christian2003.feature.accounts.navigation.AccountDestination
import de.christian2003.feature.accounts.navigation.AccountsDestination
import de.christian2003.feature.accounts.navigation.accountsDestination
import de.christian2003.feature.analysis.navigation.AnalysisDestination
import de.christian2003.feature.auth.navigation.AuthSettingsFlow
import de.christian2003.feature.autofill.navigation.AutofillSettingsFlow
import de.christian2003.feature.export.navigation.ExportsFlow
import de.christian2003.feature.export.navigation.ImportsFlow
import de.christian2003.feature.search.navigation.SearchDestination
import de.christian2003.passwordvault.presentation.ui.devsettings.DevSettingsScreen
import de.christian2003.passwordvault.presentation.ui.helpmessages.HelpMessagesScreen
import de.christian2003.passwordvault.presentation.ui.helppages.HelpPagesScreen
import de.christian2003.passwordvault.presentation.ui.home.HomeView
import de.christian2003.passwordvault.presentation.ui.legal.LegalScreen
import de.christian2003.passwordvault.presentation.ui.licenses.LicensesScreen
import de.christian2003.passwordvault.presentation.ui.main.MainScreen
import de.christian2003.passwordvault.presentation.ui.settings.SettingsScreen
import de.christian2003.passwordvault.presentation.viewmodels.DevSettingsViewModel
import de.christian2003.passwordvault.presentation.viewmodels.HelpMessagesViewModel
import de.christian2003.passwordvault.presentation.viewmodels.HelpPagesViewModel
import de.christian2003.passwordvault.presentation.viewmodels.HomeViewModel
import de.christian2003.passwordvault.presentation.viewmodels.LegalViewModel
import de.christian2003.passwordvault.presentation.viewmodels.LicensesViewModel
import de.christian2003.passwordvault.presentation.viewmodels.MainViewModel
import de.christian2003.passwordvault.presentation.viewmodels.SettingsViewModel
import kotlinx.serialization.Serializable
import java.io.Serial


// =============== Internal destinations ===============

@Serializable
internal object MainFlow

@Serializable
internal object SettingsFlow

@Serializable
internal object LegalDestination


internal fun NavGraphBuilder.mainFlow(
    mainViewModel: MainViewModel,
    navController: NavController
) {
    val onNavigateTo: (Int) -> Unit = { index ->
        if (index != mainViewModel.selectedDestination) {
            when (index) {
                0 -> {
                    mainViewModel.selectedDestination = 0
                    navController.navigate(HomeDestination) {
                        popUpTo<HomeDestination>()
                        launchSingleTop = true
                        restoreState = true
                    }

                }
                1 -> {
                    mainViewModel.selectedDestination = 1
                    navController.navigate(AccountsDestination){
                        popUpTo<HomeDestination>()
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    }

    val viewWrapper: @Composable (
        content: @Composable (innerPadding: PaddingValues) -> Unit
    ) -> Unit = @Composable { content ->
        MainScreen(
            viewModel = mainViewModel,
            onNavigateTo = onNavigateTo,
            onNavigateToSearch = {
                navController.navigate(SearchDestination)
            },
            onNavigateToSettings = {
                navController.navigate(SettingsFlow)
            },
            onCreateNewAccount = {
                navController.navigate(AccountDestination(null))
            },
            content = content
        )
    }


    navigation<MainFlow>(
        startDestination = HomeDestination
    ) {

        //Home:
        homeDestination(
            viewWrapper = viewWrapper
        )

        //List of accounts destination:
        accountsDestination(
            onEditAccount = { accountId ->
                navController.navigate(AccountDestination(accountId.toString()))
            },
            onCreateNewAccount = {
                navController.navigate(AccountDestination(null))
            },
            viewWrapper = viewWrapper
        )

    }
}


internal fun NavGraphBuilder.settingsFlow(
    navController: NavController,
    onUseGlobalThemeChange: (Boolean) -> Unit,
    onThemeContrastChange: (ThemeContrast) -> Unit,
    onEnableScreenshotsChanged: () -> Unit
) {
    navigation<SettingsFlow>(
        startDestination = SettingsDestination
    ) {

        //Settings screen:
        settingsDestination(
            onNavigateUp = {
                navController.navigateUp()
            },
            onNavigateToHelpMessages = {
                navController.navigate(HelpMessagesDestination)
            },
            onNavigateToHelpPages = {
                navController.navigate(HelpPagesDestination)
            },
            onNavigateToAuthSettings = {
                navController.navigate(AuthSettingsFlow)
            },
            onNavigateToAutofillSettings = {
                navController.navigate(AutofillSettingsFlow)
            },
            onNavigateToExport = {
                navController.navigate(ExportsFlow)
            },
            onNavigateToImport = {
                navController.navigate(ImportsFlow)
            },
            onNavigateToDevSettings = {
                navController.navigate(DevSettingsDestination)
            },
            onNavigateToLicenses = {
                navController.navigate(LicensesDestination)
            },
            onUseGlobalThemeChange = onUseGlobalThemeChange,
            onThemeContrastChange = onThemeContrastChange
        )

        //Developer settings screen:
        devSettingsDestination(
            onNavigateUp = {
                navController.navigateUp()
            },
            onEnableScreenshotsChanged = onEnableScreenshotsChanged
        )

        //Help messages screen:
        helpMessagesDestination(
            onNavigateUp = {
                navController.navigateUp()
            }
        )

        //Help pages screen:
        helpPagesDestination(
            onNavigateUp = {
                navController.navigateUp()
            }
        )

        //List of used software:
        licensesDestination(
            onNavigateUp = {
                navController.navigateUp()
            }
        )

    }
}


internal fun NavGraphBuilder.legalDestination(
    onContinue: () -> Unit,
    onCancel: () -> Unit
) {
    composable<LegalDestination> {
        val viewModel: LegalViewModel = hiltViewModel()

        LegalScreen(
            viewModel = viewModel,
            onContinue = onContinue,
            onCancel = onCancel
        )
    }
}


// =============== Private destinations ===============

@Serializable
private object HomeDestination

@Serializable
private object SettingsDestination

@Serializable
private object DevSettingsDestination

@Serializable
private object HelpMessagesDestination

@Serializable
private object HelpPagesDestination

@Serializable
private object LicensesDestination



private fun NavGraphBuilder.homeDestination(
    viewWrapper: @Composable (
        content: @Composable (innerPadding: PaddingValues) -> Unit
    ) -> Unit
) {
    composable<HomeDestination> {
        val viewModel: HomeViewModel = hiltViewModel()

        viewWrapper { innerPadding ->
            HomeView(
                viewModel = viewModel,
                innerPadding = innerPadding
            )
        }
    }
}


private fun NavGraphBuilder.settingsDestination(
    onNavigateUp: () -> Unit,
    onNavigateToHelpMessages: () -> Unit,
    onNavigateToHelpPages: () -> Unit,
    onNavigateToAuthSettings: () -> Unit,
    onNavigateToAutofillSettings: () -> Unit,
    onNavigateToExport: () -> Unit,
    onNavigateToImport: () -> Unit,
    onNavigateToDevSettings: () -> Unit,
    onNavigateToLicenses: () -> Unit,
    onUseGlobalThemeChange: (Boolean) -> Unit,
    onThemeContrastChange: (ThemeContrast) -> Unit
) {
    composable<SettingsDestination> {
        val viewModel: SettingsViewModel = hiltViewModel()

        SettingsScreen(
            viewModel = viewModel,
            onNavigateUp = onNavigateUp,
            onNavigateToHelpMessages = onNavigateToHelpMessages,
            onNavigateToHelpPages = onNavigateToHelpPages,
            onNavigateToAuthSettings = onNavigateToAuthSettings,
            onNavigateToAutofillSettings = onNavigateToAutofillSettings,
            onNavigateToExport = onNavigateToExport,
            onNavigateToImport = onNavigateToImport,
            onNavigateToDevSettings = onNavigateToDevSettings,
            onNavigateToLicenses = onNavigateToLicenses,
            onUseGlobalThemeChange = onUseGlobalThemeChange,
            onThemeContrastChange = onThemeContrastChange
        )
    }
}


private fun NavGraphBuilder.devSettingsDestination(
    onNavigateUp: () -> Unit,
    onEnableScreenshotsChanged: () -> Unit
) {
    composable<DevSettingsDestination> {
        val viewModel: DevSettingsViewModel = hiltViewModel()

        DevSettingsScreen(
            viewModel = viewModel,
            onNavigateUp = onNavigateUp,
            onEnableScreenshotsChanged = onEnableScreenshotsChanged
        )
    }
}


private fun NavGraphBuilder.helpMessagesDestination(
    onNavigateUp: () -> Unit
) {
    composable<HelpMessagesDestination> {
        val viewModel: HelpMessagesViewModel = hiltViewModel()

        HelpMessagesScreen(
            viewModel = viewModel,
            onNavigateUp = onNavigateUp
        )
    }
}


private fun NavGraphBuilder.helpPagesDestination(
    onNavigateUp: () -> Unit
) {
    composable<HelpPagesDestination> {
        val viewModel: HelpPagesViewModel = hiltViewModel()

        HelpPagesScreen(
            viewModel = viewModel,
            onNavigateUp = onNavigateUp
        )
    }
}


private fun NavGraphBuilder.licensesDestination(
    onNavigateUp: () -> Unit
) {
    composable<LicensesDestination> {
        val viewModel: LicensesViewModel = hiltViewModel()

        LicensesScreen(
            viewModel = viewModel,
            onNavigateUp = onNavigateUp
        )
    }
}

