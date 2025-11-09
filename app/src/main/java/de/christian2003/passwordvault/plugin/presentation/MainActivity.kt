package de.christian2003.passwordvault.plugin.presentation

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.SystemBarStyle
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
import de.christian2003.passwordvault.application.repository.AuthRepository
import de.christian2003.passwordvault.application.usecases.auth.BiometricAuthUseCase
import de.christian2003.passwordvault.application.usecases.auth.ToggleBiometricsUseCase
import de.christian2003.passwordvault.plugin.presentation.ui.theme.PasswordVaultTheme
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
import de.christian2003.passwordvault.plugin.presentation.view.password.PasswordViewModel
import de.christian2003.passwordvault.plugin.presentation.view.recovery.RecoveryScreen
import de.christian2003.passwordvault.plugin.presentation.view.recovery.RecoveryViewModel
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
    val navController: NavHostController = rememberNavController()
    var isAuthSetupFinished: Boolean by rememberSaveable { mutableStateOf(hasMasterPassword) }

    PasswordVaultTheme {
        NavHost(
            navController = navController,
            startDestination = if (isAuthSetupFinished) {
                "login"
            } else {
                "password/true"
            },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            composable("main") {
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
                val viewModel: AccountViewModel = hiltViewModel()
                AccountScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    }
                )
            }


            composable("settings") {
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
                        navController.navigate("password/false")
                    },
                    onNavigateToDevSettings = {
                        navController.navigate("devSettings")
                    },
                    onNavigateToSecurityQuestions = {
                        navController.navigate("questions/false")
                    }
                )
            }


            composable("devSettings") {
                val viewModel: DevSettingsViewModel = hiltViewModel()
                DevSettingsScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    }
                )
            }


            composable("help") {
                val viewModel: HelpViewModel = hiltViewModel()
                HelpScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    }
                )
            }


            composable(
                route = "password/{isSetup}",
                arguments = listOf(
                    navArgument("isSetup") { type = NavType.BoolType}
                )
            ) {
                val viewModel: PasswordViewModel = hiltViewModel()
                PasswordScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    },
                    onNavigateToNextSetupStep = {
                        navController.navigate("questions/true")
                    }
                )
            }


            composable(
                route = "questions/{isSetup}",
                arguments = listOf(
                    navArgument("isSetup") { type = NavType.BoolType}
                )
            ) {
                val viewModel: SecurityQuestionsViewModel = hiltViewModel()
                SecurityQuestionsScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    },
                    onNavigateToNextSetupStep = {
                        if (!isAuthSetupFinished) {
                            //Shown at first launch to setup auth:
                            isAuthSetupFinished = true
                            navController.navigate("main") {
                                popUpTo("questions/true") {
                                    inclusive = true
                                }
                            }
                        }
                        else {
                            //Shown through settings to edit master password:
                            navController.navigateUp()
                        }
                    }
                )
            }


            composable("login") {
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


            composable("recovery") {
                val viewModel: RecoveryViewModel = hiltViewModel()
                RecoveryScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    },
                    onFinish = {
                        navController.navigate("main") {
                            popUpTo("recovery") {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
    }
}
