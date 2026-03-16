package de.christian2003.data.files.domain.entities

import android.net.Uri


/**
 * Models a shared file that can be opened by other apps.
 *
 * @param contentUri    Content URI for the shared file.
 * @param mimeType      Mime type for the shared file.
 */
data class SharedFile(
    val contentUri: Uri,
    val mimeType: String
)
