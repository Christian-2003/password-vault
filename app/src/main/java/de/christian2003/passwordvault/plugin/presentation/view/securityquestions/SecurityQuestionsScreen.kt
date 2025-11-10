package de.christian2003.passwordvault.plugin.presentation.view.securityquestions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import de.christian2003.passwordvault.R
import de.christian2003.passwordvault.domain.security.auth.SecurityQuestion
import de.christian2003.passwordvault.plugin.presentation.ui.composables.ConfirmDeleteDialog
import de.christian2003.passwordvault.plugin.presentation.ui.composables.ContextAction
import de.christian2003.passwordvault.plugin.presentation.ui.composables.ContextActionBase
import de.christian2003.passwordvault.plugin.presentation.ui.composables.ContextActionDivider
import de.christian2003.passwordvault.plugin.presentation.ui.composables.ContextActions
import de.christian2003.passwordvault.plugin.presentation.ui.composables.DropdownInput
import de.christian2003.passwordvault.plugin.presentation.ui.composables.EmptyPlaceholder
import de.christian2003.passwordvault.plugin.presentation.ui.composables.HelpCard
import de.christian2003.passwordvault.plugin.presentation.ui.composables.TextInput


/**
 * Screen displays a list of the security questions that are configured by the user.
 *
 * @param viewModel                 View model.
 * @param onNavigateUp              Callback invoked to navigate up the navigation stack.
 * @param onNavigateToNextSetupStep Callback invoked to navigate to the next setup step.
 */
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SecurityQuestionList(
                questions = viewModel.securityQuestions,
                isHelpCardVisible = viewModel.isHelpCardVisible,
                isSetup = viewModel.isSetup,
                onDismissHelpCard = {
                    viewModel.dismissHelpCard()
                },
                onEditQuestion = { question ->
                    viewModel.questionToEdit = question
                },
                onDeleteQuestion = { question ->
                    viewModel.questionToDelete = question
                },
                onShowHelp = {
                    viewModel.isHelpDialogVisible = true
                },
                onAddQuestion = {
                    viewModel.isCreateDialogVisible = true
                },
                modifier = Modifier.weight(1f)
            )
            if (viewModel.isSetup) {
                ButtonRow(
                    isContinueEnabled = viewModel.securityQuestions.size >= 5,
                    onContinue = {
                        onNavigateToNextSetupStep()
                    },
                    onSkip = {
                        onNavigateToNextSetupStep()
                    }
                )
            }
        }
    }

    //Dialog to create a new question:
    if (viewModel.isCreateDialogVisible) {
        SecurityQuestionDialog(
            securityQuestion = null,
            selectedSecurityQuestions = viewModel.securityQuestions.map { it.question },
            onDismiss = {
                viewModel.dismissCreateDialog()
            },
            onSave = { question, answer ->
                viewModel.dismissCreateDialog(question, answer)
            }
        )
    }

    //Dialog to edit an existing question
    val questionToEdit: SecurityQuestionUiDto? = viewModel.questionToEdit
    if (questionToEdit != null) {
        SecurityQuestionDialog(
            securityQuestion = questionToEdit.question,
            selectedSecurityQuestions = viewModel.securityQuestions.map { it.question } - questionToEdit.question,
            answer = questionToEdit.answer,
            onDismiss = {
                viewModel.dismissEditQuestionDialog()
            },
            onSave = { question, answer ->
                viewModel.dismissEditQuestionDialog(question, answer)
            }
        )
    }

    //Dialog to delete a question:
    if (viewModel.questionToDelete != null) {
        ConfirmDeleteDialog(
            text = stringResource(R.string.securityQuestion_confirmDeleteMessage),
            onDismiss = {
                viewModel.dismissDeleteQuestionDialog()
            },
            onConfirm = {
                viewModel.dismissDeleteQuestionDialog(viewModel.questionToDelete?.question)
            }
        )
    }

    //Help dialog:
    if (viewModel.isHelpDialogVisible) {
        InfoDialog(
            title = stringResource(R.string.securityQuestion_noAnswerDialog_title),
            text = stringResource(R.string.securityQuestion_noAnswerDialog_text),
            onDismiss = {
                viewModel.isHelpDialogVisible = false
            }
        )
    }
}


/**
 * Displays a list of security questions.
 *
 * @param questions         List of questions to display.
 * @param isHelpCardVisible Whether the help card is visible.
 * @param isSetup           Whether the screen is shown through the app setup.
 * @param onDismissHelpCard Callback invoked to dismiss the help card.
 * @param onEditQuestion    Callback invoked to edit a security question.
 * @param onDeleteQuestion  Callback invoked to delete a security question.
 * @param onShowHelp        Callback invoked to show the help dialog.
 * @param onAddQuestion     Callback invoked to add a new question.
 * @param modifier          Modifier.
 */
