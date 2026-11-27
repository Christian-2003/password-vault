package de.christian2003.passwordvault.infrastructure.update

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.core.common.domain.entities.WebResponse
import de.christian2003.core.common.domain.services.WebRequestService
import de.christian2003.passwordvault.R
import de.christian2003.passwordvault.domain.repositories.UpdateRepository
import de.christian2003.passwordvault.infrastructure.update.dto.UpdateRootRestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.coroutines.resume


internal class RestUpdateRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val webRequestService: WebRequestService
) : UpdateRepository {

    /**
     * URL of the REST API where to fetch info about the legal pages.
     */
    private val restApiUrl: String = "https://api.christian2003.de/v1/versions/latest/?package=${context.packageName}"

    /**
     * Response from the REST API. This is null if the request was not made yet.
     */
    private var updateResponseCache: UpdateRootRestDto? = null

    /**
     * Indicates whether the app is currently downloading an update.
     */
    private var isDownloading: Boolean = false


    /**
     * Checks whether an update is available.
     *
     * @return  Whether an update is available.
     */
    override suspend fun isUpdateAvailable(): Boolean = withContext(Dispatchers.IO) {
        val updateResponse: UpdateRootRestDto? = this@RestUpdateRepository.updateResponseCache
        if (updateResponse != null) {
            return@withContext isNewVersionAvailable(updateResponse)
        }

        val response: WebResponse = webRequestService.request(restApiUrl)

        if (!response.isSuccessful) {
            return@withContext false
        }

        val jsonResponse: String = response.content ?: ""

        val responseBody: UpdateRootRestDto? = try {
            Json.decodeFromString<UpdateRootRestDto>(jsonResponse)
        } catch (_: Exception) {
            null
        }

        if (responseBody == null) {
            return@withContext false
        }

        val isNewVersionAvailable: Boolean = isNewVersionAvailable(responseBody)
        this@RestUpdateRepository.updateResponseCache = responseBody

        return@withContext isNewVersionAvailable
    }


    /**
     * Requests the download of the newest version of the app.
     * The download runs in a separate coroutine through an Android DownloadManager.
     */
    override suspend fun requestDownload() = suspendCancellableCoroutine { continuation ->
        if (isDownloading) {
            return@suspendCancellableCoroutine
        }
        isDownloading = true

        val downloadUrl: Uri? = try {
            updateResponseCache?.version?.downloadUrl?.toUri()
        } catch (_: Exception) {
            null
        }
        if (downloadUrl == null) {
            isDownloading = false
            return@suspendCancellableCoroutine
        }

        val apkFileName: String = downloadUrl.lastPathSegment ?: "${context.packageName}-update.apk"

        val downloadRequest = DownloadManager.Request(downloadUrl)
            .setTitle(context.getString(R.string.update_download_title))
            .setDescription(apkFileName)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, apkFileName)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        val downloadManager: DownloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId: Long = downloadManager.enqueue(downloadRequest)

        val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val id: Long = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id != downloadId) {
                    return
                }

                context.unregisterReceiver(this)

                val uri: Uri = downloadManager.getUriForDownloadedFile(downloadId)
                Log.d("Update", "Begin installing '${uri}'")

                val installIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/vnd.android.package-archive")
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(installIntent)
                if (!continuation.isCompleted) {
                    continuation.resume(Unit)
                }
            }
        }

        context.registerReceiver(
            broadcastReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            Context.RECEIVER_EXPORTED
        )

        continuation.invokeOnCancellation {
            try {
                context.unregisterReceiver(broadcastReceiver)
            }
            catch (_: Exception) { }
            finally {
                isDownloading = false
            }
        }
    }


    /**
     * Returns the code of the app version currently installed.
     *
     * @return  Current version code.
     */
    private fun getCurrentVersionCode(): Long {
        val packageName: String = context.packageName
        val packageInfo: PackageInfo = context.packageManager.getPackageInfo(packageName, 0)
        return packageInfo.longVersionCode
    }


    /**
     * Checks whether the specified update response contains a version that is newer than the
     * version of the installed app.
     *
     * @param updateResponse    Update response.
     * @return                  Whether the response contains a newer version.
     */
    private fun isNewVersionAvailable(updateResponse: UpdateRootRestDto): Boolean {
        val currentVersionCode: Long = getCurrentVersionCode()
        val latestVersionCode: Long = updateResponse.version?.versionCode ?: -1
        return latestVersionCode > currentVersionCode
    }

}
