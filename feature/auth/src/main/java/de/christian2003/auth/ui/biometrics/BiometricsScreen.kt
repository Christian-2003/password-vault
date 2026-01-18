package de.christian2003.auth.ui.biometrics

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.christian2003.auth.viewmodels.BiometricsViewModel
import de.christian2003.auth.R
import de.christian2003.ui.composables.HelpCard


@Composable
fun BiometricsScreen(
    viewModel: BiometricsViewModel,
    onNavigateUp: () -> Unit,
    onContinue: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(
                onNavigateUp = onNavigateUp
            )
        },
        bottomBar = {
            BottomBar(
                onEnable = {
                    //TODO: Enable biometrics
                    onContinue()
                },
                onSkip = onContinue
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(de.christian2003.ui.R.dimen.margin_horizontal))
        ) {
            AnimatedVisibility(viewModel.isHelpCardVisible) {
                HelpCard(
                    text = stringResource(R.string.biometrics_help),
                    onDismiss = {
                        viewModel.dismissHelpCard()
                    },
                    modifier = Modifier.padding(bottom = dimensionResource(de.christian2003.ui.R.dimen.padding_vertical))
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
                    modifier = Modifier.size(dimensionResource(de.christian2003.ui.R.dimen.image_xxl))
                )
            }
        }
    }
}


@Composable
private fun TopBar(
    onNavigateUp: () -> Unit
) {
    TopAppBar(
        title = {
            Text(stringResource(R.string.biometrics_title))
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


@Composable
private fun BottomBar(
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
                        horizontal = dimensionResource(de.christian2003.ui.R.dimen.margin_horizontal) - 4.dp
                    )
            ) {
                TextButton(
                    onClick = onSkip
                ) {
                    Text(stringResource(R.string.biometrics_buttonSkip))
                }
                Button(
                    onClick = onEnable,
                    modifier = Modifier.padding(start = dimensionResource(de.christian2003.ui.R.dimen.padding_horizontal))
                ) {
                    Text(stringResource(R.string.biometrics_buttonEnable))
                }
            }
        }
    }
}
