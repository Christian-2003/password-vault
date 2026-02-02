package de.christian2003.auth.ui.recoverycodes

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.christian2003.auth.viewmodels.RecoveryCodesViewModel
import de.christian2003.auth.R
import de.christian2003.auth.models.dialogs.RecoveryCodesScreenDialog
import de.christian2003.auth.models.states.RecoveryCodesScreenState
import de.christian2003.auth.viewmodels.SetupFlowSharedViewModel
import de.christian2003.ui.composables.dialog.DialogWithHeroSection
import de.christian2003.ui.composables.HelpCard
import de.christian2003.ui.composables.dialog.SimpleDialog
import de.christian2003.ui.theme.RobotoMono
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Screen displays the recovery codes to the user after they have set a new master password.
 *
 * @param viewModel         View model.
 * @param sharedViewModel   Shared view model for the setup flow.
 * @param onNavigateUp      Callback invoked to navigate up the navigation stack.
 * @param onContinue        Callback invoked to navigate to the next setup step.
 */
@Composable
internal fun RecoveryCodesScreen(
    viewModel: RecoveryCodesViewModel,
    sharedViewModel: SetupFlowSharedViewModel,
    onNavigateUp: () -> Unit,
    onContinue: () -> Unit
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    //Download launcher:
    val downloadSuccessMessage: String = stringResource(R.string.recoveryCodes_downloadSnackbar_success)
    val downloadErrorMessage: String = stringResource(R.string.recoveryCodes_downloadSnackbar_error)
    val downloadLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.data != null && result.data!!.data != null) {
            coroutineScope.launch(Dispatchers.IO) {
                val downloadResult: Boolean = viewModel.downloadRecoveryCodesToFile(result.data!!.data!!)
                withContext(Dispatchers.Main) {
                    snackbarHostState.showSnackbar(
                        message = if (downloadResult) {
                            downloadSuccessMessage
                        } else {
                            downloadErrorMessage
                        },
                        withDismissAction = true
                    )
                }
            }
        }
    }

    val invokeOnContinue: () -> Unit = {
        if (!viewModel.isError && viewModel.recoveryCodes.isNotEmpty()) {
            sharedViewModel.recoveryCodes = viewModel.recoveryCodesAsCharArray
            onContinue()
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                state = viewModel.state,
                onNavigateUp = onNavigateUp
            )
        },
        bottomBar = {
            BottomBar(
                canContinue = !viewModel.isError && viewModel.recoveryCodes.isNotEmpty(),
                onContinue = {
                    if (viewModel.areRecoveryCodesDownloaded) {
                        invokeOnContinue()
                    }
                    else {
                        viewModel.dialog = RecoveryCodesScreenDialog.ConfirmContinue
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(
                    horizontal = dimensionResource(de.christian2003.ui.R.dimen.margin_horizontal)
                )
        ) {
            when {
                viewModel.isError -> {
                    //Error:
                    ErrorContent()
                }
                viewModel.recoveryCodes.isEmpty() -> {
                    //Generating codes:
                    LoadingContent()
                }
                else -> {
                    //Display codes:
                    RecoveryCodesContent(
                        state = viewModel.state,
                        recoveryCodes = viewModel.recoveryCodes,
                        isHelpCardVisible = viewModel.isHelpCardVisible,
                        onDismissHelpCard = {
                            viewModel.dismissHelpCard()
                        },
                        onDownloadRecoveryCodes = {
                            viewModel.dialog = RecoveryCodesScreenDialog.ConfirmDownload
                        }
                    )
                }
            }
        }
    }

    //Dialogs:
    when (viewModel.dialog) {
        RecoveryCodesScreenDialog.ConfirmDownload -> {
            val downloadFilename: String = stringResource(R.string.recoveryCodes_downloadFilename)
            DialogWithHeroSection(
                title = stringResource(R.string.recoveryCodes_downloadDialog_title),
                text = stringResource(R.string.recoveryCodes_downloadDialog_text),
                dismissButtonText = stringResource(R.string.recoveryCodes_downloadDialog_buttonCancel),
                confirmButtonText = stringResource(R.string.recoveryCodes_downloadDialog_buttonConfirm),
                onDismiss = {
                    viewModel.dialog = RecoveryCodesScreenDialog.None
                },
                onConfirm = {
                    viewModel.dialog = RecoveryCodesScreenDialog.None
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.setType("text/plain")
                    intent.putExtra(Intent.EXTRA_TITLE, downloadFilename)
                    downloadLauncher.launch(intent)
                }
            ) {
                Image(
                    painter = painterResource(R.drawable.onboarding_recovery),
                    contentDescription = "",
                    modifier = Modifier.size(dimensionResource(de.christian2003.ui.R.dimen.image_xxl))
                )
            }
        }
        RecoveryCodesScreenDialog.ConfirmContinue -> {
            SimpleDialog(
                title = stringResource(R.string.recoveryCodes_confirmContinueDialog_title),
                text = stringResource(R.string.recoveryCodes_confirmContinueDialog_text),
                dismissButtonText = stringResource(R.string.recoveryCodes_confirmContinueDialog_buttonGoBack),
                confirmButtonText = stringResource(R.string.recoveryCodes_confirmContinueDialog_buttonContinue),
                onDismiss = {
                    viewModel.dialog = RecoveryCodesScreenDialog.None
                },
                onConfirm = {
                    viewModel.dialog = RecoveryCodesScreenDialog.None
                    invokeOnContinue()
                }
            )
        }
        else -> { }
    }
}


/**
 * Content displayed if an error occurred while generating the recovery codes.
 */
@Composable
private fun ErrorContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraLarge)
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(
                horizontal = dimensionResource(de.christian2003.ui.R.dimen.padding_horizontal),
                vertical = dimensionResource(de.christian2003.ui.R.dimen.padding_vertical)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(de.christian2003.ui.R.drawable.ic_error),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.padding(end = dimensionResource(de.christian2003.ui.R.dimen.padding_horizontal))
            )
            Text(
                text = stringResource(R.string.recoveryCodes_error),
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


/**
 * Content displayed while the recovery codes are generating.
 */
@Composable
private fun LoadingContent() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            LoadingIndicator()
            Text(
                text = stringResource(R.string.recoveryCodes_loadingInfo),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = dimensionResource(de.christian2003.ui.R.dimen.padding_vertical))
            )
        }
    }
}


