package de.christian2003.feature.analysis.presentation.ui.analysis

import android.graphics.drawable.Drawable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import de.christian2003.core.ui.composables.HelpCard
import de.christian2003.core.ui.composables.ListItemContainer
import de.christian2003.core.ui.composables.Shape
import de.christian2003.data.accounts.domain.entities.AccountDescriptor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid
import de.christian2003.feature.analysis.R


/**
 * List item displays an account descriptor.
 *
 * @param reusedPasswords           Reused passwords.
 * @param isHelpCardVisible         Whether the help card is visible.
 * @param helpMessage               Message for the help card.
 * @param onQueryAccountDescriptor  Callback invoke to query account descriptors.
 * @param onQueryAccountIcon        Callback invoked to query account icons.
 * @param onNavigateToAccount       Callback to navigate to an account.
 * @param onDismissHelpCard         Callback to dismiss the help card.
 * @param onDismiss                 Callback to dismiss the sheet.
 */
@Composable
internal fun ReusedPasswordsSheet(
    reusedPasswords: Map<String, List<Uuid>>,
    isHelpCardVisible: Boolean,
    helpMessage: String,
    onQueryAccountDescriptor: suspend (Uuid) -> AccountDescriptor?,
    onQueryAccountIcon: (AccountDescriptor) -> Drawable?,
    onNavigateToAccount: (Uuid) -> Unit,
    onDismissHelpCard: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val defaultInsets = BottomSheetDefaults.windowInsets

    val invokeOnDismiss: () -> Unit = {
        coroutineScope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        sheetGesturesEnabled = false,
        dragHandle = null,
        contentWindowInsets = {
            WindowInsets(
                left = defaultInsets.getLeft(LocalDensity.current, LocalLayoutDirection.current),
                top = defaultInsets.getTop(LocalDensity.current),
                right = defaultInsets.getRight(LocalDensity.current, LocalLayoutDirection.current)
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    onDismiss = invokeOnDismiss
                )
            }
        ) { innerPadding ->
            val bottomPadding: Dp = innerPadding.calculateBottomPadding()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                        top = innerPadding.calculateTopPadding(),
                        end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                    )
            ) {
                item {
                    AnimatedVisibility(isHelpCardVisible) {
                        HelpCard(
                            text = helpMessage,
                            onDismiss = onDismissHelpCard,
                            modifier = Modifier.padding(horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal))
                        )
                    }
                }

                reusedPasswords.forEach { _, accountIds ->
                    item {
                        SectionHeader(
                            numberOfAccounts = accountIds.size,
                            modifier = Modifier.padding(top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
                        )
                    }
                    itemsIndexed(accountIds) { index, accountId ->
                        AccountDescriptor(
                            accountId = accountId,
                            isFirst = index == 0,
                            isLast = index == accountIds.size - 1,
                            onQueryAccountDescriptor = onQueryAccountDescriptor,
                            onQueryAccountIcon = onQueryAccountIcon,
                            onNavigateToAccount = onNavigateToAccount
                        )
                    }
                }

                item {
                    Box(modifier = Modifier.height(bottomPadding))
                }
            }
        }
    }
}


/**
 * Section header displays how many accounts use a password.
 *
 * @param numberOfAccounts  Number of accounts using the password of the section.
 * @param modifier          Modifier.
 */
@Composable
private fun SectionHeader(
    numberOfAccounts: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.analysis_reused_sectionHeader, numberOfAccounts),
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
            )
    )
}


/**
 * List item displays an account descriptor.
 *
 * @param accountId                 ID of the account to display.
 * @param isFirst                   Whether the item is the first in the list.
 * @param isLast                    Whether the item is the last in the list.
 * @param onQueryAccountDescriptor  Callback invoke to query account descriptors.
 * @param onQueryAccountIcon        Callback invoked to query account icons.
 * @param onNavigateToAccount       Callback to navigate to an account.
 */
@Composable
private fun AccountDescriptor(
    accountId: Uuid,
    isFirst: Boolean,
    isLast: Boolean,
    onQueryAccountDescriptor: suspend (Uuid) -> AccountDescriptor?,
    onQueryAccountIcon: (AccountDescriptor) -> Drawable?,
    onNavigateToAccount: (Uuid) -> Unit
) {
    val accountDescriptor: AccountDescriptor? by produceState(null) {
        value = onQueryAccountDescriptor(accountId)
    }

    ListItemContainer(
        isFirst = isFirst,
        isLast = isLast
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onNavigateToAccount(accountId)
                    }
                    .padding(
                        horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal),
                        vertical  = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                    )
            ) {
                //Icon:
                if (accountDescriptor != null) {
                    val icon: Drawable? = onQueryAccountIcon(accountDescriptor!!)
                    if (icon == null) {
                        val firstChar: Char? = accountDescriptor!!.name.firstOrNull { !it.isWhitespace() }
                        Shape(
                            shape = MaterialShapes.Clover8Leaf,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_m))
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
                            painter = rememberDrawablePainter(icon),
                            contentDescription = "",
                            modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_m))
                        )
                    }
                }

                //Account:
                Column(
                    modifier = Modifier
                        .padding(horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
                        .weight(1f)
                ) {
                    Text(
                        text = accountDescriptor?.name ?: "",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if ((accountDescriptor?.description ?: "").isNotEmpty()) {
                        Text(
                            text = accountDescriptor?.description ?: "",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}


/**
 * Top bar for the sheet.
 *
 * @param onDismiss Callback invoked to dismiss the sheet.
 */
@Composable
private fun TopBar(
    onDismiss: () -> Unit
) {
    TopAppBar(
        title = {
            Text(stringResource(R.string.analysis_overview_reuse_title))
        },
        navigationIcon = {
            IconButton(
                onClick = onDismiss
            ) {
                Icon(
                    painter = painterResource(de.christian2003.core.ui.R.drawable.ic_cancel),
                    contentDescription = ""
                )
            }
        }
    )
}
