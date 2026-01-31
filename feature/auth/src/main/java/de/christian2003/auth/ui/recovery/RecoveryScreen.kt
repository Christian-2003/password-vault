package de.christian2003.auth.ui.recovery

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
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
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
import de.christian2003.auth.viewmodels.RecoveryViewModel
import de.christian2003.auth.R
import de.christian2003.ui.composables.HelpCard
import de.christian2003.ui.composables.LoadingIndicatorButton
import de.christian2003.ui.composables.TextInput
import de.christian2003.ui.theme.RobotoMono
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Screen through which the user can recover their master password.
 *
 * @param viewModel     View model.
 * @param onNavigateUp  Callback invoked to navigate up the navigation stack.
 * @param onContinue    Callback invoked to navigate to the next step of the recovery.
 */
@Composable
fun RecoveryScreen(
    viewModel: RecoveryViewModel,
    onNavigateUp: () -> Unit,
    onContinue: () -> Unit
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val recoveryCodeFocusRequester: FocusRequester = remember { FocusRequester() }

    val invokeOnContinue: () -> Unit = {
        coroutineScope.launch(Dispatchers.Default) {
            val result: Boolean = viewModel.verifyRecoveryCode()
            if (result) {
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
        recoveryCodeFocusRequester?.requestFocus()
    }

    Scaffold(
        topBar = {
            TopBar(
                onNavigateUp = onNavigateUp
            )
        },
        bottomBar = {
            BottomBar(
                canContinue = viewModel.canContinue.value,
                isVerifyingRecoveryCode = viewModel.isVerifyingRecoveryCode,
                onContinue = invokeOnContinue
            )
        },
        modifier = Modifier.imePadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = dimensionResource(de.christian2003.ui.R.dimen.margin_horizontal)
                )
        ) {
            AnimatedVisibility(viewModel.isHelpCardVisible) {
                HelpCard(
                    text = stringResource(R.string.recovery_help),
                    onDismiss = {
                        viewModel.dismissHelpCard()
                    },
                    modifier = Modifier.padding(bottom = dimensionResource(de.christian2003.ui.R.dimen.padding_vertical))
                )
            }

            val errorCodeInvalid: String = stringResource(R.string.recovery_error_codeInvalid)
            val errorCodeEmpty: String = stringResource(R.string.recovery_error_codeEmpty)
            TextInput(
                value = viewModel.recoveryCode,
                onValueChange = {
                    viewModel.recoveryCode = it.replace("-", "").take(24)
                },
                label = stringResource(R.string.recovery_recoveryCodeLabel),
                focusRequester = recoveryCodeFocusRequester,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions {
                    invokeOnContinue()
                },
                errorMessage = when {
                    viewModel.isRecoveryCodeValid -> null
                    viewModel.recoveryCode.isBlank() -> errorCodeEmpty
                    else -> errorCodeInvalid
                },
                visualTransformation = viewModel.visualTransformation,
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = RobotoMono
                )
            )
        }
    }
}


/**
 * Top bar for the screen.
 *
 * @param onNavigateUp  Callback invoked to navigate up the navigation stack.
 */
@Composable
private fun TopBar(
    onNavigateUp: () -> Unit
) {
    TopAppBar(
        title = {
            Text(stringResource(R.string.recovery_title))
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
 * Bottom bar for the screen.
 *
 * @param canContinue               Whether the user can try to continue to the next recovery step.
 * @param isVerifyingRecoveryCode   Whether a recovery code is currently being verified.
 * @param onContinue                Callback invoked to continue to the next recovery step.
 */
@Composable
private fun BottomBar(
    canContinue: Boolean,
    isVerifyingRecoveryCode: Boolean,
    onContinue: () -> Unit
) {
    BottomAppBar {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            LoadingIndicatorButton(
                label = stringResource(R.string.password_button_continue),
                isLoading = isVerifyingRecoveryCode,
                enabled = canContinue,
                onClick = {
                    if (canContinue && !isVerifyingRecoveryCode) {
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
