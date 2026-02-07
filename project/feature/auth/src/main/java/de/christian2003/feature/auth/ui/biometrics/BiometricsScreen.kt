package de.christian2003.feature.auth.ui.biometrics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.christian2003.feature.auth.viewmodels.BiometricsViewModel
import de.christian2003.feature.auth.R
import de.christian2003.feature.auth.models.states.BiometricsScreenState
import de.christian2003.feature.auth.viewmodels.SetupFlowSharedViewModel
import de.christian2003.core.ui.composables.HelpCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


/**
 * Screen through which the user can enable biometrics.
 *
 * @param viewModel         View model.
 * @param sharedViewModel   View model shared across flows.
 * @param onNavigateUp      Callback invoked to navigate up the navigation stack.
 * @param onContinue        Callback invoked to continue to the next flow step.
 * @param onBiometricAuth   Callback invoked to authenticate using biometrics.
 */
@Composable
internal fun BiometricsScreen(
    viewModel: BiometricsViewModel,
    sharedViewModel: SetupFlowSharedViewModel,
    onNavigateUp: () -> Unit,
    onContinue: () -> Unit,
    onBiometricAuth: suspend () -> Boolean
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopBar(
                state = viewModel.state,
                onNavigateUp = onNavigateUp
            )
        },
        bottomBar = {
            BottomBar(
                state = viewModel.state,
                onEnable = {
                    coroutineScope.launch {
                        val result: Boolean = onBiometricAuth()
                        if (result) {
                            sharedViewModel.useBiometrics = true
                            onContinue()
                        }
                    }
                },
                onSkip = {
                    sharedViewModel.useBiometrics = false
                    onContinue()
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal))
        ) {
            AnimatedVisibility(viewModel.isHelpCardVisible) {
                HelpCard(
                    text = stringResource(R.string.biometrics_help),
                    onDismiss = {
                        viewModel.dismissHelpCard()
                    },
                    modifier = Modifier.padding(bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = if (viewModel.isHelpCardVisible) {
                    Modifier.fillMaxWidth()
                } else {
                    Modifier.fillMaxSize()
                }
            ) {
                Image(
                    painter = painterResource(R.drawable.onboarding_biometrics),
                    contentDescription = "",
                    modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_xxl))
                )
            }
        }
    }
}


/**
 * Top bar for the screen.
 *
 * @param state         State of the screen.
 * @param onNavigateUp  Callback invoked to navigate up the navigation stack.
 */
@Composable
private fun TopBar(
    state: BiometricsScreenState,
    onNavigateUp: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = when(state) {
                    BiometricsScreenState.FirstTimeSetup -> stringResource(R.string.biometrics_title_firstTimeSetup)
                    BiometricsScreenState.EnableBiometrics -> stringResource(R.string.biometrics_title_enableBiometrics)
                }
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigateUp
            ) {
                Icon(
                    painter = painterResource(de.christian2003.core.ui.R.drawable.ic_back),
                    contentDescription = ""
                )
            }
        }
    )
}


/**
 * Bottom bar for the screen.
 *
 * @param state     State of the screen.
 * @param onEnable  Callback invoked to enable biometrics.
 * @param onSkip    Callback invoked to skip enabling biometrics (during setup).
 */
@Composable
private fun BottomBar(
    state: BiometricsScreenState,
    onEnable: () -> Unit,
    onSkip: () -> Unit
) {
    BottomAppBar {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(
                        //Horizontal padding of bottom app bar: 4 dp
                        horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal) - 4.dp
                    )
            ) {
                if (state == BiometricsScreenState.FirstTimeSetup) {
                    TextButton(
                        onClick = onSkip
                    ) {
                        Text(stringResource(R.string.biometrics_buttonSkip))
                    }
                }
                Button(
                    onClick = onEnable,
                    modifier = Modifier.padding(start = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
                ) {
                    Text(stringResource(R.string.biometrics_buttonEnable))
                }
            }
        }
    }
}
