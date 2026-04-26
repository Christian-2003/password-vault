package de.christian2003.feature.analysis.presentation.ui.analysis

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
                onNavigateUp = onNavigateUp
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
                //Weak accounts:
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

                //Reused accounts:
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

                //Navigation bar spacer:
                item {
                    Box(modifier = Modifier.height(bottomPadding))
                }
            }
        }

        NavigationBarProtection(bottomPadding)
    }

    when (viewModel.dialog) {
        AnalysisScreenDialog.WeakPasswords -> {
            PasswordResultsSheet(
                passwordResults = securityResult!!.passwordResults,
                filter = setOf(PasswordStrength.Weak, PasswordStrength.Medium),
                title = stringResource(R.string.analysis_overview_weak_title),
                isHelpCardVisible = viewModel.isWeakPasswordsHelpCardVisible,
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
                    viewModel.dismissWeakPasswordsDialog()
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


@Composable
fun ResultSection(
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
                text = pluralStringResource(R.plurals.analysis_overview_accountsLabel, accountsCount),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
            )
        }
    }
}


@Composable
private fun TopBar(
    onNavigateUp: () -> Unit
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
        }
    )
}
