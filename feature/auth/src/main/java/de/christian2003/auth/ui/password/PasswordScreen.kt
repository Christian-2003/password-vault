package de.christian2003.auth.ui.password

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import de.christian2003.auth.models.password.PasswordScreenState
import de.christian2003.auth.viewmodels.PasswordViewModel
import de.christian2003.auth.R
import de.christian2003.ui.composables.HelpCard
import de.christian2003.ui.composables.LoadingIndicatorButton
import de.christian2003.ui.composables.TextInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Screen to set the master password.
 *
 * @param viewModel     View model.
 * @param onNavigateUp  Callback invoked to navigate up the navigation stack.
 * @param onContinue    Callback invoked to continue to the next setup step.
 */
@Composable
fun PasswordScreen(
    viewModel: PasswordViewModel,
    onNavigateUp: () -> Unit,
    onContinue: () -> Unit
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val currentPasswordFocusRequester: FocusRequester = remember { FocusRequester() }
    val newPasswordFocusRequester: FocusRequester = remember { FocusRequester() }
    val confirmNewPasswordFocusRequester: FocusRequester = remember { FocusRequester() }

    val invokeOnContinue: () -> Unit = {
        coroutineScope.launch(Dispatchers.Default) {
            viewModel.setNewMasterPassword()
            if (viewModel.isMasterPasswordSetSuccessfully) {
                withContext(Dispatchers.Main) {
                    onContinue()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        //Safe calls required: When rotating the screen, the focus requester is not instantiated
        //for a very short period of time, during which this Launched effect is called. Without
        //this safe call, the app would crash throwing an IllegalStateException.
        when (viewModel.state) {
            PasswordScreenState.ChangePassword -> currentPasswordFocusRequester?.requestFocus()
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
                isSettingNewMasterPassword = viewModel.isSettingNewMasterPassword,
                onContinue = invokeOnContinue
            )
        },
        modifier = Modifier.imePadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            AnimatedVisibility(viewModel.isHelpCardVisible) {
                HelpCard(
                    text = when(viewModel.state) {
                        PasswordScreenState.FirstTimeSetup -> stringResource(R.string.password_helpFirstTimeSetup)
                        PasswordScreenState.ChangePassword -> stringResource(R.string.password_helpChangePassword)
                        PasswordScreenState.RecoverPassword -> stringResource(R.string.password_helpRecoverPassword)
                    },
                    onDismiss = {
                        viewModel.dismissHelpCard()
                    },
                    modifier = Modifier.padding(
                        start = dimensionResource(de.christian2003.ui.R.dimen.margin_horizontal),
                        end = dimensionResource(de.christian2003.ui.R.dimen.margin_horizontal),
                        bottom = dimensionResource(de.christian2003.ui.R.dimen.padding_vertical)
                    )
                )
            }

            //Current password:
            if (viewModel.state == PasswordScreenState.ChangePassword) {
                TextInput(
                    value = viewModel.currentPassword,
                    onValueChange = {
                        viewModel.currentPassword = it
                    },
                    label = stringResource(R.string.password_labelCurrentPassword),
                    isPassword = true,
                    focusRequester = currentPasswordFocusRequester,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions {
                        newPasswordFocusRequester.requestFocus()
                    },
                    modifier = Modifier.padding(
                        start = dimensionResource(de.christian2003.ui.R.dimen.margin_horizontal),
                        end = dimensionResource(de.christian2003.ui.R.dimen.margin_horizontal),
                        bottom = dimensionResource(de.christian2003.ui.R.dimen.padding_vertical) * 3
                    )
                )
            }

            //New password:
            val errorEmptyInput: String = stringResource(R.string.password_error_inputEmpty)
            TextInput(
                value = viewModel.newPassword,
                onValueChange = {
                    viewModel.newPassword = it
                },
                label = when (viewModel.state) {
                    PasswordScreenState.FirstTimeSetup -> stringResource(R.string.password_labelPassword)
                    else -> stringResource(R.string.password_labelNewPassword)
                },
                isPassword = true,
                focusRequester = newPasswordFocusRequester,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions {
                    confirmNewPasswordFocusRequester.requestFocus()
                },
                errorMessage = if (!viewModel.isNewPasswordValid) {
                    errorEmptyInput
                } else {
                    null
                },
                modifier = Modifier.padding(
                    start = dimensionResource(de.christian2003.ui.R.dimen.margin_horizontal),
                    end = dimensionResource(de.christian2003.ui.R.dimen.margin_horizontal),
                    bottom = dimensionResource(de.christian2003.ui.R.dimen.padding_vertical)
                )
            )

            //Confirm new password:
            val errorConfirmNewPassword: String = when (viewModel.state) {
                PasswordScreenState.FirstTimeSetup -> stringResource(R.string.password_error_passwordsDoNotMatch)
                else -> stringResource(R.string.password_error_newPasswordsDoNotMatch)
            }
            TextInput(
                value = viewModel.confirmNewPassword,
                onValueChange = {
                    viewModel.confirmNewPassword = it
                },
                label = when (viewModel.state) {
                    PasswordScreenState.FirstTimeSetup -> stringResource(R.string.password_labelConfirmPassword)
                    else -> stringResource(R.string.password_labelConfirmNewPassword)
                },
                isPassword = true,
                focusRequester = confirmNewPasswordFocusRequester,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions {
                    invokeOnContinue()
                },
                errorMessage = when {
                    !viewModel.isConfirmNewPasswordValid && viewModel.confirmNewPassword.isEmpty() -> errorEmptyInput
                    !viewModel.isConfirmNewPasswordValid -> errorConfirmNewPassword
                    else -> null
                },
                modifier = Modifier.padding(
                    start = dimensionResource(de.christian2003.ui.R.dimen.margin_horizontal),
                    end = dimensionResource(de.christian2003.ui.R.dimen.margin_horizontal),
                    bottom = dimensionResource(de.christian2003.ui.R.dimen.padding_vertical)
                )
            )
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
                    PasswordScreenState.FirstTimeSetup -> stringResource(R.string.password_titleFirstTimeSetup)
                    PasswordScreenState.ChangePassword -> stringResource(R.string.password_titleChangePassword)
                    PasswordScreenState.RecoverPassword -> stringResource(R.string.password_titleRecoverPassword)
                }
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigateUp
            ) {
                Icon(
                    painter = painterResource(de.christian2003.ui.R.drawable.ic_back),
                    contentDescription = ""
                )
            }
        }
    )
}


/**
 * Bar that is fixed at the bottom of the screen.
 *
 * @param canContinue                   Whether the use can continue to the next screen of the flow.
 * @param isSettingNewMasterPassword    Indicates whether a new master password is currently being set.
 * @param onContinue                    Callback invoked once the user continues to the next screen
 *                                      of the flow.
 */
@Composable
private fun BottomBar(
    canContinue: Boolean,
    isSettingNewMasterPassword: Boolean,
    onContinue: () -> Unit
) {
    BottomAppBar {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            LoadingIndicatorButton(
                label = stringResource(R.string.password_buttonContinue),
                isLoading = isSettingNewMasterPassword,
                enabled = canContinue,
                onClick = {
                    if (canContinue && !isSettingNewMasterPassword) {
                        onContinue()
                    }
                },
                modifier = Modifier.padding(
                    //Horizontal padding of bottom app bar: 4 dp
                    horizontal = dimensionResource(de.christian2003.ui.R.dimen.margin_horizontal) - 4.dp
                )
            )
        }
    }
}
