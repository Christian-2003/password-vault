package de.christian2003.feature.analysis.presentation.ui.analysis

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.graphics.shapes.RoundedPolygon
import de.christian2003.core.ui.composables.NavigationBarProtection
import de.christian2003.core.ui.composables.Shape
import de.christian2003.feature.analysis.presentation.viewmodels.AnalysisViewModel
import kotlin.uuid.Uuid
import de.christian2003.feature.analysis.R
import de.christian2003.feature.analysis.domain.entities.PasswordStrength
import de.christian2003.feature.analysis.domain.entities.SecurityResult
import de.christian2003.feature.analysis.presentation.models.dialogs.AnalysisScreenDialog


/**
 * Screen through which the password security analysis is displayed.
 *
 * @param viewModel             View model.
 * @param onNavigateUp          Callback to navigate up the nav stack.
 * @param onNavigateToAccount   Callback to navigate to an account.
 */
@Composable
internal fun AnalysisScreen(
    viewModel: AnalysisViewModel,
    onNavigateUp: () -> Unit,
    onNavigateToAccount: (Uuid) -> Unit
) {
    val securityResult: SecurityResult? = viewModel.securityResult
    Scaffold(
        topBar = {
            TopBar(
                restartAnalysisEnabled = securityResult != null,
                onNavigateUp = onNavigateUp,
                onRestartAnalysis = {
                    viewModel.startAnalysis()
                }
            )
        }
    ) { innerPadding ->
        val bottomPadding: Dp = innerPadding.calculateBottomPadding()

        if (securityResult == null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                LoadingIndicator()
            }
        }
        else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                        top = innerPadding.calculateTopPadding(),
                        end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                    )
            ) {
                //Hero:
                item {
                    HeroSection(
                        accountsCount = securityResult.analyzedAccounts,
                        modifier = Modifier.padding(
                            start = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                            end = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                            bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                        )
                    )
                }

                //Weak passwords:
                item {
                    var weakAccountsCount = 0
                    if (securityResult.passwordResults.contains(PasswordStrength.Weak)) {
                        weakAccountsCount += securityResult.passwordResults[PasswordStrength.Weak]!!.size
                    }
                    if (securityResult.passwordResults.contains(PasswordStrength.Medium)) {
                        weakAccountsCount += securityResult.passwordResults[PasswordStrength.Medium]!!.size
                    }
                    ResultSection(
                        title = stringResource(R.string.analysis_overview_weak_title),
                        subtitle = stringResource(R.string.analysis_overview_weak_subtitle),
                        accountsCount = weakAccountsCount,
                        painter = painterResource(de.christian2003.core.ui.R.drawable.ic_info),
                        shape = MaterialShapes.Flower,
                        onClick = {
                            viewModel.showWeakPasswordsDialog()
                        },
                        modifier = Modifier.padding(
                            horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal)
                        )
                    )
                }

                //Reused passwords:
                item {
                    var reusedAccountsCount = 0
                    securityResult.reusedPasswords.forEach { _, accountIds ->
                        reusedAccountsCount += accountIds.size
                    }
                    ResultSection(
                        title = stringResource(R.string.analysis_overview_reuse_title),
                        subtitle = stringResource(R.string.analysis_overview_reuse_subtitle),
                        accountsCount = reusedAccountsCount,
                        painter = painterResource(R.drawable.ic_reuse),
                        shape = MaterialShapes.SoftBurst,
                        onClick = {
                            viewModel.showReusedPasswordsDialog()
                        },
                        modifier = Modifier.padding(
                            horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                            vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                        )
                    )
                }

                //Bottom content
                item {
                    if (securityResult.analyzedAccounts > 0) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal))
                        ) {
                            TextButton(
                                onClick = {
                                    viewModel.showAllPasswordsDialog()
                                }
                            ) {
                                Text(stringResource(R.string.analysis_overview_allPasswords))
                            }
                        }
                    }
                }

                //Navigation bar spacer:
                item {
                    Box(modifier = Modifier.height(bottomPadding))
                }
            }
        }

        NavigationBarProtection(bottomPadding)
    }

    when (viewModel.dialog) {
        AnalysisScreenDialog.WeakPasswords, AnalysisScreenDialog.AllPasswords -> {
            PasswordResultsSheet(
                passwordResults = securityResult!!.passwordResults,
                filter = when (viewModel.dialog) {
                    AnalysisScreenDialog.WeakPasswords -> setOf(PasswordStrength.Weak, PasswordStrength.Medium)
                    else -> setOf(PasswordStrength.Weak, PasswordStrength.Medium, PasswordStrength.Strong, PasswordStrength.VeryStrong)
                },
                title = when (viewModel.dialog) {
                    AnalysisScreenDialog.WeakPasswords -> stringResource(R.string.analysis_overview_weak_title)
                    else -> stringResource(R.string.analysis_overview_all_title)
                },
                isHelpCardVisible = when (viewModel.dialog) {
                    AnalysisScreenDialog.WeakPasswords -> viewModel.isWeakPasswordsHelpCardVisible
                    else -> false
                },
                helpMessage = stringResource(R.string.analysis_help_weakPasswords),
                onQueryAccountDescriptor = { accountId ->
                    viewModel.queryAccountDescriptor(accountId)
                },
                onQueryAccountIcon = { accountDescriptor ->
                    viewModel.queryAccountIcon(accountDescriptor)
                },
                onGeneratePositiveColor = { negativeColor, darkTheme ->
                    viewModel.generatePositiveColor(negativeColor, darkTheme)
                },
                onNavigateToAccount = onNavigateToAccount,
                onDismissHelpCard = {
                    viewModel.dismissWeakPasswordsHelpCard()
                },
                onDismiss = {
                    when (viewModel.dialog) {
                        AnalysisScreenDialog.WeakPasswords -> viewModel.dismissWeakPasswordsDialog()
                        else -> viewModel.dismissAllPasswordsDialog()
                    }
                }
            )
        }
        AnalysisScreenDialog.ReusedPasswords -> {
            ReusedPasswordsSheet(
                reusedPasswords = securityResult!!.reusedPasswords,
                isHelpCardVisible = viewModel.isReusedPasswordsHelpCardVisible,
                helpMessage = stringResource(R.string.analysis_help_reusedPasswords),
                onQueryAccountDescriptor = { accountId ->
                    viewModel.queryAccountDescriptor(accountId)
                },
                onQueryAccountIcon = { accountDescriptor ->
                    viewModel.queryAccountIcon(accountDescriptor)
                },
                onNavigateToAccount = onNavigateToAccount,
                onDismissHelpCard = {
                    viewModel.dismissReusedPasswordsHelpCard()
                },
                onDismiss = {
                    viewModel.dismissReusedPasswordsDialog()
                }
            )
        }
        else -> { }
    }
}


