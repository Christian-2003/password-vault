package de.christian2003.passwordvault.presentation.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.christian2003.core.ui.composables.NavigationBarProtection
import de.christian2003.core.ui.composables.StaticSearchBar
import de.christian2003.passwordvault.R
import de.christian2003.passwordvault.presentation.viewmodels.MainViewModel


@Composable
internal fun MainScreen(
    viewModel: MainViewModel,
    onNavigateTo: (index: Int) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onCreateNewAccount: () -> Unit,
    content: @Composable (innerPadding: PaddingValues) -> Unit
) {
    val appBarState: TopAppBarState = rememberTopAppBarState()
    val appBarScrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(appBarState)

    //Navigation bar scrolling:
    var isNavigationBarVisible: Boolean by remember { mutableStateOf(true) }
    val navigationBarScrollBehavior = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (consumed.y < 0) {
                    //Scrolling down:
                    isNavigationBarVisible = false
                }
                else if (consumed.y > 0) {
                    //Scrolling up:
                    isNavigationBarVisible = true
                }
                return Offset.Zero
            }
        }
    }

    val navigationBarVisibilityState = remember { MutableTransitionState(isNavigationBarVisible) }
    navigationBarVisibilityState.targetState = isNavigationBarVisible
    var fabChanged: Boolean by remember { mutableStateOf(false) }

    LaunchedEffect(navigationBarVisibilityState.isIdle, navigationBarVisibilityState.currentState) {
        if (navigationBarVisibilityState.isIdle) {
            fabChanged = !isNavigationBarVisible
        }
    }

    //Content:
    Scaffold(
        topBar = {
            TopBar(
                scrollBehavior = appBarScrollBehavior,
                onNavigateToSearch = onNavigateToSearch,
                onNavigateToSettings = onNavigateToSettings
            )
        },
        modifier = Modifier
            .nestedScroll(appBarScrollBehavior.nestedScrollConnection)
            .nestedScroll(navigationBarScrollBehavior)
    ) { innerPadding ->
        val bottomPadding: Dp = innerPadding.calculateBottomPadding()

        content(innerPadding)

        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                FloatingActionButton(
                    onClick = onCreateNewAccount,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(
                            end = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal) * 2,
                            bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical) * 2
                                + if (fabChanged) { bottomPadding } else { 0.dp }
                        )
                ) {
                    Icon(
                        painter = painterResource(de.christian2003.core.ui.R.drawable.ic_add),
                        contentDescription = ""
                    )
                }

                AnimatedVisibility(
                    visibleState = navigationBarVisibilityState,
                    enter = expandVertically { it },
                    exit = shrinkVertically { it }
                ) {
                    NavigationBar {
                        viewModel.navigationDestinations.forEachIndexed { index, item ->
                            NavigationBarItem(
                                selected = index == viewModel.selectedDestination,
                                onClick = {
                                    onNavigateTo(index)
                                },
                                icon = {
                                    Icon(
                                        painter = if (viewModel.selectedDestination == index) {
                                            painterResource(item.selectedIcon)
                                        } else {
                                            painterResource(item.icon)
                                        },
                                        contentDescription = stringResource(item.label)
                                    )
                                },
                                label = {
                                    Text(
                                        text = stringResource(item.label)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        NavigationBarProtection(bottomPadding)
    }
}


@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onNavigateToSearch: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    TopAppBar(
        title = {
            StaticSearchBar(
                hint = stringResource(R.string.main_search_hint),
                onClick = onNavigateToSearch,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
            )
        },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            //Placeholder so that the search bar is centered
            Box(modifier = Modifier.size(IconButtonDefaults.smallContainerSize()))
        },
        actions = {
            IconButton(
                onClick = onNavigateToSettings
            ) {
                Icon(
                    painter = painterResource(de.christian2003.core.ui.R.drawable.ic_settings),
                    contentDescription = ""
                )
            }
        }
    )
}
