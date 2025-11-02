package de.christian2003.passwordvault.plugin.presentation.view.securityquestions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import de.christian2003.passwordvault.R
import de.christian2003.passwordvault.plugin.presentation.ui.composables.EmptyPlaceholder
import de.christian2003.passwordvault.plugin.presentation.ui.composables.HelpCard


@Composable
fun SecurityQuestionsScreen(
    viewModel: SecurityQuestionsViewModel,
    onNavigateUp: () -> Unit,
    onNavigateToNextSetupStep: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (viewModel.isSetup) {
                            stringResource(R.string.securityQuestion_titleSetup)
                        } else {
                            stringResource(R.string.securityQuestion_titleEdit)
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
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                AnimatedVisibility(viewModel.isHelpCardVisible) {
                    HelpCard(
                        text = if (viewModel.isSetup) {
                            stringResource(R.string.securityQuestion_helpSetup)
                        } else {
                            stringResource(R.string.securityQuestion_helpEdit)
                        },
                        onDismiss = {
                            viewModel.dismissHelpCard()
                        },
                        modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.margin_horizontal))
                    )
                }
            }
            if (viewModel.securityQuestions.isEmpty()) {
                item {
                    EmptyPlaceholder(
                        title = stringResource(R.string.securityQuestion_emptyPlaceholder_title),
                        subtitle = stringResource(R.string.securityQuestion_emptyPlaceholder_subtitle),
                        painter = painterResource(R.drawable.el_questions)
                    )
                }
            }
            items(viewModel.securityQuestions) {

            }
        }
    }
}


@Composable
private fun SecurityQuestionItem(
    isSetup: Boolean,

) {

}
