package de.christian2003.passwordvault.presentation.ui.legal

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.christian2003.core.ui.composables.Headline
import de.christian2003.core.ui.composables.HeadlineIndentation
import de.christian2003.core.ui.composables.settings.SettingsItemButton
import de.christian2003.passwordvault.R
import de.christian2003.passwordvault.domain.entities.LegalPage
import de.christian2003.passwordvault.presentation.viewmodels.LegalViewModel
import java.time.LocalDate


/**
 * Screen through which users are informed about changes to the legal pages (i.e. Privacy policy or
 * terms of changes).
 * Users must accept the changes on this change before they can continue using the app.
 *
 * @param viewModel     View model.
 * @param onContinue    Callback to continue after accepting.
 * @param onCancel      Callback to cancel after declining.
 */
@Composable
internal fun LegalScreen(
    viewModel: LegalViewModel,
    onContinue: () -> Unit,
    onCancel: () -> Unit
) {
    val context: Context = LocalContext.current

    val invokeOnContinue: () -> Unit = {
        viewModel.acceptLegalPages()
        onContinue()
    }

    BackHandler {
        onCancel()
    }

    Scaffold(
        topBar = {
            TopBar()
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
        ) {
            //Content:
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(R.drawable.legal),
                        contentDescription = "",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .padding(
                                horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                                vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                            )
                            .width(dimensionResource(de.christian2003.core.ui.R.dimen.image_xxxl))
                    )
                }
                Text(
                    text = stringResource(R.string.legal_text),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal)
                        )
                )
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(de.christian2003.core.ui.R.drawable.ic_error),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .padding(bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
                            .size(dimensionResource(de.christian2003.core.ui.R.dimen.image_l))
                    )
                }
                Text(
                    text = stringResource(R.string.legal_info),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal)
                        )
                )

                val validDate: LocalDate? = viewModel.validDate
                if (validDate != null) {
                    Text(
                        text = stringResource(R.string.legal_valid, viewModel.formatDate(validDate)),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal)
                            )
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
                )
                Headline(
                    title = stringResource(R.string.legal_changes),
                    indentation = HeadlineIndentation.PrefixIconLevel
                )
                val privacyPolicyPage: LegalPage? = viewModel.privacyPolicyPage
                val termsOfServicesPage: LegalPage? = viewModel.termsOfServicePage

                if (privacyPolicyPage != null) {
                    SettingsItemButton(
                        title = stringResource(R.string.legal_privacyPolicy_title),
                        info = stringResource(R.string.legal_privacyPolicy_info),
                        prefixIcon = painterResource(R.drawable.ic_privacy),
                        endIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_external),
                        isFirst = true,
                        isLast = termsOfServicesPage == null,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, privacyPolicyPage.pageUrl)
                            context.startActivity(intent)
                        }
                    )
                }
                if (termsOfServicesPage != null) {
                    SettingsItemButton(
                        title = stringResource(R.string.legal_termsOfService_title),
                        info = stringResource(R.string.legal_termsOfService_info),
                        prefixIcon = painterResource(R.drawable.ic_tos),
                        endIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_external),
                        isFirst = privacyPolicyPage == null,
                        isLast = true,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, termsOfServicesPage.pageUrl)
                            context.startActivity(intent)
                        }
                    )
                }
            }

            //Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                        vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                    )
            ) {
                Button(
                    onClick = invokeOnContinue,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(de.christian2003.core.ui.R.drawable.ic_check),
                            contentDescription = "",
                            modifier = Modifier.padding(end = 8.dp) //8dp spacing according to MD3 guide
                        )
                        Text(stringResource(R.string.legal_buttonAccept))
                    }
                }
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(de.christian2003.core.ui.R.drawable.ic_cancel),
                            contentDescription = "",
                            modifier = Modifier.padding(end = 8.dp) //8dp spacing according to MD3 guide
                        )
                        Text(stringResource(R.string.legal_buttonDecline))
                    }
                }
            }
        }
    }
}


/**
 * Top bar for the screen.
 */
@Composable
private fun TopBar() {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.legal_title),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    )
}
