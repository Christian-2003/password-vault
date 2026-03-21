package de.christian2003.data.files.infrastructure

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import de.christian2003.data.files.application.usecases.RemoveSharedFilesUseCase


@HiltWorker
class SharedFilesClearingWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val removeSharedFilesUseCase: RemoveSharedFilesUseCase
): Worker(context, workerParams) {

    override fun doWork(): Result {
        try {
            removeSharedFilesUseCase.removeSharedFiles()
            return Result.success()
        }
        catch (e: Exception) {
            Log.e("Filesystem", "Error occurred while trying to cleanup shared files: ${e.message ?: "Unknown error"}")
            return Result.failure()
        }
    }

}


/**
 * Extension method for work manager used to register the SharedGFilesClearingWorker with the work
 * manager.
 */
fun WorkManager.registerSharedFilesClearingWorker() {
    val testRequest = OneTimeWorkRequestBuilder<SharedFilesClearingWorker>().build()
    this.enqueue(testRequest)

    /*
    val workRequest: PeriodicWorkRequest = PeriodicWorkRequestBuilder<SharedFilesClearingWorker>(8, TimeUnit.HOURS).build()

    enqueueUniquePeriodicWork(
        "sharedFilesClearingWorker",
        ExistingPeriodicWorkPolicy.KEEP,
        workRequest
    )*/
}
