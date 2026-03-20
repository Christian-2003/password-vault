package de.christian2003.data.files.infrastructure

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import de.christian2003.data.files.domain.repositories.SharedFilesRepository
import java.io.File
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit


@HiltWorker
class SharedFilesClearingWorker @AssistedInject internal constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val sharedFilesRepository: SharedFilesRepository
): Worker(context, workerParams) {

    override fun doWork(): Result {
        try {
            val filesToDelete: List<String> = getAllFilesToDelete()
            val deletedFiles: MutableList<String> = mutableListOf()

            val sharedDir = File(applicationContext.cacheDir, "shared")

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

            return Result.success()
        }
        catch (e: Exception) {
            Log.e("Filesystem", "Error occurred while trying to cleanup shared files: ${e.message ?: "Unknown error"}")
            return Result.failure()
        }
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
            }
        }

        return filesToDelete
    }

}


/**
 * Extension method for work manager used to register the SharedGFilesClearingWorker with the work
 * manager.
 */
fun WorkManager.registerSharedFilesClearingWorker() {
    val workRequest: PeriodicWorkRequest = PeriodicWorkRequestBuilder<SharedFilesClearingWorker>(8, TimeUnit.HOURS).build()

    enqueueUniquePeriodicWork(
        "sharedFilesClearingWorker",
        ExistingPeriodicWorkPolicy.KEEP,
        workRequest
    )
}
