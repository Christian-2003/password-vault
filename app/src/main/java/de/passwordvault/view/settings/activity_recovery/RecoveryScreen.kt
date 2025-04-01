package de.passwordvault.view.settings.activity_recovery

import androidx.appcompat.widget.PopupMenu.OnDismissListener
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import de.passwordvault.R
import de.passwordvault.model.security.login.SecurityQuestion
import de.passwordvault.ui.composables.BottomSheetDialog
import de.passwordvault.ui.composables.Card
import de.passwordvault.ui.composables.EmptyPlaceholder
import de.passwordvault.ui.composables.Headline
import de.passwordvault.ui.composables.TextInput
import de.passwordvault.view.settings.dialog_security_question.SecurityQuestionDialog
import kotlin.math.exp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoveryScreen(
    viewModel: RecoveryViewModel,
    onNavigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_security_login_recovery),
                        color = MaterialTheme.colorScheme.onSurface
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
                colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
        ) {
            item {
                Card(
                    text = stringResource(R.string.recovery_info)
                )
                Headline(
                    title = stringResource(R.string.recovery_questions),
                    endIcon = painterResource(R.drawable.ic_add),
                    onClick = {
                        //TODO...
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
                                horizontal = dimensionResource(R.dimen.space_horizontal),
                                vertical = dimensionResource(R.dimen.space_vertical)
                            )
                    )
                }
            }
            else {
                items(viewModel.securityQuestions) { securityQuestion ->
                    SecurityQuestionListRow(
                        securityQuestion = securityQuestion,
                        onEdit = { securityQuestion ->
                            viewModel.editedSecurityQuestion = securityQuestion
                        },
                        onDelete = { securityQuestion ->
                            //TODO
                        }
                    )
                }
            }
        }
        if (viewModel.editedSecurityQuestion != null) {
            de.passwordvault.view.settings.activity_recovery.SecurityQuestionDialog(
                question = viewModel.editedSecurityQuestion!!.question,
                answer = viewModel.editedSecurityQuestion!!.answer,
                onQuestionChange = {
                    viewModel.editedSecurityQuestion!!.question = it
                },
                onAnswerChange = {
                    viewModel.editedSecurityQuestion!!.answer = it
                },
                onDismiss = {
                    viewModel.editedSecurityQuestion = null
                },
                onSave = {
                    //TODO
                    viewModel.editedSecurityQuestion = null
                }
            )
        }
    }
}


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
                start = dimensionResource(R.dimen.space_horizontal),
                top = dimensionResource(R.dimen.space_vertical),
                end = dimensionResource(R.dimen.space_horizontal_end_button),
                bottom = dimensionResource(R.dimen.space_vertical)
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
                modifier = Modifier.padding(start = dimensionResource(R.dimen.space_horizontal_between))
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
                        onDelete(securityQuestion)
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SecurityQuestionDialog(
    question: Int,
    answer: String,
    onQuestionChange: (Int) -> Unit,
    onAnswerChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var question: Int by remember { mutableStateOf(question) }
    var answer: String by remember { mutableStateOf(answer) }

    BottomSheetDialog(
        sheetState = sheetState,
        title = "SSS",
        onDismiss = onDismiss,
        icon = painterResource(R.drawable.ic_edit)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.space_horizontal),
                    vertical = dimensionResource(R.dimen.space_vertical)
                )
        ) {
            DropdownInput(
                label = stringResource(R.string.recovery_question),
                options = stringArrayResource(R.array.security_questions).toList()
            )
            TextInput(
                value = answer,
                onValueChange = {
                    answer = it
                },
                label = stringResource(R.string.recovery_answer)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownInput(
    label: String,
    options: List<String>
) {
    var expanded: Boolean by remember { mutableStateOf(false) }
    val textFieldState = rememberTextFieldState(options[0])
    var selectedOption: Int by remember { mutableIntStateOf(0) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = it
        }
    ) {
        OutlinedTextField(
            readOnly = true,
            value = options[selectedOption],
            onValueChange = { },
            label = {
                Text(label)
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        var i = 0
                        options.forEach { o ->
                            if (o == option) {
                                selectedOption = i
                            }
                            i++
                        }
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}
