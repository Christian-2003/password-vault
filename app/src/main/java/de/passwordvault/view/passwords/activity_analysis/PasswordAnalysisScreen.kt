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
import de.passwordvault.model.analysis.passwords.Password
import kotlinx.coroutines.launch


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
                        color = LocalPasswordVaultColors.current.text
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
                            tint = LocalPasswordVaultColors.current.text,
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
                            tint = if (viewModel.isAnalysisStarted) { LocalPasswordVaultColors.current.text.copy(0.5f) } else { LocalPasswordVaultColors.current.text },
                            contentDescription = ""
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LocalPasswordVaultColors.current.backgroundAppBar)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LocalPasswordVaultColors.current.background)
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
                    numberOfAnalyzedPasswords = viewModel.analyzedPasswords.size,
                    numberOfIdenticalPasswords = viewModel.identicalPasswords.size,
                    onAnalyzedPasswordsClicked = {

                    },
                    weakPasswords = viewModel.weakPasswords,
                    onPasswordClicked = { password ->
                        onNavigateToEntry(password.entryUuid)
                    }
                )
            }
        }
    }
}


@Composable
private fun LoadingSection() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}


@Composable
private fun ContentSection(
    securityScore: Double,
    maxSecurityScore: Int,
    thresholdGood: Float,
    thresholdNeutral: Float,
    numberOfAnalyzedPasswords: Int,
    numberOfIdenticalPasswords: Int,
    onAnalyzedPasswordsClicked: () -> Unit,
    weakPasswords: List<Password>,
    onPasswordClicked: (Password) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    TabRow(
        selectedTabIndex = pagerState.currentPage,
        containerColor = LocalPasswordVaultColors.current.backgroundAppBar,
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
                numberOfAnalyzedPasswords = numberOfAnalyzedPasswords,
                numberOfWeakPasswords = weakPasswords.size,
                numberOfIdenticalPasswords = numberOfIdenticalPasswords,
                onAnalyzedPasswordsClicked = {

                },
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
            2 -> IdenticalPasswordsTab()
        }
    }
}