@Composable
private fun SecurityQuestionList(
    questions: List<SecurityQuestionUiDto>,
    isHelpCardVisible: Boolean,
    isSetup: Boolean,
    onDismissHelpCard: () -> Unit,
    onEditQuestion: (SecurityQuestionUiDto) -> Unit,
    onDeleteQuestion: (SecurityQuestionUiDto) -> Unit,
    onShowHelp: () -> Unit,
    onAddQuestion: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        item {
            AnimatedVisibility(isHelpCardVisible) {
                HelpCard(
                    text = if (isSetup) {
                        stringResource(R.string.securityQuestion_helpSetup)
                    } else {
                        stringResource(R.string.securityQuestion_helpEdit)
                    },
                    onDismiss = onDismissHelpCard,
                    modifier = Modifier.padding(
                        start = dimensionResource(R.dimen.margin_horizontal),
                        end = dimensionResource(R.dimen.margin_horizontal),
                        bottom = dimensionResource(R.dimen.padding_vertical)
                    )
                )
            }
            InfoCard(
                securityQuestionsCount = questions.size,
                onAddQuestion = onAddQuestion
            )
            if (questions.isEmpty()) {
                EmptyPlaceholder(
                    title = stringResource(R.string.securityQuestion_emptyPlaceholder_title),
                    subtitle = stringResource(R.string.securityQuestion_emptyPlaceholder_subtitle),
                    painter = painterResource(R.drawable.el_questions),
                    modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_vertical))
                )
            }
        }
        items(questions) { question ->
            SecurityQuestionItem(
                question = question,
                onEdit = onEditQuestion,
                onDelete = onDeleteQuestion,
                onShowHelp = onShowHelp
            )
        }
    }
}


/**
 * Displays a security question within the list of security questions.
 *
 * @param question      Security question to display.
 * @param onDelete      Callback invoked to edit the question.
 * @param onDelete      Callback invoked to delete the question.
 * @param onShowHelp    Callback invoked to show the help dialog.
 */
@Composable
private fun SecurityQuestionItem(
    question: SecurityQuestionUiDto,
    onEdit: (SecurityQuestionUiDto) -> Unit,
    onDelete: (SecurityQuestionUiDto) -> Unit,
    onShowHelp: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onEdit(question)
            }
            .padding(
                start = dimensionResource(R.dimen.margin_horizontal),
                top = dimensionResource(R.dimen.padding_vertical),
                end = dimensionResource(R.dimen.margin_horizontal) - 12.dp,
                bottom = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = dimensionResource(R.dimen.padding_horizontal))
        ) {
            Text(
                text = stringArrayResource(R.array.securityQuestion_questions)[question.question.ordinal],
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
            if (question.answer != null) {
                Text(
                    text = question.answer,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        val contextActions: MutableList<ContextActionBase> = mutableListOf(
            ContextAction(
                text = stringResource(R.string.securityQuestion_edit),
                icon = painterResource(R.drawable.ic_edit)
            ) {
                onEdit(question)
            },
            ContextAction(
                text = stringResource(R.string.securityQuestion_delete),
                icon = painterResource(R.drawable.ic_delete)
            ) {
                onDelete(question)
            }
        )
        if (question.answer == null) {
            contextActions.add(ContextActionDivider())
            contextActions.add(
                ContextAction(
                    text = stringResource(R.string.securityQuestion_noAnswerDialog_title),
                    icon = painterResource(R.drawable.ic_info_outlined)
                ) {
                    onShowHelp()
                }
            )
        }
        ContextActions(contextActions)
    }
}


@Composable
private fun ButtonRow(
    isContinueEnabled: Boolean,
    onContinue: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalDivider()
        FlowRow(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.margin_horizontal),
                    vertical = dimensionResource(R.dimen.padding_vertical)
                )
        ) {
            TextButton(
                onClick = onSkip
            ) {
                Text(stringResource(R.string.button_skip))
            }
            Button(
                onClick = onContinue,
                enabled = isContinueEnabled,
                modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_horizontal))
            ) {
                Text(stringResource(R.string.button_continue))
            }
        }
    }
}


/**
 * Information card which shows to the user, whether the recovery is active.
 *
 * @param securityQuestionsCount    Number of security questions configured.
 * @param onAddQuestion             Callback invoked to add a new security question.
 * @param modifier                  Modifier.
 */
