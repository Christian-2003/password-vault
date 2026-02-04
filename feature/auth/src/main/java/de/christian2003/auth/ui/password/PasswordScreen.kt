package de.christian2003.auth.ui.password

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import de.christian2003.auth.models.states.PasswordScreenState
import de.christian2003.auth.viewmodels.PasswordViewModel
import de.christian2003.auth.R
import de.christian2003.auth.viewmodels.SetupFlowSharedViewModel
import de.christian2003.core.ui.composables.HelpCard
import de.christian2003.core.ui.composables.LoadingIndicatorButton
import de.christian2003.core.ui.composables.TextInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Screen to set the master password.
 *
 * @param viewModel             View model.
 * @param sharedViewModel       Shared view model for the flow.
 * @param onNavigateUp          Callback invoked to navigate up the navigation stack.
 * @param onContinue            Callback invoked to continue to the next setup step.
 */
@Composable
internal fun PasswordScreen(
    viewModel: PasswordViewModel,
    sharedViewModel: SetupFlowSharedViewModel,
    onNavigateUp: () -> Unit,
    onContinue: () -> Unit,
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val currentPasswordFocusRequester: FocusRequester = remember { FocusRequester() }
    val newPasswordFocusRequester: FocusRequester = remember { FocusRequester() }
    val confirmNewPasswordFocusRequester: FocusRequester = remember { FocusRequester() }
    val focusManager: FocusManager = LocalFocusManager.current

    val invokeOnContinue: () -> Unit = {
        when (viewModel.state) {
            PasswordScreenState.ChangePassword, PasswordScreenState.GenerateNewRecoveryCodes, PasswordScreenState.EnableBiometrics -> {
                //When changing password or enabling biometrics, verify if password is valid:
                coroutineScope.launch(Dispatchers.Default) {
                    val result: Boolean = viewModel.verifyCurrentPassword()
                    if (result) {
                        sharedViewModel.currentMasterPassword = viewModel.currentPassword.toCharArray()
                        sharedViewModel.newMasterPassword = viewModel.newPassword.toCharArray()
                        withContext(Dispatchers.Main) {
                            focusManager.clearFocus()
                            onContinue()
                        }
                    }
                }
            }
            else -> {
                //No need to verify password validity, since a new password is being set:
                sharedViewModel.currentMasterPassword = viewModel.currentPassword.toCharArray()
                sharedViewModel.newMasterPassword = viewModel.newPassword.toCharArray()
                focusManager.clearFocus()
                onContinue()
            }
        }
    }

    LaunchedEffect(Unit) {
        //Safe calls required: When rotating the screen, the focus requester is not instantiated
        //for a very short period of time, during which this Launched effect is called. Without
        //this safe call, the app would crash throwing an IllegalStateException.
        when (viewModel.state) {
            PasswordScreenState.ChangePassword, PasswordScreenState.GenerateNewRecoveryCodes, PasswordScreenState.EnableBiometrics -> currentPasswordFocusRequester?.requestFocus()
            else -> newPasswordFocusRequester?.requestFocus()
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                state = viewModel.state,
                onNavigateUp = onNavigateUp
            )
        },
        bottomBar = {
            BottomBar(
                canContinue = viewModel.isContinueButtonEnabled.value,
                isVerifyingData = viewModel.isVerifyingCurrentPassword,
                onContinue = invokeOnContinue
            )
        },
        modifier = Modifier.imePadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            AnimatedVisibility(viewModel.isHelpCardVisible) {
                HelpCard(
                    text = when(viewModel.state) {
                        PasswordScreenState.FirstTimeSetup -> stringResource(R.string.password_help_firstTimeSetup)
                        PasswordScreenState.ChangePassword -> stringResource(R.string.password_help_changePassword)
                        PasswordScreenState.RecoverPassword -> stringResource(R.string.password_help_recoverPassword)
                        PasswordScreenState.GenerateNewRecoveryCodes -> stringResource(R.string.password_help_generateNewRecoveryCodes)
                        PasswordScreenState.EnableBiometrics -> stringResource(R.string.password_help_enableBiometrics)
                    },
                    onDismiss = {
                        viewModel.dismissHelpCard()
                    },
                    modifier = Modifier.padding(
                        start = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                        end = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                        bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                    )
                )
            }

            //Current password:
            if (viewModel.state == PasswordScreenState.ChangePassword
                || viewModel.state == PasswordScreenState.GenerateNewRecoveryCodes
                || viewModel.state == PasswordScreenState.EnableBiometrics) {

                TextInput(
                    value = viewModel.currentPassword,
                    onValueChange = {
                        viewModel.currentPassword = it
                    },
                    label = stringResource(R.string.password_label_currentPassword),
                    isPassword = true,
                    focusRequester = currentPasswordFocusRequester,
                    keyboardOptions = KeyboardOptions(
                        imeAction = when(viewModel.state) {
                            PasswordScreenState.GenerateNewRecoveryCodes, PasswordScreenState.EnableBiometrics -> ImeAction.Done
                            else -> ImeAction.Next
                        }
                    ),
                    keyboardActions = KeyboardActions {
                        when(viewModel.state) {
                            PasswordScreenState.GenerateNewRecoveryCodes, PasswordScreenState.EnableBiometrics -> invokeOnContinue()
                            else -> newPasswordFocusRequester.requestFocus()
                        }
                    },
                    errorMessage = when {
                        !viewModel.isCurrentPasswordValid && viewModel.currentPassword.isBlank() -> stringResource(R.string.password_error_inputEmpty)
                        !viewModel.isCurrentPasswordValid -> stringResource(R.string.password_error_passwordInvalid)
                        else -> null
                    },
                    modifier = Modifier.padding(
                        start = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                        end = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                        bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical) * 3
                    )
                )
            }

            if (viewModel.state != PasswordScreenState.GenerateNewRecoveryCodes
                && viewModel.state != PasswordScreenState.EnableBiometrics) {
                //New password:
                TextInput(
                    value = viewModel.newPassword,
                    onValueChange = {
                        viewModel.newPassword = it
                    },
                    label = when (viewModel.state) {
                        PasswordScreenState.FirstTimeSetup -> stringResource(R.string.password_label_password)
                        else -> stringResource(R.string.password_label_newPassword)
                    },
                    isPassword = true,
                    focusRequester = newPasswordFocusRequester,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions {
                        confirmNewPasswordFocusRequester.requestFocus()
                    },
                    errorMessage = if (!viewModel.isNewPasswordValid) {
                        stringResource(R.string.password_error_inputEmpty)
                    } else {
                        null
                    },
                    modifier = Modifier.padding(
                        start = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                        end = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                        bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                    )
                )

                //Confirm new password:
                TextInput(
                    value = viewModel.confirmNewPassword,
                    onValueChange = {
                        viewModel.confirmNewPassword = it
                    },
                    label = when (viewModel.state) {
                        PasswordScreenState.FirstTimeSetup -> stringResource(R.string.password_label_confirmPassword)
                        else -> stringResource(R.string.password_label_confirmNewPassword)
                    },
                    isPassword = true,
                    focusRequester = confirmNewPasswordFocusRequester,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions {
                        invokeOnContinue()
                    },
                    errorMessage = when {
                        !viewModel.isConfirmNewPasswordValid && viewModel.confirmNewPassword.isEmpty() -> stringResource(R.string.password_error_inputEmpty)
                        !viewModel.isConfirmNewPasswordValid -> when (viewModel.state) {
                            PasswordScreenState.FirstTimeSetup -> stringResource(R.string.password_error_passwordsDoNotMatch)
                            else -> stringResource(R.string.password_error_newPasswordsDoNotMatch)
                        }
                        else -> null
                    },
                    modifier = Modifier.padding(
                        start = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                        end = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                        bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                    )
                )
            }
        }
    }
}


