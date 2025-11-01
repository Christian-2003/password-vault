package de.christian2003.passwordvault.plugin.presentation.view.password

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import de.christian2003.passwordvault.R
import de.christian2003.passwordvault.plugin.presentation.ui.composables.HelpCard
import de.christian2003.passwordvault.plugin.presentation.ui.composables.TextInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Screen allows the user to configure the master password when they open the app for the first time.
 * Additionally, the user can change the existing master password through this screen.
 *
 * @param viewModel                 View model.
 * @param onNavigateUp              Callback invoked to navigate up the navigation stack.
 * @param onNavigateToNextSetupStep Callback invoked to navigate to the next setup step.
 */
@Composable
fun PasswordScreen(
    viewModel: PasswordViewModel,
    onNavigateUp: () -> Unit,
    onNavigateToNextSetupStep: () -> Unit
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val errorSaving: String = stringResource(R.string.password_errorSaving)
    val errorOldPassword: String = stringResource(R.string.password_errorOldPassword)
    val errorRepeatNewPassword: String = if (viewModel.isSetup) {
        stringResource(R.string.password_errorRepeatPassword)
    } else {
        stringResource(R.string.password_errorRepeatNewPassword)
    }
    val oldPasswordFocusRequester: FocusRequester = remember { FocusRequester() }
    val newPasswordFocusRequester: FocusRequester = remember { FocusRequester() }
    val repeatNewPasswordFocusRequester: FocusRequester = remember { FocusRequester() }

    val invokeSave: () -> Unit = {
        coroutineScope.launch(Dispatchers.Default) {
            viewModel.save()
            withContext(Dispatchers.Main) {
                if (viewModel.isOldPasswordValid && viewModel.isRepeatNewPasswordValid && viewModel.isDataValid.value) {
                    if (viewModel.isSetup) {
                        onNavigateToNextSetupStep()
                    }
                    else {
                        onNavigateUp()
                    }
                }
                else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(errorSaving)
                    }
                }
            }
        }

    }

    LaunchedEffect(Unit) {
        //Safe calls required: When rotating the screen, the focus requester is not instantiated
        //for a very short period of time, during which this Launched effect is called. Without
        //this safe call, the app would crash throwing an IllegalStateException.
        if (viewModel.isSetup) {
            newPasswordFocusRequester?.requestFocus()
        }
        else {
            oldPasswordFocusRequester?.requestFocus()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (viewModel.isSetup) {
                            stringResource(R.string.password_titleSetup)
                        } else {
                            stringResource(R.string.password_titleEdit)
                        }
                    )
                },
                navigationIcon = {
                    if (!viewModel.isSetup) {
                        IconButton(
                            onClick = onNavigateUp
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_back),
                                contentDescription = ""
                            )
                        }
                    }
                }
            )
        },
        modifier = Modifier.imePadding()
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimensionResource(R.dimen.margin_horizontal))
        ) {
            AnimatedVisibility(viewModel.isHelpCardVisible) {
                HelpCard(
                    text = if (viewModel.isSetup) {
                        stringResource(R.string.password_helpSetup)
                    } else {
                        stringResource(R.string.password_helpEdit)
                    },
                    onDismiss = {
                        viewModel.dismissHelpCard()
                    },
                    modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_vertical))
                )
            }
            if (!viewModel.isSetup) {
                TextInput(
                    value = viewModel.oldPassword,
                    onValueChange = {
                        viewModel.oldPassword = it
                    },
                    label = stringResource(R.string.password_oldPasswordLabel),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            newPasswordFocusRequester.requestFocus()
                        }
                    ),
                    focusRequester = oldPasswordFocusRequester,
                    errorMessage = if (viewModel.isOldPasswordValid) { null } else { errorOldPassword },
                    isPassword = true,
                    modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_vertical) * 2)
                )
            }
            TextInput(
                value = viewModel.newPassword,
                onValueChange = {
                    viewModel.newPassword = it
                },
                label = if (viewModel.isSetup) {
                    stringResource(R.string.password_passwordLabel)
                } else {
                    stringResource(R.string.password_newPasswordLabel)
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        repeatNewPasswordFocusRequester.requestFocus()
                    }
                ),
                focusRequester = newPasswordFocusRequester,
                isPassword = true,
                modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_vertical))
            )
            TextInput(
                value = viewModel.repeatNewPassword,
                onValueChange = {
                    viewModel.repeatNewPassword = it
                },
                label = if (viewModel.isSetup) {
                    stringResource(R.string.password_repeatPasswordLabel)
                } else {
                    stringResource(R.string.password_repeatNewPasswordLabel)
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        invokeSave()
                    }
                ),
                focusRequester = repeatNewPasswordFocusRequester,
                errorMessage = if (viewModel.isRepeatNewPasswordValid) { null } else { errorRepeatNewPassword },
                isPassword = true,
                modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_vertical))
            )
            Button(
                enabled = viewModel.isDataValid.value,
                onClick = {
                    invokeSave()
                }
            ) {
                Text(
                    text = if (viewModel.isSetup) {
                        stringResource(R.string.button_continue)
                    } else {
                        stringResource(R.string.button_save)
                    }
                )
            }
        }
    }
}
