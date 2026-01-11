package de.christian2003.auth.ui.password

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import de.christian2003.auth.models.password.PasswordScreenState
import de.christian2003.auth.viewmodels.PasswordViewModel
import de.christian2003.auth.R
import de.christian2003.ui.composables.HelpCard


@Composable
fun PasswordScreen(
    viewModel: PasswordViewModel,
    onNavigateUp: () -> Unit,
    onContinue: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when(viewModel.state) {
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

            //TODO: Add password fields
        }
    }
}
