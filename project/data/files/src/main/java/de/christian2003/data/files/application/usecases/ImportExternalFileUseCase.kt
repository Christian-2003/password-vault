package de.christian2003.data.files.application.usecases

import android.net.Uri
import de.christian2003.data.files.domain.entities.InternalDirectory
import de.christian2003.data.files.domain.repositories.InternalFilesystemRepository
import de.christian2003.data.files.domain.services.FileCopyService
import javax.inject.Inject
import kotlin.uuid.Uuid


class ImportExternalFileUseCase @Inject internal constructor(
    private val repository: InternalFilesystemRepository,
    private val fileCopyService: FileCopyService
) {

    suspend fun importExternalFile(externalFileUri: Uri, internalDirectory: InternalDirectory) {
        val internalFileName = "${Uuid.random()}.enc"

        repository.copyFileToDirectory(externalFileUri, internalFileName, internalDirectory)

        //TODO: Add entry in lookup table:
    }

}
