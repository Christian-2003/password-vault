package de.christian2003.passwordvault.ui.help

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.christian2003.core.ui.composables.HelpCard
import de.christian2003.core.ui.composables.ListItemContainer
import de.christian2003.core.ui.composables.NavigationBarProtection
import de.christian2003.core.ui.composables.Shape
import de.christian2003.core.ui.model.HelpCard
import de.christian2003.passwordvault.R
import de.christian2003.passwordvault.viewmodels.HelpViewModel


/**
 * Screen displays a list of all help messages that help the user understand functionalities all over
 * the app. Through this screen, a user can reactivate a help message if they have dismissed it
 * previously.
 *
 * @param viewModel     View model.
 * @param onNavigateUp  Callback invoked to navigate up on the navigation stack.
 */
@Composable
fun HelpScreen(
    viewModel: HelpViewModel,
    onNavigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.help_title))
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
                AnimatedVisibility(viewModel.helpCards[HelpCard.Help] == true) {
                    HelpCard(
                        text = stringResource(R.string.help_help),
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
            val helpCards = viewModel.helpCards.toList()
            itemsIndexed(helpCards) { index, (helpCard, visible) ->
                HelpListItem(
                    helpCard = helpCard,
                    visible = visible,
                    isFirst = index == 0,
                    isLast = index == helpCards.size - 1,
                    onClick = { helpCard ->
                        viewModel.toggleHelpCardVisibility(helpCard)
                    }
                )
            }
            item {
                Box(
                    modifier = Modifier.height(bottomPadding)
                )
            }
        }

        NavigationBarProtection(bottomPadding)
    }
}


/**
 * Shows an item in the list which displays the state of all help messages.
 *
 * @param helpCard  Help card for which to display an item.
 * @param visible   Whether the help message is visible.
 * @param isFirst   Whether this is the first list item.
 * @param isLast    Whether this is the last list item.
 * @param onClick   List item was clicked (i.e. the help message should be toggled to be visible).
 */
@Composable
private fun HelpListItem(
    helpCard: HelpCard,
    visible: Boolean,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: (HelpCard) -> Unit
) {
    ListItemContainer(
        isFirst = isFirst,
        isLast = isLast
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick(helpCard)
                }
                .padding(
                    horizontal = dimensionResource(R.dimen.padding_horizontal),
                    vertical = dimensionResource(R.dimen.padding_vertical)
                )
        ) {
            Shape(
                shape = MaterialShapes.Cookie12Sided,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .padding(end = dimensionResource(R.dimen.padding_horizontal))
                    .size(dimensionResource(R.dimen.image_m))
            ) {
                Icon(
                    painter = if (visible) {
                        painterResource(de.christian2003.core.ui.R.drawable.ic_visibility_on)
                    } else {
                        painterResource(de.christian2003.core.ui.R.drawable.ic_visibility_off)
                    },
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = "",
                    modifier = Modifier.size(dimensionResource(R.dimen.image_xs))
                )
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringArrayResource(R.array.help_shortNames)[helpCard.ordinal],
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = if (visible) { stringResource(R.string.help_visibleLabel) } else { stringResource(R.string.help_dismissedLabel) },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Checkbox(
                checked = visible,
                onCheckedChange = {
                    onClick(helpCard)
                },
                modifier = Modifier
                    .padding(start = dimensionResource(R.dimen.padding_horizontal))
                    .size(24.dp)
            )
        }
    }
}
