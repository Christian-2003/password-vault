package de.christian2003.passwordvault.plugin.presentation.view.test

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import de.christian2003.passwordvault.R
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun TestScreen(
    onNavigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Test")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            var data by remember { mutableStateOf(List(10) { "Item $it" } ) }
            val lazyListState = rememberLazyListState()
            val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
                val fromIndex = from.index - 1
                val toIndex = to.index - 1
                data = data.toMutableList().apply {
                    add(toIndex, removeAt(fromIndex))
                }
            }

            LazyColumn(
                state = lazyListState
            ) {
                item {
                    Text("Hello, World", color = MaterialTheme.colorScheme.primary)
                }
                items(data, key = { it }) { item ->
                    ReorderableItem(reorderableLazyListState, key = item) { isDragging ->
                        val elevation = animateDpAsState(if (isDragging) 4.dp else 0.dp)
                        Surface(shadowElevation = elevation.value) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.draggableHandle().fillMaxWidth()
                            ) {
                                Text(item, modifier = Modifier.weight(1f))
                                Icon(
                                    painter = painterResource(R.drawable.ic_draghandle),
                                    contentDescription = ""
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
