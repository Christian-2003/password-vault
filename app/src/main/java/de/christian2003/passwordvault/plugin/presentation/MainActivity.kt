package de.christian2003.passwordvault.plugin.presentation

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import de.christian2003.auth.navigation.AuthSettingsFlow
import de.christian2003.auth.navigation.LoginDestination
import de.christian2003.auth.navigation.RecoveryFlow
import de.christian2003.auth.navigation.SetupFlow
import de.christian2003.auth.navigation.authSettingsFlow
import de.christian2003.auth.navigation.loginDestination
import de.christian2003.auth.navigation.recoveryFlow
import de.christian2003.auth.navigation.setupFlow
import de.christian2003.passwordvault.application.usecases.auth.ToggleBiometricsUseCase
import de.christian2003.passwordvault.plugin.presentation.ui.theme.PasswordVaultTheme
import de.christian2003.passwordvault.plugin.presentation.ui.theme.ThemeContrast
import de.christian2003.passwordvault.plugin.presentation.view.account.AccountScreen
import de.christian2003.passwordvault.plugin.presentation.view.account.AccountViewModel
import de.christian2003.passwordvault.plugin.presentation.view.devsettings.DevSettingsScreen
import de.christian2003.passwordvault.plugin.presentation.view.devsettings.DevSettingsViewModel
import de.christian2003.passwordvault.plugin.presentation.view.help.HelpScreen
import de.christian2003.passwordvault.plugin.presentation.view.help.HelpViewModel
import de.christian2003.passwordvault.plugin.presentation.view.main.MainScreen
import de.christian2003.passwordvault.plugin.presentation.view.main.MainViewModel
import de.christian2003.passwordvault.plugin.presentation.view.settings.SettingsScreen
import de.christian2003.passwordvault.plugin.presentation.view.settings.SettingsViewModel
import de.christian2003.security.application.usecases.BiometricAuthUseCase
import de.christian2003.security.application.usecases.CanMasterKeyBeUnlockedUseCase
import de.christian2003.security.application.usecases.UnlockWithBiometricsUseCase
import javax.inject.Inject


//Require FragmentActivity instead of ComponentActivity in order to host a biometric prompt
//Activity is created by Android, Hilt cannot inject into constructor
@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Deprecated("Use :core:security use case instead")
    @Inject lateinit var biometricAuthUseCaseDeprecated: BiometricAuthUseCase
    @Deprecated("Use :core:security use case instead")
    @Inject lateinit var toggleBiometricsUseCase: ToggleBiometricsUseCase


    @Inject lateinit var canMasterKeyBeUnlockedUseCase: CanMasterKeyBeUnlockedUseCase
    @Inject lateinit var unlockWithBiometricsUseCase: UnlockWithBiometricsUseCase
    @Inject lateinit var biometricAuthUseCase: BiometricAuthUseCase


    /**
     * Creates the activity.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //App content:
        enableEdgeToEdge(
            navigationBarStyle = if (isNightMode()) {
                SystemBarStyle.dark(
                    scrim = android.graphics.Color.TRANSPARENT
                )
            } else {
                SystemBarStyle.light(
                    scrim = android.graphics.Color.TRANSPARENT,
                    darkScrim = android.graphics.Color.TRANSPARENT
                )
            }
        )
        setContent {
            PasswordVault(
                canUnlockMasterKey = canMasterKeyBeUnlockedUseCase.canBeUnlocked(),
                onBiometricUnlock = {
                    try {
                        unlockWithBiometricsUseCase.unlock()
                    } catch (_: Exception) {
                        false
                    }
                },
                onBiometricAuth = {
                    biometricAuthUseCase.authenticate()
                },
                onToggleBiometrics = {
                    toggleBiometricsUseCase.toggleBiometrics()
                }
            )
        }
    }


    /**
     * Determines whether the system is in night or day mode.
     *
     * @return  Whether the system is night or day mode.
     */
    private fun isNightMode(): Boolean {
        val currentMode: Int = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentMode == Configuration.UI_MODE_NIGHT_YES
    }

}


