package de.christian2003.feature.accounts.ui.account

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import de.christian2003.core.ui.composables.EmptyPlaceholder
import de.christian2003.core.ui.composables.SearchField
import de.christian2003.feature.accounts.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


/**
 * Dialog through which to select installed Android packages.
 *
 * @param packageNames              List of the package names for all installed Android packages.
 * @param selectedPackages          Set of the package names of all selected packages.
 * @param getLocalizedPackageName   Callback invoked to query a localized name for an installed package.
 * @param getPackageIcon            Callback invoked to query the icon for an installed package.
 * @param onDismiss                 Callback invoked to dismiss the dialog.
 * @param onSave                    Callback invoked to save a set of installed packages.
 */
@Composable
internal fun SelectPackageDialog(
    packageNames: List<String>?,
    selectedPackages: Set<String>,
    getLocalizedPackageName: (String) -> String?,
    getPackageIcon: (String) -> Drawable?,
    onDismiss: () -> Unit,
    onSave: (Set<String>) -> Unit
) {
    var filteredPackageNames: List<String> by remember(packageNames) { mutableStateOf(packageNames ?: listOf()) }
    val mutableSetSaver = Saver<MutableSet<String>, List<String>>(
        save = { set -> set.toList() },
        restore = { list -> list.toMutableSet() }
    )
    val mutableSelectedPackages: MutableSet<String> = rememberSaveable(saver = mutableSetSaver) { mutableStateSetOf() }
    mutableSelectedPackages.addAll(selectedPackages)
    val focusRequester: FocusRequester = remember { FocusRequester() }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 24.dp,
                        bottom = 24.dp
                    )
            ) {
                DialogHeader(
                    focusRequester = focusRequester,
                    onSearch = { query ->
                        val lowercaseQuery: String = query.lowercase()
                        val filteredPackages: MutableList<String> = mutableListOf()
                        packageNames?.forEach { packageName ->
                            if (packageName.lowercase().contains(lowercaseQuery)) {
                                filteredPackages.add(packageName)
                            }
                            else {
                                val localizedName: String? = getLocalizedPackageName(packageName)
                                if (localizedName != null && localizedName.lowercase().contains(query)) {
                                    filteredPackages.add(packageName)
                                }
                            }
                        }
                        filteredPackageNames = filteredPackages
                    }
                )
                HorizontalDivider()
                if (packageNames == null) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(24.dp)
                    )
                }
                else {
                    if (filteredPackageNames.isEmpty()) {
                        EmptyPlaceholder(
                            title = stringResource(R.string.target_packages_queryEmptyPlaceholder_title),
                            subtitle = stringResource(R.string.target_packages_queryEmptyPlaceholder_subtitle),
                            painter = painterResource(de.christian2003.core.ui.R.drawable.el_search),
                            modifier = Modifier
                                .padding(
                                    vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                                )
                        )
                    }
                    else {
                        LazyColumn(
                            modifier = Modifier.weight(1f)
                        ) {
                            items(filteredPackageNames) { packageName ->
                                PackageListRow(
                                    packageName = packageName,
                                    isSelected = mutableSelectedPackages.contains(packageName),
                                    onSelectPackage = { packageName ->
                                        mutableSelectedPackages.add(packageName)
                                    },
                                    onDeselectPackage = { packageName ->
                                        mutableSelectedPackages.remove(packageName)
                                    },
                                    getPackageIcon = getPackageIcon,
                                    getLocalizedPackageName = getLocalizedPackageName
                                )
                            }
                        }
                    }
                }
                HorizontalDivider()
                FlowRow(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(
                            start = 24.dp,
                            top = 16.dp,
                            end = 24.dp
                        )
                ) {
                    TextButton(
                        onClick = {
                            onDismiss()
                        }
                    ) {
                        Text(stringResource(de.christian2003.core.ui.R.string.button_cancel))
                    }
                    TextButton(
                        onClick = {
                            onSave(mutableSelectedPackages)
                        },
                        modifier = Modifier.padding(start = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
                    ) {
                        Text(stringResource(de.christian2003.core.ui.R.string.button_ok))
                    }
                }
            }
        }
    }
}


/**
 * List row displaying a single installed app (package).
 *
 * @param packageName               Package name.
 * @param isSelected                Whether the package is selected.
 * @param onSelectPackage           Callback invoked to select the package.
 * @param onDeselectPackage         Callback invoked to deselect the package.
 * @param getPackageIcon            Callback invoked to query the icon for the package.
 * @param getLocalizedPackageName   Callback invoked to query the localized package name.
 */
@Composable
private fun PackageListRow(
    packageName: String,
    isSelected: Boolean,
    onSelectPackage: (String) -> Unit,
    onDeselectPackage: (String) -> Unit,
    getPackageIcon: (String) -> Drawable?,
    getLocalizedPackageName: (String) -> String?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (isSelected) {
                    onDeselectPackage(packageName)
                }
                else {
                    onSelectPackage(packageName)
                }
            }
            .padding(
                start = 24.dp,
                top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical),
                end = 12.dp, //24 - 12 = 12
                bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
            )
    ) {
        Image(
            painter = rememberDrawablePainter(getPackageIcon(packageName)),
            contentDescription = "",
            modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_s))
        )
        Text(
            text = getLocalizedPackageName(packageName) ?: packageName,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(start = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
                .weight(1f)
        )
        Checkbox(
            checked = isSelected,
            onCheckedChange = {
                if (isSelected) {
                    onDeselectPackage(packageName)
                }
                else {
                    onSelectPackage(packageName)
                }
            }
        )
    }
}


/**
 * Header of the dialog either shows a simple title or a search input through which the user can
 * filter the installed apps.
 *
 * @param focusRequester    Focus requester used to request focus on the query input.
 * @param onSearch          Callback invoked to begin searching the installed apps.
 * @param modifier          Modifier.
 */
@Composable
private fun DialogHeader(
    focusRequester: FocusRequester,
    onSearch: suspend (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery: String? by remember { mutableStateOf(null) }
    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 24.dp,
                bottom = 16.dp
            )
    ) {
        if (searchQuery == null) {
            Text(
                text = stringResource(R.string.target_packages_title),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall,
                modifier = modifier.weight(1f)
            )
        }
        else {
            val query: String? = searchQuery
            if (query != null) {
                SearchField(
                    query = query,
                    hint = stringResource(R.string.target_packages_queryHint),
                    focusRequester = focusRequester,
                    onQueryChange = { query ->
                        searchQuery = query
                    },
                    onSearch = {
                        coroutineScope.launch {
                            onSearch(query)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
                LaunchedEffect(Unit) {
                    //Safe-call required, similar to all other focus requesters.
                    focusRequester?.requestFocus()
                }
            }
        }

        IconButton(
            onClick = {
                if (searchQuery == null) {
                    //Show query input:
                    searchQuery = ""
                }
                else {
                    //Start search:
                    coroutineScope.launch {
                        val query: String? = searchQuery
                        if (query != null) {
                            onSearch(query)
                        }
                    }
                }
            },
            modifier = Modifier.padding(
                start = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal),
                end = 24.dp - 12.dp
            )
        ) {
            Icon(
                painter = painterResource(de.christian2003.core.ui.R.drawable.ic_search),
                contentDescription = ""
            )
        }
    }
}
