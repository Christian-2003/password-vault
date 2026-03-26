package de.christian2003.feature.files.ui.directory

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.dp
import de.christian2003.core.ui.composables.ListItemContainer
import de.christian2003.core.ui.composables.Shape
import de.christian2003.data.files.domain.entities.InternalDirectory
import de.christian2003.feature.files.R
import de.christian2003.feature.files.models.states.DirectoryScreenState


@Composable
internal fun DirectoryListItem(
    state: DirectoryScreenState,
    internalDirectory: InternalDirectory,
    isFirst: Boolean,
    isLast: Boolean,
    isSelected: Boolean,
    onClick: (InternalDirectory) -> Unit,
    onDelete: (InternalDirectory) -> Unit,
    onRename: (InternalDirectory) -> Unit,
    onStartMultiselect: (InternalDirectory) -> Unit,
    onToggleSelected: (InternalDirectory) -> Unit
) {
    var isDropdownExpanded: Boolean by remember { mutableStateOf(false) }

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
                            DirectoryScreenState.Default -> onClick(internalDirectory)
                            DirectoryScreenState.Multiselect -> onToggleSelected(internalDirectory)
                        }
                    },
                    onLongClick = {
                        when (state) {
                            DirectoryScreenState.Default -> onStartMultiselect(internalDirectory)
                            DirectoryScreenState.Multiselect -> onToggleSelected(internalDirectory)
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
                    MaterialTheme.colorScheme.secondaryContainer
                } else {
                    MaterialTheme.colorScheme.primary
                },
                modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_m))
            ) {
                Icon(
                    painter = if (!isSelected) {
                        painterResource(R.drawable.ic_directory)
                    } else {
                        painterResource(de.christian2003.core.ui.R.drawable.ic_check)
                    },
                    contentDescription = "",
                    tint = if (!isSelected) {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    } else {
                        MaterialTheme.colorScheme.onPrimary
                    },
                    modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_xs))
                )
            }
            Text(
                text = internalDirectory.internalName,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal)
                    )
            )

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
                                    Text(stringResource(R.string.directory_rename))
                                },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(de.christian2003.core.ui.R.drawable.ic_edit),
                                        contentDescription = ""
                                    )
                                },
                                onClick = {
                                    onRename(internalDirectory)
                                    isDropdownExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(stringResource(R.string.directory_delete))
                                },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(de.christian2003.core.ui.R.drawable.ic_delete),
                                        contentDescription = ""
                                    )
                                },
                                onClick = {
                                    onDelete(internalDirectory)
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