/**
 * Hero section of the screen.
 *
 * @param accountsCount Number of analyzed accounts.
 * @param modifier      Modifier.
 */
@Composable
private fun HeroSection(
    accountsCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
            )
    ) {
        Image(
            painter = painterResource(R.drawable.analysis),
            contentDescription = "",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .width(dimensionResource(de.christian2003.core.ui.R.dimen.image_xxl))
        )
        Text(
            text = pluralStringResource(R.plurals.analysis_hero_text, accountsCount, accountsCount),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
        )
    }
}


/**
 * Result section (e.g. "weak passwords" or "reused passwords").
 *
 * @param title         Title for the section.
 * @param subtitle      Subtitle for the section.
 * @param accountsCount Number of accounts.
 * @param painter       Icon painter.
 * @param shape         Icon shape.
 * @param onClick       Callback once the section is clicked.
 * @param modifier      Modifier.
 */
@Composable
private fun ResultSection(
    title: String,
    subtitle: String,
    accountsCount: Int,
    painter: Painter,
    shape: RoundedPolygon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val highlightColor: Color = if (accountsCount == 0) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.error
    }
    val onHighlightColor: Color = if (accountsCount == 0) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onError
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable(accountsCount > 0) {
                onClick()
            }
            .padding(
                horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal),
                vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
        ) {
            Shape(
                shape = shape,
                color = highlightColor,
                modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_m))
            ) {
                Icon(
                    painter = painter,
                    contentDescription = "",
                    tint = onHighlightColor,
                    modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_xs))
                )
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
                    .weight(1f)
            ) {
                Text(
                    text = title,
                    color = highlightColor,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (accountsCount > 0) {
                Icon(
                    painter = painterResource(de.christian2003.core.ui.R.drawable.ic_next),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_xxs))
                )
            }
        }
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.padding(
                start = dimensionResource(de.christian2003.core.ui.R.dimen.image_m) + dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal)
            )
        ) {
            Text(
                text = accountsCount.toString(),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = pluralStringResource(R.plurals.analysis_overview_passwordsLabel, accountsCount),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
            )
        }
    }
}


/**
 * Top bar for the screen.
 *
 * @param restartAnalysisEnabled    Whether the button to restart the analysis is enabled.
 * @param onNavigateUp              Callback to navigate up the nav stack.
 * @param onRestartAnalysis         Callback to restart the analysis.
 */
@Composable
private fun TopBar(
    restartAnalysisEnabled: Boolean,
    onNavigateUp: () -> Unit,
    onRestartAnalysis: () -> Unit
) {
    TopAppBar(
        title = {
            Text(stringResource(R.string.analysis_title))
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
        },
        actions = {
            IconButton(
                enabled = restartAnalysisEnabled,
                onClick = onRestartAnalysis
            ) {
                Icon(
                    painter = painterResource(de.christian2003.core.ui.R.drawable.ic_reload),
                    contentDescription = ""
                )
            }
        }
    )
}
