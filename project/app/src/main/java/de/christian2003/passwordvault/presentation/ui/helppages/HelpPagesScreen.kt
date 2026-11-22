package de.christian2003.passwordvault.presentation.ui.helppages

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import de.christian2003.core.ui.composables.EmptyPlaceholder
import de.christian2003.core.ui.composables.HelpCard
import de.christian2003.core.ui.composables.NavigationBarProtection
import de.christian2003.passwordvault.R
import de.christian2003.passwordvault.domain.entities.HelpPage
import de.christian2003.passwordvault.presentation.models.other.LocaleDrawable
import de.christian2003.passwordvault.presentation.viewmodels.HelpPagesViewModel


/**
 * Screen through which users can view all help pages that are available.
 *
 * @param viewModel     View model.
 * @param onNavigateUp  Callback to navigate up the nav stack.
 */
@Composable
internal fun HelpPagesScreen(
    viewModel: HelpPagesViewModel,
    onNavigateUp: () -> Unit
) {
    val context: Context = LocalContext.current

    Scaffold(
        topBar = {
            TopBar(
                onNavigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        val bottomPadding: Dp = innerPadding.calculateBottomPadding()

        val modifier = Modifier
            .fillMaxSize()
            .padding(
                start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                top = innerPadding.calculateTopPadding(),
                end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
            )

        when {
            viewModel.isLoading -> {
                //Display loading indicator:
                ContentLoadingIndicator(
                    bottomPadding = bottomPadding,
                    modifier = modifier
                )
            }
            viewModel.helpPages.isEmpty() -> {
                //Display empty placeholder:
                ContentEmptyPlaceholder(
                    bottomPadding = bottomPadding,
                    isHelpCardVisible = viewModel.isHelpCardVisible,
                    onDismissHelpCard = {
                        viewModel.dismissHelpCard()
                    },
                    modifier = modifier
                )
            }
            else -> {
                //Display help pages:
                ContentHelpPages(
                    bottomPadding = bottomPadding,
                    helpPages = viewModel.helpPages,
                    isHelpCardVisible = viewModel.isHelpCardVisible,
                    imageLoader = viewModel.imageLoader,
                    onClick = { helpPage ->
                        val intent = Intent(Intent.ACTION_VIEW, helpPage.pageUrl)
                        context.startActivity(intent)
                    },
                    onDismissHelpCard = {
                        viewModel.dismissHelpCard()
                    },
                    onQueryImageRequest = { url ->
                        viewModel.queryImageRequest(url)
                    },
                    modifier = modifier
                )
            }
        }

        NavigationBarProtection(bottomPadding)
    }
}


/**
 * Screen content displays a loading indicator while info about available pages is fetched.
 *
 * @param bottomPadding Bottom padding.
 * @param modifier      Modifier.
 */
@Composable
private fun ContentLoadingIndicator(
    bottomPadding: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.padding(bottom = bottomPadding)
    ) {
        CircularWavyProgressIndicator()
    }
}


/**
 * Screen content displays an empty placeholder if info about available help pages cannot be fetched
 * (e.g. because no network available).
 *
 * @param bottomPadding     Bottom padding.
 * @param isHelpCardVisible Whether the help card is visible.
 * @param onDismissHelpCard Callback to dismiss the help card.
 * @param modifier          Modifier.
 */
@Composable
private fun ContentEmptyPlaceholder(
    bottomPadding: Dp,
    isHelpCardVisible: Boolean,
    onDismissHelpCard: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isHelpCardVisible) {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
        ) {
            HelpCard(
                text = stringResource(R.string.help_pages_help),
                onDismiss = onDismissHelpCard,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                    vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                )
            )
            EmptyPlaceholder(
                title = stringResource(R.string.help_pages_emptyPlaceholder_title),
                subtitle = stringResource(R.string.help_pages_emptyPlaceholder_subtitle),
                painter = painterResource(R.drawable.el_help),
                modifier = Modifier.padding(
                    vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                )
            )
            Box(
                modifier = Modifier.height(bottomPadding)
            )
        }
    }
    else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.padding(bottom = bottomPadding)
        ) {
            EmptyPlaceholder(
                title = stringResource(R.string.help_pages_emptyPlaceholder_title),
                subtitle = stringResource(R.string.help_pages_emptyPlaceholder_subtitle),
                painter = painterResource(R.drawable.el_help),
                modifier = Modifier.padding(
                    vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                )
            )
        }
    }
}


/**
 * Screen content displays a list of available help pages.
 *
 * @param bottomPadding         Bottom padding.
 * @param helpPages             List of help pages to display.
 * @param isHelpCardVisible     Whether the help card is visible.
 * @param imageLoader           Image loader.
 * @param onClick               Callback once a help page is clicked.
 * @param onDismissHelpCard     Callback to dismiss the help card.
 * @param onQueryImageRequest   Callback to query web image request.
 * @param modifier              Modifier.
 */
@Composable
private fun ContentHelpPages(
    bottomPadding: Dp,
    helpPages: List<HelpPage>,
    isHelpCardVisible: Boolean,
    imageLoader: ImageLoader,
    onClick: (HelpPage) -> Unit,
    onDismissHelpCard: () -> Unit,
    onQueryImageRequest: (Uri) -> ImageRequest,
    modifier: Modifier = Modifier
) {
    val cardShape: Shape = MaterialTheme.shapes.large

    LazyColumn(
        modifier = modifier
    ) {
        item {
            AnimatedVisibility (isHelpCardVisible) {
                HelpCard(
                    text = stringResource(R.string.help_pages_help),
                    onDismiss = onDismissHelpCard,
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                        vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                    )
                )
            }
        }

        itemsIndexed(helpPages) { index, helpPage ->
            Card(
                shape = cardShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                        vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onClick(helpPage)
                        }
                ) {
                    AsyncImage(
                        model = onQueryImageRequest(helpPage.previewImageUrl),
                        imageLoader = imageLoader,
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(cardShape)
                    )
                    Text(
                        text = helpPage.title,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal),
                                vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                            )
                    )
                    Text(
                        text = helpPage.description,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal)
                            )
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal),
                                vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                            )
                    ) {
                        Image(
                            painter = painterResource(LocaleDrawable.forLocale(helpPage.lang).drawable),
                            contentDescription = "",
                            modifier = Modifier
                                .height(24.dp)
                                .padding(end = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
                                .clip(MaterialTheme.shapes.extraSmall)
                        )
                        Text(
                            text = if (helpPage.isInCurrentLang) {
                                stringResource(R.string.help_pages_lang_current)
                            } else {
                                stringResource(R.string.help_pages_lang_default)
                            },
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        item {
            Box(modifier = Modifier.height(bottomPadding))
        }
    }
}


/**
 * Top bar of the screen.
 *
 * @param onNavigateUp  Callback to navigate up the nav stack.
 */
@Composable
private fun TopBar(
    onNavigateUp: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.help_pages_title),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigateUp
            ) {
                Icon(
                    painter = painterResource(de.christian2003.core.ui.R.drawable.ic_back),
                    contentDescription = ""
                )
            }
        }
    )
}