/**
 * Top app bar of the screen.
 *
 * @param state         State of the screen.
 * @param onNavigateUp  Callback invoked to navigate up the navigation stack.
 */
@Composable
private fun TopBar(
    state: PasswordScreenState,
    onNavigateUp: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = when(state) {
                    PasswordScreenState.FirstTimeSetup -> stringResource(R.string.password_title_firstTimeSetup)
                    PasswordScreenState.ChangePassword -> stringResource(R.string.password_title_changePassword)
                    PasswordScreenState.RecoverPassword -> stringResource(R.string.password_title_recoverPassword)
                    PasswordScreenState.GenerateNewRecoveryCodes -> stringResource(R.string.password_title_generateNewRecoveryCodes)
                    PasswordScreenState.EnableBiometrics -> stringResource(R.string.password_title_enableBiometrics)
                }
            )
        },
        navigationIcon = {
            if (state != PasswordScreenState.FirstTimeSetup) {
                IconButton(
                    onClick = onNavigateUp
                ) {
                    Icon(
                        painter = painterResource(de.christian2003.core.ui.R.drawable.ic_back),
                        contentDescription = ""
                    )
                }
            }
        }
    )
}


/**
 * Bar that is fixed at the bottom of the screen.
 *
 * @param canContinue       Whether the use can continue to the next screen of the flow.
 * @parm isVerifyingData    Whether data is currently being verified.
 * @param onContinue        Callback invoked once the user continues to the next screen of the flow.
 */
@Composable
private fun BottomBar(
    canContinue: Boolean,
    isVerifyingData: Boolean,
    onContinue: () -> Unit
) {
    BottomAppBar {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            LoadingIndicatorButton(
                label = stringResource(R.string.password_button_continue),
                isLoading = isVerifyingData,
                enabled = canContinue,
                onClick = {
                    if (canContinue) {
                        onContinue()
                    }
                },
                modifier = Modifier.padding(
                    //Horizontal padding of bottom app bar: 4 dp
                    horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal) - 4.dp
                )
            )
        }
    }
}
