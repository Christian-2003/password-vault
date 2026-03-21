package de.christian2003.data.files.application.usecases

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.data.files.domain.entities.SharedFileMetadata
import de.christian2003.data.files.domain.repositories.SharedFilesRepository
import java.io.File
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject


class RemoveSharedFilesUseCase @Inject internal constructor(
    @param:ApplicationContext private val context: Context,
    private val sharedFilesRepository: SharedFilesRepository
) {

    fun removeSharedFiles() {
        val filesToDelete: List<SharedFileMetadata> = getAllFilesToDelete()
        val deletedFiles: MutableList<String> = mutableListOf()

        val sharedDir = File(context.cacheDir, "shared")

        filesToDelete.forEach { file ->
            val fileToDelete = File(sharedDir, file.actualFileName)
            try {
                val result: Boolean = fileToDelete.delete()
                if (result) {
                    deletedFiles.add(file.actualFileName)
                }
            }
            catch (_: Exception) { }
        }

        sharedFilesRepository.removeSharedFiles(deletedFiles)
    }


    /**
     * Returns a list of all shared files that should be deleted by this worker.
     * The returned list will only contain the file names. The paths are not part of the result.
     *
     * @return  List of files to delete.
     */
    private fun getAllFilesToDelete(): List<SharedFileMetadata> {
        val sharedFiles: List<SharedFileMetadata> = sharedFilesRepository.getSharedFiles()
        val filesToDelete: MutableList<SharedFileMetadata> = mutableListOf()
        val now: LocalDateTime = LocalDateTime.now()

        sharedFiles.forEach { sharedFile ->
            val minutesBetween: Long = Duration.between(sharedFile.timestamp, now).abs().toMinutes()
            if (minutesBetween > 30) {
                filesToDelete.add(sharedFile)
                Log.d("Filesystem", "Add file '${sharedFile.actualFileName}' for deletion")
            }
            else {
                Log.d("Filesystem", "Do not add file '${sharedFile.actualFileName}' for deletion")
            }
        }

        return filesToDelete
    }

}
