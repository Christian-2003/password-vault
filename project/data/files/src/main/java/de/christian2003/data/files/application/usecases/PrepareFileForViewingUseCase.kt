package de.christian2003.data.files.application.usecases

import android.net.Uri
import de.christian2003.data.files.domain.entities.InternalDirectory
import de.christian2003.data.files.domain.entities.InternalFile
import de.christian2003.data.files.domain.entities.SharedFile
import de.christian2003.data.files.domain.services.FileCopyService
import javax.inject.Inject


class PrepareFileForViewingUseCase @Inject internal constructor(
    private val fileCopyService: FileCopyService
) {

    suspend fun prepare(file: InternalFile, directory: InternalDirectory): SharedFile? {
        return fileCopyService.copyInternalFileToShared(file, directory)
    }

}
