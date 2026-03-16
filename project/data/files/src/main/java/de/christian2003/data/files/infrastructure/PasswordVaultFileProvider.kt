package de.christian2003.data.files.infrastructure

import androidx.core.content.FileProvider


/**
 * File provider allows other apps to access app-internal files through a content URI.
 *
 * According to the Android documentation, we need to create a subclass of FileProvider to reference
 * in the manifest, even if we do not add any logic to it.
 *
 * https://developer.android.com/reference/androidx/core/content/FileProvider
 */
class PasswordVaultFileProvider : FileProvider() {

}
