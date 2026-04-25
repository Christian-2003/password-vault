package de.christian2003.passwordvault

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import de.christian2003.feature.auth.navigation.AuthSettingsFlow
import de.christian2003.feature.auth.navigation.LoginDestination
import de.christian2003.feature.auth.navigation.RecoveryFlow
import de.christian2003.feature.auth.navigation.SetupFlow
import de.christian2003.feature.auth.navigation.authSettingsFlow
import de.christian2003.feature.auth.navigation.loginDestination
import de.christian2003.feature.auth.navigation.recoveryFlow
import de.christian2003.feature.auth.navigation.setupFlow
import de.christian2003.passwordvault.ui.devsettings.DevSettingsScreen
import de.christian2003.passwordvault.viewmodels.DevSettingsViewModel
import de.christian2003.passwordvault.ui.help.HelpScreen
import de.christian2003.passwordvault.viewmodels.HelpViewModel
import de.christian2003.passwordvault.ui.settings.SettingsScreen
import de.christian2003.passwordvault.viewmodels.SettingsViewModel
import de.christian2003.core.security.application.usecases.BiometricAuthUseCase
import de.christian2003.core.security.application.usecases.CanMasterKeyBeUnlockedUseCase
import de.christian2003.core.security.application.usecases.UnlockWithBiometricsUseCase
import de.christian2003.core.ui.theme.PasswordVaultTheme
import de.christian2003.core.ui.theme.ThemeContrast
import de.christian2003.data.files.infrastructure.registerSharedFilesClearingWorker
import de.christian2003.feature.accounts.navigation.AccountDestination
import de.christian2003.feature.accounts.navigation.AccountsDestination
import de.christian2003.feature.accounts.navigation.accountDestination
import de.christian2003.feature.accounts.navigation.accountsDestination
import de.christian2003.feature.analysis.navigation.AnalysisDestination
import de.christian2003.feature.analysis.navigation.analysisDestination
import de.christian2003.feature.autofill.navigation.AutofillSettingsFlow
import de.christian2003.feature.autofill.navigation.autofillSettingsFlow
import de.christian2003.feature.files.navigation.DirectoriesFlow
import de.christian2003.feature.files.navigation.directoriesFlow
import de.christian2003.feature.search.navigation.SearchDestination
import de.christian2003.feature.search.navigation.searchDestination
import javax.inject.Inject


//Require FragmentActivity instead of ComponentActivity in order to host a biometric prompt
//Activity is created by Android, Hilt cannot inject into constructor
@AndroidEntryPoint
class MainActivity : FragmentActivity() {

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

        //Work manager:
        Handler(Looper.getMainLooper()).postDelayed(
            {
                WorkManager.getInstance(this).registerSharedFilesClearingWorker()
            },
            1000
        )


        //App content:
        enableEdgeToEdge(
            navigationBarStyle = if (isNightMode()) {
                SystemBarStyle.dark(
                    scrim = Color.TRANSPARENT
                )
            } else {
                SystemBarStyle.light(
                    scrim = Color.TRANSPARENT,
                    darkScrim = Color.TRANSPARENT
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
 */
@Composable
fun PasswordVault(
    canUnlockMasterKey: Boolean,
    onBiometricUnlock: suspend () -> Boolean,
    onBiometricAuth: suspend () -> Boolean,
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
                    navController.navigate(AccountsDestination) {
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

            //List of accounts destination:
            accountsDestination(
                onEditAccount = { accountId ->
                    navController.navigate(AccountDestination(accountId.toString()))
                },
                onCreateNewAccount = {
                    navController.navigate(AccountDestination(null))
                },
                onNavigateToSettings = {
                    navController.navigate("settings")
                },
                onNavigateToSearch = {
                    navController.navigate(SearchDestination)
                },
                onNavigateToFiles = {
                    navController.navigate(DirectoriesFlow)
                },
                onNavigateToAnalysis = {
                    navController.navigate(AnalysisDestination)
                }
            )

            //Create, edit or delete accounts:
            accountDestination(
                onNavigateUp = {
                    navController.navigateUp()
                }
            )

            //Flow for the autofill settings:
            autofillSettingsFlow(
                navController = navController
            )

            //Flow for the in-app search:
            searchDestination(
                onNavigateUp = {
                    navController.navigateUp()
                },
                onNavigateToAccount = { accountId ->
                    navController.navigate(AccountDestination(accountId.toString()))
                }
            )

            //Flow for the files:
            directoriesFlow(
                navController = navController
            )

            //Destination for the password security analysis:
            analysisDestination(
                onNavigateUp = {
                    navController.navigateUp()
                },
                onNavigateToAccount = { accountId ->
                    navController.navigate(AccountDestination(accountId.toString()))
                }
            )


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
                    onNavigateToAutofillSettings = {
                        navController.navigate(AutofillSettingsFlow)
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