@Composable
private fun InfoCard(
    securityQuestionsCount: Int,
    onAddQuestion: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = modifier
            .padding(
                start = dimensionResource(R.dimen.margin_horizontal),
                end = dimensionResource(R.dimen.margin_horizontal),
                bottom = dimensionResource(R.dimen.padding_vertical),
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.margin_horizontal),
                    vertical = dimensionResource(R.dimen.padding_vertical)
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.image_m))
                        .clip(CircleShape)
                        .background(
                            color = if (securityQuestionsCount >= 5) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.errorContainer
                            }
                        )
                ) {
                    Icon(
                        painter = if (securityQuestionsCount >= 5) {
                            painterResource(R.drawable.ic_shield_check)
                        } else {
                            painterResource(R.drawable.ic_shield_warning)
                        },
                        contentDescription = "",
                        tint = if (securityQuestionsCount >= 5) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onErrorContainer
                        },
                        modifier = Modifier.size(dimensionResource(R.dimen.image_s))
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = dimensionResource(R.dimen.padding_horizontal))
                ) {
                    Text(
                        text = if (securityQuestionsCount >= 5) {
                            stringResource(R.string.securityQuestion_activeTitle)
                        } else {
                            stringResource(R.string.securityQuestion_inactiveTitle)
                        },
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = if (securityQuestionsCount >= 5) {
                            stringResource(R.string.securityQuestion_activeSubtitle, securityQuestionsCount)
                        } else {
                            pluralStringResource(R.plurals.securityQuestion_inactiveSubtitle, 5 - securityQuestionsCount, 5 - securityQuestionsCount)
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            AnimatedVisibility(securityQuestionsCount < 5) {
                TextButton(
                    onClick = onAddQuestion,
                    enabled = securityQuestionsCount < 5,
                    modifier = Modifier.padding(
                        start = dimensionResource(R.dimen.image_m) + dimensionResource(R.dimen.padding_horizontal) - ButtonDefaults.TextButtonContentPadding.calculateStartPadding(LocalLayoutDirection.current)
                    )
                ) {
                    Text(stringResource(R.string.button_addSecurityQuestion))
                }
            }
        }
    }
}


/**
 * Dialog through which to add or edit a security question.
 *
 * @param securityQuestion          Security question to edit. Pass null to create a new one.
 * @param selectedSecurityQuestions List of security questions that are already selected.
 * @param onDismiss                 Callback invoked to dismiss the dialog.
 * @param onSave                    Callback invoked to save the security question and answer specified.
 */
@Composable
private fun SecurityQuestionDialog(
    securityQuestion: SecurityQuestion?,
    selectedSecurityQuestions: List<SecurityQuestion>,
    answer: String? = null,
    onDismiss: () -> Unit,
    onSave: (SecurityQuestion, String) -> Unit
) {
    val availableSecurityQuestions: List<SecurityQuestion> = remember { SecurityQuestion.entries - selectedSecurityQuestions }
    var mutableAnswer: String by remember { mutableStateOf(answer ?: "") }
    var selectedQuestionIndex: Int by remember { mutableIntStateOf(if (securityQuestion != null) { availableSecurityQuestions.indexOf(securityQuestion) } else { 0 }) }

    val invokeOnSave: () -> Unit = {
        if (mutableAnswer.isNotBlank()) {
            onSave(availableSecurityQuestions[selectedQuestionIndex], mutableAnswer)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = false
        )
    ) {
        Card(
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Text(
                    text = if (securityQuestion == null) {
                        stringResource(R.string.securityQuestion_edit_titleAdd)
                    } else {
                        stringResource(R.string.securityQuestion_edit_titleEdit)
                    },
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                DropdownInput(
                    items = availableSecurityQuestions.map { stringArrayResource(R.array.securityQuestion_questions)[it.ordinal] },
                    selectedItemIndex = selectedQuestionIndex,
                    onSelectedItemIndexChange = {
                        selectedQuestionIndex = it
                    },
                    label = stringResource(R.string.securityQuestion_edit_labelQuestion),
                    prefixIcon = painterResource(R.drawable.ic_question),
                    modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_vertical))
                )

                TextInput(
                    value = mutableAnswer,
                    onValueChange = {
                        mutableAnswer = it
                    },
                    label = stringResource(R.string.securityQuestion_edit_labelAnswer),
                    prefixIcon = painterResource(R.drawable.ic_text),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            invokeOnSave()
                        }
                    )
                )

                FlowRow(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.align(Alignment.End).padding(top = 24.dp)
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text(stringResource(R.string.button_cancel))
                    }
                    TextButton(
                        onClick = {
                            invokeOnSave()
                        },
                        enabled = mutableAnswer.isNotBlank()
                    ) {
                        Text(stringResource(R.string.button_save))
                    }
                }
            }
        }
    }
}


/**
 * Displays an informational dialog.
 *
 * @param title     Title for the dialog.
 * @param text      Text for the dialog.
 * @param onDismiss Callback invoked to dismiss the dialog.
 */
@Composable
private fun InfoDialog(
    title: String,
    text: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Card(
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(
                        top = 16.dp,
                        bottom = 24.dp
                    )
                )

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(stringResource(R.string.button_ok))
                }
            }
        }
    }
}
