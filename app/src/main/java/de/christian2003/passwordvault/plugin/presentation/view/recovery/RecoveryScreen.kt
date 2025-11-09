package de.christian2003.passwordvault.plugin.presentation.view.recovery

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import de.christian2003.passwordvault.R
import de.christian2003.passwordvault.plugin.presentation.ui.composables.Headline
import de.christian2003.passwordvault.plugin.presentation.ui.composables.HelpCard
import de.christian2003.passwordvault.plugin.presentation.ui.composables.LoadingIndicatorButton
import de.christian2003.passwordvault.plugin.presentation.ui.composables.TextInput
import de.christian2003.passwordvault.plugin.presentation.view.securityquestions.SecurityQuestionUiDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun RecoveryScreen(
    viewModel: RecoveryViewModel,
    sharedViewModel: SharedRecoveryViewModel,
    onNavigateUp: () -> Unit,
    onNavigateToChangePassword: () -> Unit
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
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
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider()
                AnimatedVisibility(viewModel.answersValid != null && !viewModel.answersValid!!) {
                    Text(
                        text = stringResource(R.string.recovery_answersInvalid),
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = dimensionResource(R.dimen.margin_horizontal),
                                top = dimensionResource(R.dimen.padding_vertical),
                                end = dimensionResource(R.dimen.margin_horizontal)
                            )
                    )
                }
                LoadingIndicatorButton(
                    label = stringResource(R.string.recovery_verifyButton),
                    isLoading = viewModel.isValidatingAnswers,
                    onClick = {
                        coroutineScope.launch(Dispatchers.Default) {
                            if (!viewModel.isValidatingAnswers) {
                                if (viewModel.verifySecurityQuestions()) {
                                    sharedViewModel.securityQuestions = viewModel.securityQuestionsToMap()
                                    withContext(Dispatchers.Main) {
                                        onNavigateToChangePassword()
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(R.dimen.margin_horizontal),
                        vertical = dimensionResource(R.dimen.padding_vertical)
                    )
                )
            }
        }
    }
}


/**
 * Item displaying a security question and a text input through which to enter the answer.
 *
 * @param index             Index of the security question within the list of questions.
 * @param question          Security question to answer.
 * @param onAnswerChange    Callback invoked once the answer changes.
 */
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
            label = stringResource(R.string.recovery_answerLabel, index + 1),
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.margin_horizontal))
        )
    }
}
