package de.christian2003.passwordvault.plugin.presentation.view.recovery

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import de.christian2003.passwordvault.R
import de.christian2003.passwordvault.plugin.presentation.ui.composables.Headline
import de.christian2003.passwordvault.plugin.presentation.ui.composables.HelpCard
import de.christian2003.passwordvault.plugin.presentation.ui.composables.TextInput
import de.christian2003.passwordvault.plugin.presentation.view.securityquestions.SecurityQuestionUiDto


@Composable
fun RecoveryScreen(
    viewModel: RecoveryViewModel,
    onNavigateUp: () -> Unit,
    onFinish: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.recovery_title))
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateUp
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = ""
                        )
                    }
                }
            )
        },
        modifier = Modifier.imePadding()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                AnimatedVisibility(viewModel.isHelpCardVisible) {
                    HelpCard(
                        text = stringResource(R.string.recovery_help),
                        onDismiss = {
                            viewModel.dismissHelpCard()
                        },
                        modifier = Modifier.padding(
                            start = dimensionResource(R.dimen.margin_horizontal),
                            end = dimensionResource(R.dimen.margin_horizontal),
                            bottom = dimensionResource(R.dimen.padding_vertical)
                        )
                    )
                }
            }
            itemsIndexed(viewModel.securityQuestions) { index, question ->
                QuestionItem(
                    index = index,
                    question = question,
                    onAnswerChange = { index, answer ->
                        viewModel.securityQuestions[index] = question.copy(answer = answer)
                    }
                )
            }
        }
    }
}


@Composable
private fun QuestionItem(
    index: Int,
    question: SecurityQuestionUiDto,
    onAnswerChange: (Int, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = dimensionResource(R.dimen.padding_vertical))
    ) {
        Headline(stringResource(R.string.recovery_questionTitle, index + 1))
        Text(
            text = stringArrayResource(R.array.securityQuestion_questions)[question.question.ordinal],
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.margin_horizontal))
        )
        TextInput(
            value = question.answer ?: "",
            onValueChange = { answer ->
                onAnswerChange(index, answer)
            },
            label = stringResource(R.string.recovery_answerLabel),
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.margin_horizontal))
        )
    }
}
