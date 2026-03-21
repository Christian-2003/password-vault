package de.christian2003.data.files.application.usecases

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
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
        val filesToDelete: List<String> = getAllFilesToDelete()
        val deletedFiles: MutableList<String> = mutableListOf()

        val sharedDir = File(context.cacheDir, "shared")

        filesToDelete.forEach { fileName ->
            val fileToDelete = File(sharedDir, fileName)
            try {
                val result: Boolean = fileToDelete.delete()
                if (result) {
                    deletedFiles.add(fileName)
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
    private fun getAllFilesToDelete(): List<String> {
        val sharedFiles: Map<String, LocalDateTime> = sharedFilesRepository.getSharedFiles()
        val filesToDelete: MutableList<String> = mutableListOf()
        val now: LocalDateTime = LocalDateTime.now()

        sharedFiles.forEach { fileName, timestamp ->
            val minutesBetween: Long = Duration.between(timestamp, now).abs().toMinutes()
            if (minutesBetween > 30) {
                filesToDelete.add(fileName)
                Log.d("Filesystem", "Add file '$fileName' for deletion")
            }
            else {
                Log.d("Filesystem", "Do not add file '$fileName' for deletion")
            }
        }

        return filesToDelete
    }

}
