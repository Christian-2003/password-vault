package de.christian2003.passwordvault.plugin.presentation.view.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import de.christian2003.passwordvault.R
import de.christian2003.passwordvault.plugin.presentation.ui.composables.LoadingIndicatorButton
import de.christian2003.passwordvault.plugin.presentation.ui.composables.TextInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Screen allows the user to login to the app.
 *
 * @param viewModel             View model.
 * @param onBiometricAuth       Callback invoked to perform biometric authentication.
 * @param onFinish              Callback invoked to finish login and continue to the main screen.
 * @param onNavigateToRecovery  Callback invoked to navigate to the password recovery.
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onBiometricAuth: suspend () -> Boolean,
    onFinish: () -> Unit,
    onNavigateToRecovery: () -> Unit
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val errorPassword: String = stringResource(R.string.login_errorPassword)
    val focusRequester: FocusRequester = remember { FocusRequester() }

    val invokeOnFinish: () -> Unit = {
        coroutineScope.launch(Dispatchers.Default) {
            viewModel.verifyPassword()
            if (viewModel.isPasswordValid) {
                viewModel.clearData()
                withContext(Dispatchers.Main) {
                    onFinish()
                }
            }
        }
    }

    val invokeBiometricAuth: () -> Unit = {
        coroutineScope.launch {
            val result = onBiometricAuth()
            if (result) {
                viewModel.clearData()
                onFinish()
            }
        }
    }

    LaunchedEffect(Unit) {
        if (viewModel.areBiometricsConfigured && !viewModel.skipBiometricsPrompt) {
            invokeBiometricAuth()
        }
        else {
            //Safe calls required: When rotating the screen, the focus requester is not instantiated
            //for a very short period of time, during which this Launched effect is called. Without
            //this safe call, the app would crash throwing an IllegalStateException.
            focusRequester?.requestFocus()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(stringResource(R.string.app_name))
                }
            )
        },
        modifier = Modifier.imePadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(
                    horizontal = dimensionResource(R.dimen.margin_horizontal)
                )
        ) {
            //Content:
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Image(
                    painter = rememberAsyncImagePainter(R.mipmap.ic_launcher),
                    contentDescription = "",
                    modifier = Modifier.size(dimensionResource(R.dimen.image_l))
                )
                Text(
                    text = stringResource(R.string.login_hint),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_vertical))
                )
                TextInput(
                    value = viewModel.password.concatToString(),
                    onValueChange = {
                        viewModel.password = it.toCharArray()
                    },
                    label = stringResource(R.string.login_passwordLabel),
                    isPassword = true,
                    errorMessage = if (viewModel.isPasswordValid) { null } else { errorPassword },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            invokeOnFinish()
                        }
                    ),
                    focusRequester = focusRequester
                )
                if (viewModel.isRecoveryConfigured) {
                    TextButton(
                        onClick = onNavigateToRecovery
                    ) {
                        Text(stringResource(R.string.button_forgotPassword))
                    }
                }
            }

            //Buttons:
            Column {
                if (viewModel.areBiometricsConfigured) {
                    OutlinedButton(
                        onClick = invokeBiometricAuth,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_biometrics),
                                contentDescription = "",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(stringResource(R.string.button_useBiometrics))
                        }
                    }
                }
                LoadingIndicatorButton(
                    label = stringResource(R.string.button_login),
                    isLoading = viewModel.isConfirmingPassword,
                    onClick = invokeOnFinish,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimensionResource(R.dimen.padding_vertical))
                )
            }
        }
    }
}
