package de.passwordvault.view.settings.activity_recovery

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import de.passwordvault.R
import de.passwordvault.model.security.login.SecurityQuestion
import de.passwordvault.ui.composables.BottomSheetDialog
import de.passwordvault.ui.composables.DeleteDialog
import de.passwordvault.ui.composables.DropdownInput
import de.passwordvault.ui.composables.EmptyPlaceholder
import de.passwordvault.ui.composables.Headline
import de.passwordvault.ui.composables.HelpCard
import de.passwordvault.ui.composables.TextInput
import kotlinx.coroutines.launch


/**
 * Screen through which to configure recovery methods for the account.
 *
 * @param viewModel     View model for the screen.
 * @param onNavigateUp  Callback invoked to navigate up on the navigation stack.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoveryScreen(
    viewModel: RecoveryViewModel,
    onNavigateUp: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    AppBarTitle(
                        collapsedFraction = scrollBehavior.state.collapsedFraction,
                        amount = viewModel.securityQuestions.size
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onNavigateUp()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = ""
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        //Display list of security questions:
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
        ) {
            item {
                AnimatedVisibility(viewModel.isHelpMode && viewModel.securityQuestions.size < 5) {
                    HelpCard(
                        text = stringResource(R.string.recovery_info),
                        onDismiss = {
                            viewModel.dismissHelpMode()
                        }
                    )
                }
                Headline(
                    title = stringResource(R.string.recovery_questions),
                    isEyecatcherVisible = viewModel.isHelpMode && viewModel.securityQuestions.size < 5,
                    endIcon = painterResource(R.drawable.ic_add),
                    onClick = {
                        viewModel.editedSecurityQuestion = SecurityQuestion(-1, "")
                    }
                )
            }
            if (viewModel.securityQuestions.isEmpty()) {
                item {
                    EmptyPlaceholder(
                        title = stringResource(R.string.recovery_questions_empty_headline),
                        text = stringResource(R.string.recovery_questions_empty_support),
                        painter = painterResource(R.drawable.el_security_question),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = dimensionResource(R.dimen.padding_horizontal),
                                vertical = dimensionResource(R.dimen.padding_vertical)
                            )
                    )
                }
            }
            else {
                items(viewModel.securityQuestions) { securityQuestion ->
                    SecurityQuestionListRow(
                        securityQuestion = securityQuestion,
                        onEdit = {
                            viewModel.editedSecurityQuestion = it
                        },
                        onDelete = {
                            viewModel.deleteSecurityQuestion = it
                        }
                    )
                }
            }
        }

        //Display dialog to edit security question:
        if (viewModel.editedSecurityQuestion != null) {
            SecurityQuestionDialog(
                question = viewModel.editedSecurityQuestion!!.question,
                answer = viewModel.editedSecurityQuestion!!.answer,
                securityQuestions = viewModel.getAvailableSecurityQuestions(),
                onDismiss = {
                    viewModel.editedSecurityQuestion = null
                },
                onSave = { question, answer ->
                    viewModel.saveSecurityQuestion(question, answer)
                }
            )
        }

        //Display dialog to delete security question:
        if (viewModel.deleteSecurityQuestion != null) {
            DeleteDialog(
                message = stringResource(R.string.recovery_delete_message),
                onCancel = {
                    viewModel.deleteSecurityQuestion = null
                },
                onConfirm = {
                    viewModel.deleteSecurityQuestion(viewModel.deleteSecurityQuestion!!)
                }
            )
        }
    }
}


/**
 * Displays the title for the app bar.
 *
 * @param collapsedFraction Fraction to which the app bar is collapsed.
 * @param amount            Number of security questions that are configured.
 */
@Composable
private fun AppBarTitle(
    collapsedFraction: Float,
    amount: Int
) {
    val collapsedThreshold = 0.7f
    val stateText = if (collapsedFraction > collapsedThreshold) {
        stringResource(R.string.recovery_subtitle_collapsed).replace("{arg}", amount.toString())
    } else {
        if (amount == 0) {
            stringResource(R.string.recovery_subtitle_expanded_disabled)
        } else if (amount >= 5) {
            stringResource(R.string.recovery_subtitle_expanded_enabled)
        } else if (amount == 4) {
            stringResource(R.string.recovery_subtitle_expanded_disabledSingular).replace("{arg}", (5 - amount).toString())
        } else {
            stringResource(R.string.recovery_subtitle_expanded_disabledPlural).replace("{arg}", (5 - amount).toString())
        }
    }

    Column {
        Text(
            text = stringResource(R.string.recovery_title),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = stateText,
            color = if (amount >= 5) { MaterialTheme.colorScheme.onSurfaceVariant } else { MaterialTheme.colorScheme.error },
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            maxLines = if (collapsedFraction > collapsedThreshold) { 1 } else { Int.MAX_VALUE },
            overflow = TextOverflow.Ellipsis
        )
    }
}


