package de.passwordvault.view.settings.activity_quality_gates

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import de.passwordvault.R
import de.passwordvault.ui.composables.Card
import de.passwordvault.ui.composables.Headline
import de.passwordvault.ui.theme.LocalPasswordVaultColors


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun QualityGatesScreen(
    viewModel: QualityGatesViewModel,
    onNavigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.quality_gates),
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LocalPasswordVaultColors.current.backgroundAppBar)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding)
        ) {
            item {
                Card(
                    text = stringResource(R.string.quality_gates_info)
                )
            }
            stickyHeader {
                Headline(
                    title = stringResource(R.string.quality_gates_title),
                    endIcon = painterResource(R.drawable.ic_add),
                    onClick = {
                        //TODO
                    },
                    modifier = Modifier.background(LocalPasswordVaultColors.current.background)
                )
            }
        }
    }
}
