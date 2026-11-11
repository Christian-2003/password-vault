package de.christian2003.filesysteminspector.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.christian2003.filesysteminspector.domain.FileMetadata
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import java.util.Locale


@Composable
fun FileDetailsDialog(
    file: FileMetadata,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = file.name ?: "Unknown",
                maxLines = 1,
                overflow = TextOverflow.MiddleEllipsis
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                MetadataItem("Name", file.name)

                MetadataItem("Path", file.path)

                MetadataItem(
                    "Size",
                    file.size?.let { "$it bytes" } ?: "Unknown"
                )

                MetadataItem(
                    "MIME type",
                    file.mimeType ?: "Unknown"
                )

                MetadataItem("File type", file.fileType)

                MetadataItem(
                    "Extension",
                    file.extension ?: "None"
                )

                MetadataItem(
                    "Last modified",
                    file.lastModified?.let {
                        SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss",
                            Locale.getDefault()
                        ).format(Date(it))
                    } ?: "Unknown"
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                MetadataItem(
                    "Readable",
                    file.canRead.toString()
                )

                MetadataItem(
                    "Writable",
                    file.canWrite.toString()
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
private fun MetadataItem(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