/**
 * Composable displays a single security question within the list of security questions.
 *
 * @param securityQuestion  Security question to display.
 * @param onEdit            Callback invoked to edit the security question.
 * @param onDelete          Callback invoked to delete the security question.
 */
@Composable
private fun SecurityQuestionListRow(
    securityQuestion: SecurityQuestion,
    onEdit: (SecurityQuestion) -> Unit,
    onDelete: (SecurityQuestion) -> Unit
) {
    var isExpanded by remember { mutableStateOf(securityQuestion.isExpanded) }
    var isDropdownVisible by remember { mutableStateOf(false) }
    val arrowAnimator by animateFloatAsState(
        targetValue = if (isExpanded) { 180F } else { 0F },
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        )
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                isExpanded = !isExpanded
                securityQuestion.isExpanded = isExpanded
            }
            .padding(
                start = dimensionResource(R.dimen.padding_horizontal),
                top = dimensionResource(R.dimen.padding_vertical),
                end = dimensionResource(R.dimen.padding_horizontal_end_button),
                bottom = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringArrayResource(R.array.security_questions)[securityQuestion.question],
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
            Icon(
                painter = painterResource(R.drawable.ic_expand),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = "",
                modifier = Modifier.rotate(arrowAnimator)
            )
            AnimatedVisibility(isExpanded) {
                Text(
                    text = securityQuestion.answer,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Box {
            IconButton(
                onClick = {
                    isDropdownVisible = true
                },
                modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_horizontal_between))
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_more),
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = ""
                )
            }
            DropdownMenu(
                expanded = isDropdownVisible,
                onDismissRequest = {
                    isDropdownVisible = false
                }
            ) {
                DropdownMenuItem(
                    text = {
                        Text(stringResource(R.string.button_edit))
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_edit),
                            contentDescription = ""
                        )
                    },
                    onClick = {
                        isDropdownVisible = false
                        onEdit(securityQuestion)
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(stringResource(R.string.button_delete))
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_delete),
                            contentDescription = ""
                        )
                    },
                    onClick = {
                        isDropdownVisible = false
                        onDelete(securityQuestion)
                    }
                )
            }
        }
    }
}


/**
 * Composable displays a dialog through which to edit the security question.
 *
 * @param question          Question to edit.
 * @param answer            Answer to edit.
 * @param securityQuestions List of security questions to display.
 * @param onDismiss         Callback invoked to dismiss the dialog.
 * @param onSave            Callback invoked to save the security question.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun SecurityQuestionDialog(
    question: Int,
    answer: String,
    securityQuestions: List<String>,
    onDismiss: () -> Unit,
    onSave: (Int, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var question: Int by rememberSaveable { mutableIntStateOf(question) }
    var answer: String by rememberSaveable { mutableStateOf(answer) }
    var answerError: Boolean by rememberSaveable { mutableStateOf(false) }
    var questionError: Boolean by rememberSaveable { mutableStateOf(false) }
    val allQuestions: Array<String> = stringArrayResource(R.array.security_questions)

    BottomSheetDialog(
        sheetState = sheetState,
        title = stringResource(R.string.recovery_configure_question),
        onDismiss = onDismiss,
        icon = painterResource(R.drawable.ic_edit),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.padding_horizontal),
                    vertical = dimensionResource(R.dimen.padding_vertical)
                )
        ) {
            DropdownInput(
                selected = if (question != -1) { stringArrayResource(R.array.security_questions)[question] } else { null },
                onSelectedChange = {
                    var i = 0
                    allQuestions.forEach { q ->
                        if (q == it) {
                            question = i
                        }
                        i++
                    }
                },
                label = stringResource(R.string.recovery_question),
                options = securityQuestions,
                prefixIcon = painterResource(R.drawable.ic_security_question),
                errorMessage = if (questionError) { stringResource(R.string.error_empty_input) } else { null }
            )
            TextInput(
                value = answer,
                onValueChange = {
                    answer = it
                },
                label = stringResource(R.string.recovery_answer),
                prefixIcon = painterResource(R.drawable.ic_answer),
                errorMessage = if (answerError) { stringResource(R.string.error_empty_input) } else { null },
                modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_vertical))
            )
            FlowRow(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = dimensionResource(R.dimen.padding_vertical))
            ) {
                TextButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            onDismiss()
                        }
                    }
                ) {
                    Text(text = stringResource(R.string.button_cancel))
                }
                TextButton(
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_horizontal_between)),
                    onClick = {
                        answerError = answer.isEmpty()
                        questionError = question == -1

                        if (!answerError && !questionError) {
                            scope.launch {
                                sheetState.hide()
                            }.invokeOnCompletion {
                                onSave(question, answer)
                            }
                        }
                    }
                ) {
                    Text(text = stringResource(R.string.button_save))
                }
            }
        }
    }
}
