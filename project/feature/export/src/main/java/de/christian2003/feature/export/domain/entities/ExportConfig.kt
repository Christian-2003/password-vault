package de.christian2003.feature.export.domain.entities

import android.net.Uri
import kotlin.uuid.Uuid


/**
 * Value object contains the configuration for creating an export.
 *
 * @param accounts          IDs of the accounts to include in the export.
 * @param files             Internal names of the files to include.
 * @param exportDestination Destination URI where to create the exported file.
 */
internal data class ExportConfig(
    val accounts: Set<Uuid>,
    val files: Set<String>,
    val exportDestination: Uri
)
