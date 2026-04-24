package de.christian2003.signinginfoinspector.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.security.cert.X509Certificate
import java.util.Base64


@Composable
fun CertInfoDialog(
    x509: X509Certificate,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Certificate Info")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                CertInfoItem(
                    title = "Subject",
                    content = x509.subjectX500Principal.name
                )
                CertInfoItem(
                    title = "Issuer",
                    content = x509.issuerX500Principal.name
                )
                CertInfoItem(
                    title = "Serial Number",
                    content = x509.serialNumber.toString()
                )
                CertInfoItem(
                    title = "Not Before",
                    content = x509.notBefore.toString()
                )
                CertInfoItem(
                    title = "Not After",
                    content = x509.notAfter.toString()
                )
                CertInfoItem(
                    title = "Sig Algorithm",
                    content = x509.sigAlgName
                )
                CertInfoItem(
                    title = "PK Algorithm",
                    content = x509.publicKey.algorithm
                )
                CertInfoItem(
                    title = "Public Key",
                    content = Base64.getEncoder().encodeToString(x509.publicKey.encoded)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Ok")
            }
        }
    )
}

@Composable
private fun CertInfoItem(
    title: String,
    content: String
) {
    Column(
        modifier = Modifier.padding(
            vertical = 8.dp
        )
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
