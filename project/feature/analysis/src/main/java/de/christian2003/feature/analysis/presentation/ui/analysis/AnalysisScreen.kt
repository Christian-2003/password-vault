package de.christian2003.feature.analysis.presentation.ui.analysis

import android.graphics.drawable.Drawable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import de.christian2003.core.ui.composables.ListItemContainer
import de.christian2003.core.ui.composables.Shape
import de.christian2003.data.accounts.domain.entities.AccountDescriptor
import de.christian2003.feature.analysis.presentation.viewmodels.AnalysisViewModel
import kotlin.uuid.Uuid
import de.christian2003.feature.analysis.R
import de.christian2003.feature.analysis.domain.entities.PasswordResult
import de.christian2003.feature.analysis.domain.entities.SecurityCriteria
import de.christian2003.feature.analysis.domain.entities.SecurityResult


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
                    .padding(innerPadding)
            ) {
                itemsIndexed(securityResult.passwordResults) { index, passwordResult ->
                    PasswordResultListRow(
                        passwordResult = passwordResult,
                        isFirst = index == 0,
                        isLast = index == securityResult.passwordResults.size - 1,
                        onQueryAccountDescriptor = { accountId ->
                            viewModel.queryAccountDescriptor(accountId)
                        },
                        onQueryAccountIcon = { accountDescriptor ->
                            viewModel.queryAccountIcon(accountDescriptor)
                        },
                        onNavigateToAccount = onNavigateToAccount
                    )
                }
            }
        }
    }
}


@Composable
private fun PasswordResultListRow(
    passwordResult: PasswordResult,
    isFirst: Boolean,
    isLast: Boolean,
    onQueryAccountDescriptor: suspend (Uuid) -> AccountDescriptor?,
    onQueryAccountIcon: (AccountDescriptor) -> Drawable?,
    onNavigateToAccount: (Uuid) -> Unit
) {
    val accountDescriptor: AccountDescriptor? by produceState(null) {
        value = onQueryAccountDescriptor(passwordResult.accountId)
    }
    var expanded: Boolean by remember { mutableStateOf(false) }

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
                        expanded = !expanded
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
                    Text(
                        text = accountDescriptor?.description ?: "",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                //Expanded:
                val animatedArrowRotation by animateFloatAsState(
                    targetValue = if (expanded) { 180F } else { 0F },
                    animationSpec = spring()
                )
                Icon(
                    painter = painterResource(de.christian2003.core.ui.R.drawable.ic_expand),
                    contentDescription = "",
                    modifier = Modifier.rotate(animatedArrowRotation)
                )
            }

            AnimatedVisibility(expanded) {
                PasswordResultWeaknesses(
                    weaknesses = passwordResult.weaknesses,
                    onFixIssuesClick = {
                        onNavigateToAccount(passwordResult.accountId)
                    }
                )
            }
        }
    }
}


@Composable
private fun PasswordResultWeaknesses(
    weaknesses: List<SecurityCriteria>,
    onFixIssuesClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (weaknesses.isEmpty()) {
            PasswordResultWeaknessesSecurityCriteria(null)
        }
        else {
            weaknesses.forEach { weakness ->
                PasswordResultWeaknessesSecurityCriteria(
                    securityCriteria = weakness
                )
            }
            TextButton(
                onClick = onFixIssuesClick
            ) {
                Text(stringResource(R.string.analysis_weakness_fixIssues))
            }
        }
    }
}


@Composable
private fun PasswordResultWeaknessesSecurityCriteria(
    securityCriteria: SecurityCriteria?
) {
    val tintColor: Color = if (securityCriteria == null) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.error
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal) * 2 + dimensionResource(de.christian2003.core.ui.R.dimen.image_m),
                end = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal),
                bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
            )
    ) {
        Icon(
            painter = when (securityCriteria) {
                SecurityCriteria.Length -> painterResource(R.drawable.ic_length)
                SecurityCriteria.CharacterVariety -> painterResource(de.christian2003.core.ui.R.drawable.ic_text)
                SecurityCriteria.Entropy -> painterResource(R.drawable.ic_entropy)
                SecurityCriteria.PatternsAndSubstitutions -> painterResource(R.drawable.ic_patterns)
                SecurityCriteria.DictionaryWords -> painterResource(R.drawable.ic_dictionary)
                SecurityCriteria.CommonPassword -> painterResource(de.christian2003.core.ui.R.drawable.ic_copy)
                SecurityCriteria.Reuse -> painterResource(R.drawable.ic_reuse)
                null -> painterResource(R.drawable.ic_happy)
            },
            contentDescription = "",
            tint = tintColor,
            modifier = Modifier
                .padding(
                    end = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal) / 2
                )
                .size(dimensionResource(de.christian2003.core.ui.R.dimen.image_xs))
        )
        Text(
            text = when (securityCriteria) {
                SecurityCriteria.Length -> stringResource(R.string.analysis_weakness_length)
                SecurityCriteria.CharacterVariety -> stringResource(R.string.analysis_weakness_characterVariety)
                SecurityCriteria.Entropy -> stringResource(R.string.analysis_weakness_entropy)
                SecurityCriteria.PatternsAndSubstitutions -> stringResource(R.string.analysis_weakness_patterns)
                SecurityCriteria.DictionaryWords -> stringResource(R.string.analysis_weakness_dictionaryWords)
                SecurityCriteria.CommonPassword -> stringResource(R.string.analysis_weakness_commonPasswords)
                SecurityCriteria.Reuse -> stringResource(R.string.analysis_weakness_reuse)
                null -> stringResource(R.string.analysis_weakness_none)
            },
            style = MaterialTheme.typography.labelMedium,
            color = tintColor
        )
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
