package de.christian2003.core.ui.composables.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import de.christian2003.core.ui.model.AccountUiDto
import kotlin.uuid.Uuid
import de.christian2003.core.ui.R
import de.christian2003.core.ui.composables.Shape
import java.util.UUID
import kotlin.uuid.toKotlinUuid


/**
 * Dialog through which a list of accounts is displayed and the user can select accounts from this
 * list.
 *
 * @param title             Title for the dialog.
 * @param accounts          List of accounts to display.
 * @param selectedAccounts  IDs of the selected accounts.
 * @param onSave            Invoked once the dialog is dismissed and the selected accounts are to be
 *                          saved.
 * @param onDismiss         Invoked to dismiss the dialog without saving.
 * @param confirmButtonText Text for the button to save the dialog.
 * @param dismissButtonText Text for the button to dismiss the dialog.
 */
@Composable
fun AccountSelectorDialog(
    title: String,
    accounts: List<AccountUiDto>,
    selectedAccounts: Set<Uuid>,
    onSave: (Set<Uuid>) -> Unit,
    onDismiss: () -> Unit,
    confirmButtonText: String = stringResource(R.string.button_ok),
    dismissButtonText: String = stringResource(R.string.button_cancel)
) {
    val mutableSetSaver = Saver<MutableSet<Uuid>, List<String>>(
        save = { set -> set.map { it.toString() } },
        restore = { list -> list.map { UUID.fromString(it).toKotlinUuid() }.toMutableSet() }
    )

    val selectedAccountIds: MutableSet<Uuid> = rememberSaveable(saver = mutableSetSaver) {
        val mutableSet: MutableSet<Uuid> = mutableStateSetOf()
        mutableSet.addAll(selectedAccounts)
        return@rememberSaveable mutableSet
    }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(
                            start = 24.dp,
                            end = 24.dp,
                            bottom = 16.dp
                        )
                )
                HorizontalDivider()

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(accounts) { account ->
                        AccountListRow(
                            account = account,
                            isSelected = selectedAccountIds.contains(account.id),
                            onSelectedChange = { isSelected ->
                                if (isSelected) {
                                    selectedAccountIds.add(account.id)
                                }
                                else {
                                    selectedAccountIds.remove(account.id)
                                }
                            }
                        )
                    }
                }

                HorizontalDivider()
                FlowRow(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(
                            start = 24.dp,
                            top = 16.dp,
                            end = 24.dp
                        )
                ) {
                    TextButton(
                        onClick = {
                            onDismiss()
                        }
                    ) {
                        Text(dismissButtonText)
                    }
                    TextButton(
                        onClick = {
                            onSave(selectedAccountIds)
                        },
                        modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_horizontal))
                    ) {
                        Text(confirmButtonText)
                    }
                }
            }
        }
    }
}


/**
 * Display a single account in a list.
 *
 * @param account           Account to display.
 * @param isSelected        Whether the account is selected.
 * @param onSelectedChange  Invoked once the account is (de)selected.
 */
@Composable
private fun AccountListRow(
    account: AccountUiDto,
    isSelected: Boolean,
    onSelectedChange: (Boolean) -> Unit
) {
    val invokeOnSelectedChange: (Boolean) -> Unit = { isSelectedNew ->
        if (isSelectedNew != isSelected) {
            onSelectedChange(isSelectedNew)
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                invokeOnSelectedChange(!isSelected)
            }
            .padding(
                start = 24.dp,
                top = dimensionResource(R.dimen.padding_vertical),
                end = 12.dp, //24 - 12 = 12
                bottom = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        //IMAGE
        if (account.icon == null) {
            val firstChar: Char? = account.name.firstOrNull { !it.isWhitespace() }
            Shape(
                shape = MaterialShapes.Clover8Leaf,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(dimensionResource(R.dimen.image_m))
            ) {
                Text(
                    text = firstChar?.toString() ?: "",
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        else {
            Image(
                painter = rememberDrawablePainter(account.icon),
                contentDescription = "",
                modifier = Modifier.size(dimensionResource(R.dimen.image_m))
            )
        }

        //Name, Description:
        Column(
            modifier = Modifier
                .padding(start = dimensionResource(R.dimen.padding_horizontal))
                .weight(1f)
        ) {
            Text(
                text = account.name,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (account.description.isNotBlank()) {
                Text(
                    text = account.description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        //Checkbox:
        Checkbox(
            checked = isSelected,
            onCheckedChange = invokeOnSelectedChange
        )
    }
}