/**
 * First layer composable which encompasses the entire application.
 *
 * @param canUnlockMasterKey    Whether the app can unlock the master key.
 * @param onBiometricAuth       Callback invoked to perform biometric auth.
 * @param onToggleBiometrics    Callback invoke to enable / disable biometric auth.
 */
@Composable
fun PasswordVault(
    canUnlockMasterKey: Boolean,
    onBiometricUnlock: suspend () -> Boolean,
    onBiometricAuth: suspend () -> Boolean,
    onToggleBiometrics: suspend () -> Boolean
) {
    val activity: FragmentActivity = LocalActivity.current as FragmentActivity
    val preferences: SharedPreferences = activity.getSharedPreferences("settings", Context.MODE_PRIVATE)
    var enableScreenshots: Boolean by rememberSaveable { mutableStateOf(preferences.getBoolean("enable_screenshots", false)) }
    val applyFlags: (Boolean) -> Unit = { isSensitive ->
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        if (isSensitive && !enableScreenshots) {
            activity.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    val navController: NavHostController = rememberNavController()
    var isAuthSetupFinished: Boolean by rememberSaveable { mutableStateOf(canUnlockMasterKey) }
    var useGlobalTheme: Boolean by rememberSaveable { mutableStateOf(preferences.getBoolean("global_theme", false)) }
    var themeContrast: ThemeContrast by rememberSaveable { mutableStateOf(ThemeContrast.entries[preferences.getInt("theme_contrast", 0)]) }

    PasswordVaultTheme(
        dynamicColor = useGlobalTheme,
        contrast = themeContrast
    ) {
        NavHost(
            navController = navController,
            startDestination = if (isAuthSetupFinished) {
                LoginDestination
            } else {
                SetupFlow
            },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {

            //Flow for the first-time master password setup:
            setupFlow(
                navController = navController,
                onNotifyAuthSetupFinished = {
                    isAuthSetupFinished = true
                },
                onBiometricAuth = onBiometricAuth
            )

            //Flow for the recovery of the master password:
            recoveryFlow(
                navController = navController
            )

            //Flow for the authentication settings:
            authSettingsFlow(
                navController = navController,
                onBiometricAuth = onBiometricAuth
            )

            //Login screen:
            loginDestination(
                onContinue = {
                    navController.navigate("main") {
                        popUpTo<LoginDestination> {
                            inclusive = true
                        }
                    }
                },
                onNavigateToRecovery = {
                    navController.navigate(RecoveryFlow)
                },
                onBiometricUnlock = onBiometricUnlock
            )


            composable("main") {
                applyFlags(false)
                val viewModel: MainViewModel = hiltViewModel()
                MainScreen(
                    viewModel = viewModel,
                    onEditAccount = { id ->
                        val idAsString: String = id.toString()
                        navController.navigate("account/$idAsString")
                    },
                    onCreateNewAccount = {
                        navController.navigate("account/")
                    },
                    onNavigateToSettings = {
                        navController.navigate("settings")
                    }
                )
            }


            composable(
                route = "account/{accountId}",
                arguments = listOf(
                    navArgument("accountId") { type = NavType.StringType}
                )
            ) {
                applyFlags(true)
                val viewModel: AccountViewModel = hiltViewModel()
                AccountScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    }
                )
            }


            composable("settings") {
                applyFlags(false)
                val viewModel: SettingsViewModel = hiltViewModel()
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    },
                    onNavigateToHelp = {
                        navController.navigate("help")
                    },
                    onNavigateToAuthSettings = {
                        navController.navigate(AuthSettingsFlow)
                    },
                    onNavigateToDevSettings = {
                        navController.navigate("devSettings")
                    },
                    onUseGlobalThemeChange = {
                        useGlobalTheme = it
                    },
                    onThemeContrastChange = {
                        themeContrast = it
                    }
                )
            }


            composable("devSettings") {
                applyFlags(false)
                val viewModel: DevSettingsViewModel = hiltViewModel()
                DevSettingsScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    },
                    onEnableScreenshotsChanged = {
                        enableScreenshots = preferences.getBoolean("enable_screenshots", false)
                    }
                )
            }


            composable("help") {
                applyFlags(false)
                val viewModel: HelpViewModel = hiltViewModel()
                HelpScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}
