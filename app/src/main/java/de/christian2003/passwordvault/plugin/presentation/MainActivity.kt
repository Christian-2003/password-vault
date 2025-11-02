package de.christian2003.passwordvault.plugin.presentation

import android.content.Context
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
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.christian2003.passwordvault.application.repository.AuthRepository
import de.christian2003.passwordvault.application.security.BiometricAuthService
import de.christian2003.passwordvault.application.usecases.account.CreateAccountUseCase
import de.christian2003.passwordvault.application.usecases.account.DeleteAccountUseCase
import de.christian2003.passwordvault.application.usecases.account.GetAccountByIdUseCase
import de.christian2003.passwordvault.application.usecases.account.GetAccountIconUseCase
import de.christian2003.passwordvault.application.usecases.account.GetAllAccountDescriptorsUseCase
import de.christian2003.passwordvault.application.usecases.account.UpdateAccountUseCase
import de.christian2003.passwordvault.application.usecases.packages.GetAllPackagesUseCase
import de.christian2003.passwordvault.application.usecases.packages.GetLocalizedPackageNameUseCase
import de.christian2003.passwordvault.application.usecases.packages.GetPackageIconUseCase
import de.christian2003.passwordvault.application.usecases.tag.CreateTagUseCase
import de.christian2003.passwordvault.application.usecases.tag.DeleteTagUseCase
import de.christian2003.passwordvault.application.usecases.tag.GetAllTagsUseCase
import de.christian2003.passwordvault.application.usecases.tag.UpdateTagUseCase
import de.christian2003.passwordvault.application.security.ClipboardService
import de.christian2003.passwordvault.application.usecases.auth.AreBiometricsAvailableUseCase
import de.christian2003.passwordvault.application.usecases.auth.AreBiometricsConfiguredUseCase
import de.christian2003.passwordvault.application.usecases.auth.BiometricAuthUseCase
import de.christian2003.passwordvault.application.usecases.auth.ChangeSecurityQuestionUseCase
import de.christian2003.passwordvault.application.usecases.auth.GetSecurityQuestionsUseCase
import de.christian2003.passwordvault.application.usecases.auth.SetupAuthUseCase
import de.christian2003.passwordvault.application.usecases.auth.SetupBiometricsUseCase
import de.christian2003.passwordvault.application.usecases.auth.SetupSecurityQuestionsUseCase
import de.christian2003.passwordvault.application.usecases.auth.UpdatePasswordUseCase
import de.christian2003.passwordvault.application.usecases.auth.VerifyPasswordUseCase
import de.christian2003.passwordvault.plugin.PasswordVaultApplication
import de.christian2003.passwordvault.plugin.infrastructure.db.PasswordVaultRepository
import de.christian2003.passwordvault.plugin.infrastructure.packages.AndroidPackageFingerprintService
import de.christian2003.passwordvault.plugin.infrastructure.packages.LocalPackagesRepository
import de.christian2003.passwordvault.plugin.infrastructure.security.AndroidClipboardService
import de.christian2003.passwordvault.plugin.infrastructure.security.auth.AndroidBiometricAuthService
import de.christian2003.passwordvault.plugin.infrastructure.security.auth.SharedPreferencesAuthRepository
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
import de.christian2003.passwordvault.plugin.presentation.view.securityquestions.SecurityQuestionsScreen
import de.christian2003.passwordvault.plugin.presentation.view.securityquestions.SecurityQuestionsViewModel
import de.christian2003.passwordvault.plugin.presentation.view.settings.SettingsScreen
import de.christian2003.passwordvault.plugin.presentation.view.settings.SettingsViewModel
import java.util.UUID
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid


//Require FragmentActivity instead of ComponentActivity in order to host a biometric prompt
class MainActivity : FragmentActivity() {

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
            PasswordVault()
        }
    }


    private fun isNightMode(): Boolean {
        val currentMode: Int = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentMode == Configuration.UI_MODE_NIGHT_YES
    }

}


