package de.christian2003.feature.files.ui.directory

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.christian2003.core.ui.composables.ListItemContainer
import de.christian2003.core.ui.composables.Shape
import de.christian2003.core.ui.composables.Tooltip
import de.christian2003.data.files.domain.entities.InternalFile
import de.christian2003.feature.files.R
import de.christian2003.feature.files.models.other.FileType
import de.christian2003.feature.files.models.states.DirectoryScreenState
import java.time.LocalDateTime


@Composable
internal fun FileListItem(
    state: DirectoryScreenState,
    internalFile: InternalFile,
    isFirst: Boolean,
    isLast: Boolean,
    isSelected: Boolean,
    onFormatStorageSize: (Long) -> String,
    onFormatDateTime: (LocalDateTime) -> String,
    onQueryFileType: (String) -> FileType,
    onDelete: (InternalFile) -> Unit,
    onRename: (InternalFile) -> Unit,
    onOpenWith: (InternalFile) -> Unit,
    onMoreInfo: (InternalFile) -> Unit,
    onStartMultiselect: (InternalFile) -> Unit,
    onToggleSelected: (InternalFile) -> Unit
) {
    var isDropdownExpanded: Boolean by remember { mutableStateOf(false) }
    val fileType: FileType = onQueryFileType(internalFile.metadata.mimeType)

    ListItemContainer(
        isFirst = isFirst,
        isLast = isLast,
        isSelected = isSelected
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        when (state) {
                            DirectoryScreenState.Default -> onOpenWith(internalFile)
                            DirectoryScreenState.Multiselect -> onToggleSelected(internalFile)
                        }
                    },
                    onLongClick = {
                        when (state) {
                            DirectoryScreenState.Default -> onStartMultiselect(internalFile)
                            DirectoryScreenState.Multiselect -> onToggleSelected(internalFile)
                        }
                    }
                )
                .padding(
                    start = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal),
                    top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical),
                    end = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal) - 12.dp,
                    bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                )
        ) {
            Shape(
                shape = MaterialShapes.Cookie4Sided,
                color = if (!isSelected) {
                    fileType.getSurfaceColor()
                } else {
                    MaterialTheme.colorScheme.primary
                },
                modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_m))
            ) {
                Icon(
                    painter = if (!isSelected) {
                        painterResource(fileType.drawableRes)
                    } else {
                        painterResource(de.christian2003.core.ui.R.drawable.ic_check)
                    },
                    contentDescription = "",
                    tint = if (!isSelected) {
                        fileType.getOnSurfaceColor()
                    } else {
                        MaterialTheme.colorScheme.onPrimary
                    },
                    modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_xs))
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal)
                    )
            ) {
                Tooltip(
                    tooltip = internalFile.actualFileName
                ) {
                    Text(
                        text = internalFile.actualFileName,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.MiddleEllipsis
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = onFormatDateTime(internalFile.metadata.editedAt),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .padding(end = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
                            .weight(1f)
                    )
                    Text(
                        text = onFormatStorageSize(internalFile.metadata.size),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            //Dropdown:
            if (state != DirectoryScreenState.Multiselect) {
                Box {
                    IconButton(
                        onClick = {
                            isDropdownExpanded = !isDropdownExpanded
                        }
                    ) {
                        Icon(
                            painter = painterResource(de.christian2003.core.ui.R.drawable.ic_more),
                            contentDescription = ""
                        )
                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = {
                                isDropdownExpanded = false
                            }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(stringResource(R.string.directory_file_rename))
                                },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(de.christian2003.core.ui.R.drawable.ic_edit),
                                        contentDescription = ""
                                    )
                                },
                                onClick = {
                                    onRename(internalFile)
                                    isDropdownExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(stringResource(R.string.directory_file_delete))
                                },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(de.christian2003.core.ui.R.drawable.ic_delete),
                                        contentDescription = ""
                                    )
                                },
                                onClick = {
                                    onDelete(internalFile)
                                    isDropdownExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(stringResource(R.string.directory_file_openWith))
                                },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(de.christian2003.core.ui.R.drawable.ic_external),
                                        contentDescription = ""
                                    )
                                },
                                onClick = {
                                    onOpenWith(internalFile)
                                    isDropdownExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(stringResource(R.string.directory_file_moreInfo))
                                },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(de.christian2003.core.ui.R.drawable.ic_info_outlined),
                                        contentDescription = ""
                                    )
                                },
                                onClick = {
                                    onMoreInfo(internalFile)
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
