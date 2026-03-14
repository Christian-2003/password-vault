package de.christian2003.data.files.application.usecases

import android.util.Log
import de.christian2003.data.files.domain.entities.InternalDirectory
import de.christian2003.data.files.domain.repositories.InternalFilesystemRepository
import javax.inject.Inject


class CreateInternalDirectoryUseCase @Inject internal constructor(
    private val repository: InternalFilesystemRepository
) {

    fun create(name: String, parentDirectory: InternalDirectory) {
        Log.d("Files", "Use case start creation of '${name}'")
        val directory = InternalDirectory(
            internalPath = parentDirectory.internalPath + "/" + name
        )
        repository.addDirectory(directory)
        Log.d("Files", "Use case finish creation of '${name}'")
    }

}