@Composable
fun PasswordVault() {
    val navController: NavHostController = rememberNavController()
    val context: Context = LocalContext.current
    val application: PasswordVaultApplication = (context.applicationContext as PasswordVaultApplication)
    val repository: PasswordVaultRepository = application.getRepository()
    val packagesRepository: LocalPackagesRepository = application.getPackagesRepository()
    val authRepository: AuthRepository = SharedPreferencesAuthRepository(context)
    val biometricAuthService: BiometricAuthService = AndroidBiometricAuthService(context)
    val clipboardService: ClipboardService = AndroidClipboardService(LocalClipboard.current.nativeClipboard)

    var isAuthSetupFinished: Boolean by rememberSaveable { mutableStateOf(authRepository.hasPassword()) }

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
                val viewModel: MainViewModel = viewModel()
                viewModel.init(
                    getAllAccountDescriptorsUseCase = GetAllAccountDescriptorsUseCase(repository),
                    deleteAccountUseCase = DeleteAccountUseCase(repository),
                    getAccountIconUseCase = GetAccountIconUseCase(packagesRepository)
                )

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
            ) { backStackEntry ->
                val id: Uuid? = try {
                    UUID.fromString(backStackEntry.arguments!!.getString("accountId")).toKotlinUuid() //Wtf is this shit?
                } catch (_: Exception) {
                    null
                }
                val viewModel: AccountViewModel = viewModel()
                viewModel.init(
                    getAccountByIdUseCase = GetAccountByIdUseCase(repository),
                    createAccountUseCase = CreateAccountUseCase(repository, repository),
                    updateAccountUseCase = UpdateAccountUseCase(repository, repository),
                    getAccountIconUseCase = GetAccountIconUseCase(packagesRepository),
                    getAllTagsUseCase = GetAllTagsUseCase(repository),
                    createTagUseCase = CreateTagUseCase(repository),
                    updateTagUseCase = UpdateTagUseCase(repository),
                    deleteTagUseCase = DeleteTagUseCase(repository),
                    getAllPackagesUseCase = GetAllPackagesUseCase(packagesRepository),
                    getLocalizedPackageNameUseCase = GetLocalizedPackageNameUseCase(packagesRepository),
                    getPackageIconUseCase = GetPackageIconUseCase(packagesRepository),
                    packageFingerprintService = AndroidPackageFingerprintService(context.packageManager),
                    clipboardService = clipboardService,
                    id = id
                )

                AccountScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    }
                )
            }


            composable("settings") {
                val viewModel: SettingsViewModel = viewModel()
                viewModel.init(
                    areBiometricsAvailableUseCase = AreBiometricsAvailableUseCase(authRepository),
                    areBiometricsConfiguredUseCase = AreBiometricsConfiguredUseCase(authRepository),
                    setupBiometricsUseCase = SetupBiometricsUseCase(authRepository, biometricAuthService)
                )

                SettingsScreen(
                    viewModel = viewModel,
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
                val viewModel: DevSettingsViewModel = viewModel()
                viewModel.init()

                DevSettingsScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    }
                )
            }


            composable("help") {
                val viewModel: HelpViewModel = viewModel()
                viewModel.init()

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
            ) { backStackEntry ->
                val isSetup: Boolean = if (backStackEntry.arguments?.containsKey("isSetup") ?: false) {
                    backStackEntry.arguments?.getBoolean("isSetup") ?: false
                } else {
                    false
                }
                val viewModel: PasswordViewModel = viewModel()
                viewModel.init(
                    setupAuthUseCase = SetupAuthUseCase(authRepository),
                    updatePasswordUseCase = UpdatePasswordUseCase(authRepository),
                    verifyPasswordUseCase = VerifyPasswordUseCase(authRepository),
                    isSetup = isSetup
                )

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
            ) { backStackEntry ->
                val isSetup: Boolean = if (backStackEntry.arguments?.containsKey("isSetup") ?: false) {
                    backStackEntry.arguments?.getBoolean("isSetup") ?: false
                } else {
                    false
                }
                val viewModel: SecurityQuestionsViewModel = viewModel()
                viewModel.init(
                    setupSecurityQuestionsUseCase = SetupSecurityQuestionsUseCase(authRepository),
                    getSecurityQuestionsUseCase = GetSecurityQuestionsUseCase(authRepository),
                    changeSecurityQuestionUseCase = ChangeSecurityQuestionUseCase(authRepository),
                    isSetup = isSetup
                )

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
                val viewModel: LoginViewModel = viewModel()
                viewModel.init(
                    verifyPasswordUseCase = VerifyPasswordUseCase(authRepository),
                    areBiometricsConfiguredUseCase = AreBiometricsConfiguredUseCase(authRepository),
                    biometricAuthUseCase = BiometricAuthUseCase(authRepository, biometricAuthService)
                )

                LoginScreen(
                    viewModel = viewModel,
                    onFinish = {
                        navController.navigate("main") {
                            popUpTo("login") {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
    }
}
