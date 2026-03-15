package de.christian2003.data.files.domain.services

import android.net.Uri


internal interface FileCopyService {

    suspend fun copyExternalFileToInternal(
        sourceUri: Uri,
        destinationInternalFilePath: String
    )

}
