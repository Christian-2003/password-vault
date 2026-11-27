package de.christian2003.passwordvault.presentation.ui.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.christian2003.core.common.domain.abstraction.LandingPluginContext
import de.christian2003.passwordvault.presentation.viewmodels.HomeViewModel


@Composable
internal fun HomeView(
    viewModel: HomeViewModel,
    innerPadding: PaddingValues,
    pluginContext: LandingPluginContext
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        items(viewModel.plugins) { item ->
            item.Content(pluginContext)
        }
    }
}
