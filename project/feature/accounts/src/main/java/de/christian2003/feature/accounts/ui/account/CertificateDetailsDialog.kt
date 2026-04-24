package de.christian2003.feature.accounts.ui.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import de.christian2003.feature.accounts.R
import de.christian2003.core.ui.composables.Headline
import de.christian2003.core.ui.composables.HeadlineIndentation
import de.christian2003.core.ui.theme.isDarkTheme
import java.security.cert.X509Certificate
import java.time.LocalDate
import java.time.ZoneId
import kotlin.collections.contains


/**
 * Dialog displays details for the provided X.509 certificate that was used to sign an Android
 * target.
 *
 * @param certificate               X.509 certificate whose details to display.
 * @param onFormatDate              Callback invoked to format a date.
 * @param onGeneratePositiveColor   Callback invoked to generate a positive color.
 * @param onDismiss                 Callback invoked to dismiss the dialog.
 */
@Composable
internal fun CertificateDetailsDialog(
    certificate: X509Certificate,
    onFormatDate: (LocalDate) -> String,
    onGeneratePositiveColor: (Color, Boolean) -> Color,
    onDismiss: () -> Unit
) {
    val subjectDn: Map<String, List<String>> = remember { parseDn(certificate.subjectX500Principal.name) }
    val issuerDn: Map<String, List<String>> = remember { parseDn(certificate.issuerX500Principal.name) }
    val notBefore: LocalDate = remember { certificate.notBefore.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() }
    val notAfter: LocalDate = remember { certificate.notAfter.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() }
    val positiveColor: Color = onGeneratePositiveColor(MaterialTheme.colorScheme.error, MaterialTheme.isDarkTheme())

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.certDetails_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(
                        start = 24.dp,
                        top = 24.dp,
                        end = 24.dp,
                        bottom = 16.dp
                    )
                )

                HorizontalDivider()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                ) {
                    //Subject
                    DNSection(
                        sectionTitle = stringResource(R.string.certDetails_subject),
                        dn = subjectDn
                    )

                    //Issuer
                    DNSection(
                        sectionTitle = stringResource(R.string.certDetails_issuer),
                        dn = issuerDn
                    )

                    //Validity
                    Headline(
                        title = stringResource(R.string.certDetails_validity),
                        indentation = HeadlineIndentation.NoneNoPadding
                    )
                    SectionContent(
                        label = stringResource(R.string.certDetails_validity_notBefore),
                        content = onFormatDate(notBefore)
                    )
                    SectionContent(
                        label = stringResource(R.string.certDetails_validity_notAfter),
                        content = onFormatDate(notAfter)
                    )

                    //Fingerprint matching
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
                    ) {
                        Icon(
                            painter = painterResource(de.christian2003.core.ui.R.drawable.ic_check_filled),
                            contentDescription = "",
                            tint = positiveColor,
                            modifier = Modifier.padding(end = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
                        )
                        Text(
                            text = stringResource(R.string.certDetails_fingerprintMatch),
                            color = positiveColor,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Text(
                        text = stringResource(R.string.certDetails_fingerprintMatchText),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
                    )
                }

                HorizontalDivider()

                //Dismiss button
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .padding(
                            start = 24.dp,
                            top = 16.dp,
                            end = 24.dp,
                            bottom = 24.dp
                        )
                        .align(Alignment.End)
                ) {
                    Text(stringResource(de.christian2003.core.ui.R.string.button_ok))
                }
            }
        }
    }
}


@Composable
private fun DNSection(
    sectionTitle: String,
    dn: Map<String, List<String>>
) {
    if(dn.contains("CN") || dn.contains("O") || dn.contains("C")) {
        Headline(
            title = sectionTitle,
            indentation = HeadlineIndentation.NoneNoPadding
        )

        if (dn.contains("CN")) {
            SectionContent(
                label = stringResource(R.string.certDetails_dn_cn),
                content = dn["CN"]!!.firstOrNull() ?: ""
            )
        }
        if (dn.contains("O")) {
            SectionContent(
                label = stringResource(R.string.certDetails_dn_o),
                content = dn["O"]!!.firstOrNull() ?: ""
            )
        }
        if (dn.contains("C")) {
            SectionContent(
                label = stringResource(R.string.certDetails_dn_c),
                content = dn["C"]!!.firstOrNull() ?: ""
            )
        }
    }
}


@Composable
private fun SectionContent(
    label: String,
    content: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = content,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


/**
 * Parses the DN from an X.509 certificate and returns it's parts in a map where the key is the
 * identifier (e.g. "CN" or "O").
 *
 * @param dn    DN to parse.
 * @return      Parsed DN.
 */
private fun parseDn(dn: String): Map<String, List<String>> {
    val result = mutableMapOf<String, MutableList<String>>()

    var i = 0
    while (i < dn.length) {
        val eq = dn.indexOf('=', i)
        if (eq == -1) break

        val key = dn.substring(i, eq)
        i = eq + 1

        val value = StringBuilder()
        var escaped = false

        while (i < dn.length) {
            val c = dn[i]
            if (!escaped && c == ',') break
            if (!escaped && c == '\\') {
                escaped = true
            } else {
                value.append(c)
                escaped = false
            }
            i++
        }

        result.getOrPut(key) { mutableListOf() }.add(value.toString())
        i++ //Skip comma
    }

    return result
}
