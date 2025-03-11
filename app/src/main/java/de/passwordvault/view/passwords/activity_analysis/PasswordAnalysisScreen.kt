package de.passwordvault.view.passwords.activity_analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import de.passwordvault.ui.composables.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import de.passwordvault.ui.theme.LocalPasswordVaultColors
import de.passwordvault.R
import de.passwordvault.model.analysis.passwords.AnalyzedPassword
import de.passwordvault.model.entry.EntryAbbreviated
import kotlinx.coroutines.launch


/**
 * Composable displays the screen for the password security analysis.
 *
 * @param viewModel         View model for the screen.
 * @param onNavigateUp      Callback invoked to navigate up on the navigation stack.
 * @param onNavigateToEntry Callback invoked to navigate to the entry of the specified UUID.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordAnalysisScreen(
    viewModel: PasswordAnalysisViewModel,
    onNavigateUp: () -> Unit,
    onNavigateToEntry: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.password_analysis_title),
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
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.analyze()
                        },
                        enabled = !viewModel.isAnalysisStarted
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_reload),
                            tint = if (viewModel.isAnalysisStarted) { MaterialTheme.colorScheme.onSurface.copy(0.5f) } else { MaterialTheme.colorScheme.onSurface },
                            contentDescription = ""
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
        ) {
            if (viewModel.isAnalysisStarted) {
                LoadingSection()
            }
            else {
                ContentSection(
                    securityScore = viewModel.securityScore,
                    maxSecurityScore = viewModel.maxSecurityScore!!,
                    thresholdGood = viewModel.thresholdGood!!,
                    thresholdNeutral = viewModel.thresholdNeutral!!,
                    numberOfIdenticalPasswords = viewModel.identicalPasswords.size,
                    weakPasswords = viewModel.weakPasswords,
                    identicalPasswords = viewModel.identicalPasswords,
                    onPasswordClicked = { password ->
                        onNavigateToEntry(password.entry.uuid)
                    },
                    onEntryClicked = { entry ->
                        onNavigateToEntry(entry.uuid)
                    }
                )
            }
        }
    }
}


/**
 * Composable displays the loading section while passwords are being analyzed.
 */
@Composable
private fun LoadingSection() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}


/**
 * Composable displays the content section after passwords have been loaded.
 *
 * @param securityScore                 Average security score.
 * @param maxSecurityScore              Max possible security score.
 * @param thresholdGood                 Threshold with which a password is considered 'good'.
 * @param thresholdNeutral              Threshold with which a password is considered 'neutral'.
 * @param numberOfIdenticalPasswords    Number of identical passwords.
 * @param weakPasswords                 List of weak passwords.
 * @param identicalPasswords            List of identical passwords.
 * @param onPasswordClicked             Callback invoked once a password is clicked.
 * @param onEntryClicked                Callback invoked once an entry is clicked.
 */
@Composable
private fun ContentSection(
    securityScore: Double,
    maxSecurityScore: Int,
    thresholdGood: Float,
    thresholdNeutral: Float,
    numberOfIdenticalPasswords: Int,
    weakPasswords: List<AnalyzedPassword>,
    identicalPasswords: List<List<AnalyzedPassword>>,
    onPasswordClicked: (AnalyzedPassword) -> Unit,
    onEntryClicked: (EntryAbbreviated) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    TabRow(
        selectedTabIndex = pagerState.currentPage,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        divider = {},
        indicator = { tabPositions ->
            TabRowDefaults.PrimaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage])
            )
        },
        tabs = {
            Tab(
                title = stringResource(R.string.password_analysis_menu_general),
                index = 0,
                selectedIndex = pagerState.currentPage,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(it)
                    }
                }
            )
            Tab(
                title = stringResource(R.string.password_analysis_menu_weak),
                index = 1,
                selectedIndex = pagerState.currentPage,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(it)
                    }
                }
            )
            Tab(
                title = stringResource(R.string.password_analysis_menu_duplicates),
                index = 2,
                selectedIndex = pagerState.currentPage,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(it)
                    }
                }
            )
        }
    )
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        when (page) {
            0 -> GeneralTab(
                securityScore = securityScore,
                maxSecurityScore = maxSecurityScore,
                thresholdGood = thresholdGood,
                thresholdNeutral = thresholdNeutral,
                numberOfWeakPasswords = weakPasswords.size,
                numberOfIdenticalPasswords = numberOfIdenticalPasswords,
                onWeakPasswordsClicked = {
                    scope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                },
                onIdenticalPasswordsClicked = {
                    scope.launch {
                        pagerState.animateScrollToPage(2)
                    }
                }
            )
            1 -> WeakPasswordsTab(
                weakPasswords = weakPasswords,
                onWeakPasswordClicked = onPasswordClicked,
                thresholdGood = thresholdGood,
                thresholdNeutral = thresholdNeutral,
                maxSecurityScore = maxSecurityScore
            )
            2 -> IdenticalPasswordsTab(
                identicalPasswords = identicalPasswords,
                onEntryClicked = onEntryClicked
            )
        }
    }
}
