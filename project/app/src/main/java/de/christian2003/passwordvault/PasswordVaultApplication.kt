package de.christian2003.passwordvault

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import de.christian2003.data.files.infrastructure.registerSharedFilesClearingWorker
import javax.inject.Inject


/**
 * Application implementation for the entire program.
 */
@HiltAndroidApp
class PasswordVaultApplication(): Application(), Configuration.Provider {

    /**
     * Worker factory for the hilt extension.
     */
    @Inject lateinit var workerFactory: HiltWorkerFactory


    /**
     * Instantiates a new application.
     */
    override fun onCreate() {
        super.onCreate()

        //WorkManager.initialize(this, workManagerConfiguration)
    }


    /**
     * Configuration for the work manager that uses the worker factory for the hilt extension.
     */
    override val workManagerConfiguration: Configuration
        get() {
            return Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()
        }

}
