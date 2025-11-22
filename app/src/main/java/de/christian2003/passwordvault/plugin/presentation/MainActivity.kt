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
import androidx.compose.runtime.remember
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
import androidx.navigation.navigation
import dagger.hilt.android.AndroidEntryPoint
import de.christian2003.passwordvault.application.repository.AuthRepository
import de.christian2003.passwordvault.application.usecases.auth.BiometricAuthUseCase
import de.christian2003.passwordvault.application.usecases.auth.ToggleBiometricsUseCase
import de.christian2003.passwordvault.plugin.presentation.ui.theme.PasswordVaultTheme
import de.christian2003.passwordvault.plugin.presentation.ui.theme.ThemeContrast
import de.christian2003.passwordvault.plugin.presentation.view.account.AccountScreen
import de.christian2003.passwordvault.plugin.presentation.view.account.AccountViewModel
import de.christian2003.passwordvault.plugin.presentation.view.devsettings.DevSettingsScreen
import de.christian2003.passwordvault.plugin.presentation.view.devsettings.DevSettingsViewModel
import de.christian2003.passwordvault.plugin.presentation.view.help.HelpScreen
import de.christian2003.passwordvault.plugin.presentation.view.help.HelpViewModel
import de.christian2003.passwordvault.plugin.presentation.view.login.LoginScreen
import de.christian2003.passwordvault.plugin.presentation.view.login.LoginViewModel
import de.christian2003.passwordvault.plugin.presentation.view.main.MainScreen
import de.christian2003.passwordvault.plugin.presentation.view.main.MainViewModel
import de.christian2003.passwordvault.plugin.presentation.view.password.PasswordScreen
import de.christian2003.passwordvault.plugin.presentation.view.password.PasswordScreenFlow
import de.christian2003.passwordvault.plugin.presentation.view.password.PasswordViewModel
import de.christian2003.passwordvault.plugin.presentation.view.recovery.RecoveryScreen
import de.christian2003.passwordvault.plugin.presentation.view.recovery.RecoveryViewModel
import de.christian2003.passwordvault.plugin.presentation.view.recovery.SharedRecoveryViewModel
import de.christian2003.passwordvault.plugin.presentation.view.securityquestions.SecurityQuestionsScreen
import de.christian2003.passwordvault.plugin.presentation.view.securityquestions.SecurityQuestionsViewModel
import de.christian2003.passwordvault.plugin.presentation.view.settings.SettingsScreen
import de.christian2003.passwordvault.plugin.presentation.view.settings.SettingsViewModel
import javax.inject.Inject


//Require FragmentActivity instead of ComponentActivity in order to host a biometric prompt
@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    /**
     * Use case to perform biometric authentication. This use case is activity-scoped, so that
     * it can use an activity context. Therefore, the use case cannot be injected into view models
     * and must be injected into the activity itself.
     */
    @Inject lateinit var biometricAuthUseCase: BiometricAuthUseCase

    /**
     * Use case to enable / disable the biometric authentication. This use case is activity-scoped,
     * so that it can use an activity context. Therefore, the use case cannot be injected into view
     * models and must be injected into the activity itself.
     */
    @Inject lateinit var toggleBiometricsUseCase: ToggleBiometricsUseCase

    @Inject lateinit var authRepository: AuthRepository


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
                hasMasterPassword = authRepository.hasPassword(),
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
 * @param hasMasterPassword     Whether the app has a master password.
 * @param onBiometricAuth       Callback invoked to perform biometric auth.
 * @param onToggleBiometrics    Callback invoke to enable / disable biometric auth.
 */
