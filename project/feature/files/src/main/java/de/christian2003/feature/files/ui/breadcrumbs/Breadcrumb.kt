package de.christian2003.feature.files.ui.breadcrumbs

import androidx.compose.ui.graphics.painter.Painter


/**
 * Models a single breadcrumb inside a breadcrumbs navigation.
 *
 * @param label         Label for the breadcrumb.
 * @param leadingIcon   Optional painter for the leading icon.
 * @param onClick       Optional callback invoked once the breadcrumb is clicked. Pass null to make
 *                      the breadcrumb item not clickable.
 */
data class Breadcrumb(
    val label: String,
    val leadingIcon: Painter? = null,
    val onClick: (() -> Unit)? = null,
)
