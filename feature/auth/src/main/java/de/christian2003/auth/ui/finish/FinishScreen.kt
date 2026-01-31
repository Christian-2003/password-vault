package de.christian2003.auth.ui.finish

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import de.christian2003.auth.models.states.FinishScreenState
import de.christian2003.auth.R
import de.christian2003.auth.viewmodels.FinishViewModel
import de.christian2003.auth.viewmodels.SetupFlowSharedViewModel
import de.christian2003.ui.theme.isDarkTheme
import de.christian2003.ui.theme.onErrorContainerDark


/**
 * Screen is displayed to the user while authentication data is being saved.
 *
 * @param viewModel             View model.
 * @param onNavigateUp          Callback invoked to navigate up the navigation stack.
 * @param onFinish              Callback invoked to finish the setup.
 * @param setupFlowViewModel    Shared view model for the flow.
 */
@Composable
internal fun FinishScreen(
    viewModel: FinishViewModel,
    onNavigateUp: () -> Unit,
    onFinish: () -> Unit,
    setupFlowViewModel: SetupFlowSharedViewModel? = null
) {
    val isSaving: Boolean = when(viewModel.state) {
        FinishScreenState.FirstTimeSetup -> setupFlowViewModel?.isSavingSession ?: false
        else -> false
    }
    val isFinishedSaving: Boolean = when(viewModel.state) {
        FinishScreenState.FirstTimeSetup -> setupFlowViewModel?.isFinishedSavingSession ?: false
        else -> false
    }

    LaunchedEffect(Unit) {
        if (!isSaving && !isFinishedSaving) {
            when(viewModel.state) {
                FinishScreenState.FirstTimeSetup -> setupFlowViewModel?.save(viewModel.state)
                else -> { }
            }
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                state = viewModel.state,
                onNavigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimensionResource(de.christian2003.ui.R.dimen.margin_horizontal))
        ) {
            if (isSaving) {
                SavingContent(
                    state = viewModel.state
                )
            }
            else {
                FinishedSavingContent(
                    state = viewModel.state,
                    onContinue = onFinish,
                    onGeneratePositiveColor = { negative, darkTheme ->
                        viewModel.generatePositiveColorFromNegativeColor(negative, darkTheme)
                    }
                )
            }
        }
    }
}


/**
 * Content for the screen that is displayed while data is being saved.
 *
 * @param state     State for the screen.
 * @param modifier  Modifier.
 */
@Composable
private fun SavingContent(
    state: FinishScreenState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        LoadingIndicator(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = dimensionResource(de.christian2003.ui.R.dimen.padding_vertical))
        )
        Text(
            text = when (state) {
                FinishScreenState.FirstTimeSetup -> stringResource(R.string.finish_labelLoading_firstTimeSetup)
                FinishScreenState.GenerateNewRecoveryCodes -> stringResource(R.string.finish_labelLoading_generateNewRecoveryCodes)
                else -> stringResource(R.string.finish_labelLoading_newPassword)
            },
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium
        )
    }
}


/**
 * Content for the screen that is displayed once all data is saved successfully.
 *
 * @param state                     State for the screen.
 * @param onContinue                Callback invoked to continue to the next screen.
 * @param onGeneratePositiveColor   Callback invoked to generate a positive color from a specified
 *                                  negative color.
 * @param modifier                  Modifier.
 */
@Composable
private fun FinishedSavingContent(
    state: FinishScreenState,
    onContinue: () -> Unit,
    onGeneratePositiveColor: (Color, Boolean) -> Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_active),
            contentDescription = "",
            tint = onGeneratePositiveColor(MaterialTheme.colorScheme.error, MaterialTheme.isDarkTheme()),
            modifier = Modifier
                .padding(bottom = dimensionResource(de.christian2003.ui.R.dimen.padding_vertical))
                .size(dimensionResource(de.christian2003.ui.R.dimen.image_xxl))
        )
        Text(
            text = when (state) {
                FinishScreenState.FirstTimeSetup -> stringResource(R.string.finish_labelFinished_firstTimeSetup)
                FinishScreenState.GenerateNewRecoveryCodes -> stringResource(R.string.finish_labelFinished_generateNewRecoveryCodes)
                else -> stringResource(R.string.finish_labelFinished_newPassword)
            },
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(de.christian2003.ui.R.dimen.padding_vertical))
        )
        Button(
            onClick = onContinue
        ) {
            Text(stringResource(R.string.finish_buttonContinue))
        }
    }
}


/**
 * Top bar for the screen.
 *
 * @param state         State for the screen.
 * @param onNavigateUp  Callback invoked to navigate up the navigation stack.
 */
@Composable
private fun TopBar(
    state: FinishScreenState,
    onNavigateUp: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = when (state) {
                    FinishScreenState.FirstTimeSetup -> stringResource(R.string.finish_title_firstTimeSetup)
                    FinishScreenState.RecoverPassword -> stringResource(R.string.finish_title_recoverPassword)
                    FinishScreenState.ChangePassword -> stringResource(R.string.finish_title_changePassword)
                    FinishScreenState.GenerateNewRecoveryCodes -> stringResource(R.string.finish_title_generateNewRecoveryCodes)
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