@Composable
fun PasswordVault(
    hasMasterPassword: Boolean,
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
    var isAuthSetupFinished: Boolean by rememberSaveable { mutableStateOf(hasMasterPassword) }
    var useGlobalTheme: Boolean by rememberSaveable { mutableStateOf(preferences.getBoolean("global_theme", false)) }
    var themeContrast: ThemeContrast by rememberSaveable { mutableStateOf(ThemeContrast.entries[preferences.getInt("theme_contrast", 0)]) }

    PasswordVaultTheme(
        dynamicColor = useGlobalTheme,
        contrast = themeContrast
    ) {
        NavHost(
            navController = navController,
            startDestination = if (isAuthSetupFinished) {
                "login"
            } else {
                "setup_flow"
            },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
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
                    onToggleBiometrics = onToggleBiometrics,
                    onNavigateUp = {
                        navController.navigateUp()
                    },
                    onNavigateToHelp = {
                        navController.navigate("help")
                    },
                    onNavigateToPassword = {
                        navController.navigate("password")
                    },
                    onNavigateToDevSettings = {
                        navController.navigate("devSettings")
                    },
                    onNavigateToSecurityQuestions = {
                        navController.navigate("questions")
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


            composable("password") { backStackEntry ->
                applyFlags(true)
                val viewModel: PasswordViewModel = hiltViewModel()
                viewModel.flow = PasswordScreenFlow.None
                PasswordScreen(
                    viewModel = viewModel,
                    sharedViewModel = null,
                    onNavigateUp = {
                        navController.navigateUp()
                    },
                    onFinish = {
                        navController.navigateUp()
                    }
                )
            }


            composable("questions") {
                applyFlags(true)
                val viewModel: SecurityQuestionsViewModel = hiltViewModel()
                viewModel.isSetup = false
                SecurityQuestionsScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    },
                    onNavigateToNextSetupStep = {
                        navController.navigateUp()
                    }
                )
            }


            composable("login") {
                applyFlags(true)
                val viewModel: LoginViewModel = hiltViewModel()
                LoginScreen(
                    viewModel = viewModel,
                    onBiometricAuth = onBiometricAuth,
                    onFinish = {
                        navController.navigate("main") {
                            popUpTo("login") {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateToRecovery = {
                        navController.navigate("recovery")
                    }
                )
            }


            /**
             * Flow for the initial app setup.
             */
            navigation(
                route = "setup_flow",
                startDestination = "password"
            ) {
                composable("password") { backStackEntry ->
                    applyFlags(true)
                    val viewModel: PasswordViewModel = hiltViewModel()
                    viewModel.flow = PasswordScreenFlow.Setup
                    PasswordScreen(
                        viewModel = viewModel,
                        sharedViewModel = null,
                        onNavigateUp = {
                            navController.popBackStack("setup_flow", true)
                        },
                        onFinish = {
                            navController.navigate("questions")
                        }
                    )
                }

                composable("questions") { backStackEntry ->
                    applyFlags(true)
                    val viewModel: SecurityQuestionsViewModel = hiltViewModel()
                    viewModel.isSetup = true
                    SecurityQuestionsScreen(
                        viewModel = viewModel,
                        onNavigateUp = {
                            navController.navigateUp()
                        },
                        onNavigateToNextSetupStep = {
                            isAuthSetupFinished = true
                            navController.popBackStack("setup_flow", true)
                        }
                    )
                }
            }


            /**
             * Flow for the recovery of the master password.
             */
            navigation(
                route = "recovery_flow",
                startDestination = "recovery"
            ) {
                composable("recovery") { backStackEntry ->
                    applyFlags(true)
                    val viewModel: RecoveryViewModel = hiltViewModel()
                    val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("recovery_flow") }
                    val sharedViewModel: SharedRecoveryViewModel = hiltViewModel(parentEntry)
                    RecoveryScreen(
                        viewModel = viewModel,
                        sharedViewModel = sharedViewModel,
                        onNavigateUp = {
                            navController.popBackStack("recovery_flow", true)
                        },
                        onNavigateToChangePassword = {
                            navController.navigate("password")
                        }
                    )
                }

                composable("password") { backStackEntry ->
                    applyFlags(true)
                    val viewModel: PasswordViewModel = hiltViewModel()
                    viewModel.flow = PasswordScreenFlow.Recovery
                    val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("recovery_flow") }
                    val sharedViewModel: SharedRecoveryViewModel = hiltViewModel(parentEntry)
                    PasswordScreen(
                        viewModel = viewModel,
                        sharedViewModel = sharedViewModel,
                        onNavigateUp = {
                            navController.navigateUp()
                        },
                        onFinish = {
                            navController.popBackStack("recovery_flow", true)
                        }
                    )
                }
            }
        }
    }
}