/**
 * Content displayed once recovery codes are generated.
 *
 * @param state                     State with which the screen is displayed.
 * @param recoveryCodes             List of recovery codes to display.
 * @param isHelpCardVisible         Whether the help card is visible.
 * @param onDismissHelpCard         Callback invoked to dismiss the help card.
 * @param onDownloadRecoveryCodes   Callback invoked to download the recovery codes.
 */
@Composable
private fun RecoveryCodesContent(
    state: RecoveryCodesScreenState,
    recoveryCodes: List<String>,
    isHelpCardVisible: Boolean,
    onDismissHelpCard: () -> Unit,
    onDownloadRecoveryCodes: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        AnimatedVisibility(isHelpCardVisible) {
            HelpCard(
                text = when (state) {
                    RecoveryCodesScreenState.FirstTimeSetup -> stringResource(R.string.recoveryCodes_help_firstTimeSetup)
                    else -> stringResource(R.string.recoveryCodes_help_newCodes)
                },
                onDismiss = onDismissHelpCard,
                modifier = Modifier.padding(bottom = dimensionResource(de.christian2003.ui.R.dimen.padding_vertical))
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.extraLarge)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(
                    horizontal = dimensionResource(de.christian2003.ui.R.dimen.padding_horizontal),
                    vertical = dimensionResource(de.christian2003.ui.R.dimen.padding_vertical)
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_recovery),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(end = dimensionResource(de.christian2003.ui.R.dimen.padding_horizontal))
                )
                Text(
                    text = stringResource(R.string.recoveryCodes_codesHeader),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            recoveryCodes.forEach { recoveryCode ->
                RecoveryCodesItem(
                    recoveryCode = recoveryCode
                )
            }
        }
        TextButton(
            onClick = onDownloadRecoveryCodes
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(de.christian2003.ui.R.drawable.ic_download),
                    contentDescription = "",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(stringResource(R.string.recoveryCodes_button_download))
            }
        }
    }
}


/**
 * Displays a single recovery code.
 *
 * @param recoveryCode  Formatted recovery code to display.
 */
@Composable
private fun RecoveryCodesItem(
    recoveryCode: String
) {
    Text(
        text = recoveryCode,
        style = MaterialTheme.typography.bodyMedium.copy(
            fontFamily = RobotoMono
        ),
        modifier = Modifier
            .padding(top = dimensionResource(de.christian2003.ui.R.dimen.padding_vertical))
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(
                horizontal = 8.dp,
                vertical = 4.dp
            )
    )
}


/**
 * Top bar for the screen.
 *
 * @param state         State with which the screen is opened.
 * @param onNavigateUp  Callback invoked to navigate up the navigation stack.
 */
@Composable
private fun TopBar(
    state: RecoveryCodesScreenState,
    onNavigateUp: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = when (state) {
                    RecoveryCodesScreenState.FirstTimeSetup -> stringResource(R.string.recoveryCodes_title_firstTimeSetup)
                    else -> stringResource(R.string.recoveryCodes_title_newCodes)
                }
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigateUp
            ) {
                Icon(
                    painter = painterResource(de.christian2003.ui.R.drawable.ic_back),
                    contentDescription = ""
                )
            }
        }
    )
}


/**
 * Bottom bar for the screen.
 *
 * @param canContinue   Whether the user can continue to the next step.
 * @param onContinue    Callback invoked once the user continues to the next step.
 */
@Composable
private fun BottomBar(
    canContinue: Boolean,
    onContinue: () -> Unit
) {
    BottomAppBar {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                enabled = canContinue,
                onClick = {
                    if (canContinue) {
                        onContinue()
                    }
                },
                modifier = Modifier.padding(
                    //Horizontal padding of bottom app bar: 4 dp
                    horizontal = dimensionResource(de.christian2003.ui.R.dimen.margin_horizontal) - 4.dp
                )
            ) {
                Text(stringResource(R.string.password_button_continue))
            }
        }
    }
}
