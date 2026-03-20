package de.christian2003.feature.files.ui.directory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import de.christian2003.core.ui.composables.DragHandle
import de.christian2003.core.ui.composables.Shape
import de.christian2003.core.ui.theme.isDarkTheme
import de.christian2003.data.files.domain.entities.InternalDirectory
import de.christian2003.data.files.domain.entities.InternalFile
import de.christian2003.feature.files.models.other.FileType
import java.time.LocalDateTime
import de.christian2003.feature.files.R


@Composable
internal fun FileDetailSheet(
    file: InternalFile,
    directory: InternalDirectory,
    isShared: Boolean,
    bottomPadding: Dp,
    onQueryFileType: (String) -> FileType,
    onFormatStorageUnit: (Long) -> String,
    onFormatTime: (LocalDateTime) -> String,
    onGeneratePositiveColor: (Color, Boolean) -> Color,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal)
                )
        ) {
            DragHandle(modifier = Modifier.align(Alignment.CenterHorizontally))

            Text(
                text = stringResource(R.string.fileDetails_title),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
            )

            FileNameHeadline(
                fileName = file.actualFileName,
                fileType = onQueryFileType(file.metadata.mimeType),
                modifier = Modifier.padding(vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
            )

            FileInfo(
                label = stringResource(R.string.fileDetails_size),
                content = onFormatStorageUnit(file.metadata.size),
                modifier = Modifier.padding(vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
            )

            FileInfo(
                label = stringResource(R.string.fileDetails_path),
                content = "${directory.internalPath}/${file.actualFileName}",
                modifier = Modifier.padding(vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
            )

            FileInfo(
                label = stringResource(R.string.fileDetails_accessedAt),
                content = onFormatTime(file.metadata.accessedAt),
                modifier = Modifier.padding(vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
            )

            FileInfo(
                label = stringResource(R.string.fileDetails_editedAt),
                content = onFormatTime(file.metadata.editedAt),
                modifier = Modifier.padding(vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
            )

            FileInfo(
                label = stringResource(R.string.fileDetails_createdAt),
                content = onFormatTime(file.metadata.createdAt),
                modifier = Modifier.padding(vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
            )

            SharedFileInfo(
                isShared = isShared,
                onGeneratePositiveColor = onGeneratePositiveColor,
                modifier = Modifier.padding(vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
            )

            Box(modifier = Modifier.height(bottomPadding))
        }
    }
}


@Composable
private fun FileNameHeadline(
    fileName: String,
    fileType: FileType,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Shape(
            shape = MaterialShapes.Cookie4Sided,
            color = fileType.getSurfaceColor(),
            modifier = Modifier
                .align(Alignment.Top)
                .size(dimensionResource(de.christian2003.core.ui.R.dimen.image_xl))
        ) {
            Icon(
                painter = painterResource(fileType.drawableRes),
                contentDescription = "",
                tint = fileType.getOnSurfaceColor(),
                modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_m))
            )
        }
        Text(
            text = fileName,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
        )
    }
}


@Composable
private fun FileInfo(
    label: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = content,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


@Composable
private fun SharedFileInfo(
    isShared: Boolean,
    onGeneratePositiveColor: (Color, Boolean) -> Color,
    modifier: Modifier = Modifier
) {
    val color: Color = if (isShared) {
        MaterialTheme.colorScheme.error
    } else {
        onGeneratePositiveColor(MaterialTheme.colorScheme.error, MaterialTheme.isDarkTheme())
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                painter = if (isShared) {
                    painterResource(de.christian2003.core.ui.R.drawable.ic_error)
                } else {
                    painterResource(de.christian2003.core.ui.R.drawable.ic_check_filled)
                },
                tint = color,
                contentDescription = "",
                modifier = Modifier.padding(end = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
            )
            Text(
                text = if (isShared) {
                    stringResource(R.string.fileDetails_isShared_title)
                } else {
                    stringResource(R.string.fileDetails_isNotShared_title)
                },
                color = color,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        if (isShared) {
            Text(
                text = stringResource(R.string.fileDetails_isShared_text),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}
