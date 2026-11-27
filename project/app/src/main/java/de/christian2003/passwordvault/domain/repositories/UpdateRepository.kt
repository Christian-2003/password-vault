package de.christian2003.passwordvault.domain.repositories


/**
 * Repository to check for app updates.
 */
internal interface UpdateRepository {

    /**
     * Checks whether an update is available.
     *
     * @return  Whether an update is available.
     */
    suspend fun isUpdateAvailable(): Boolean

    /**
     * Requests the download of the newest version of the app.
     */
    suspend fun requestDownload()

}
