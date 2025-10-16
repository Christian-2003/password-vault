package de.christian2003.passwordvault.application.usecases.packages

import android.net.Uri
import android.util.Base64
import android.util.Log
import de.christian2003.passwordvault.domain.model.target.Target


/**
 * Service to create a target for an Android app.
 *
 * @param packageFingerprintService Service through which to get the fingerprint for installed Android apps.
 */
class CreateAndroidTargetService(
    private val packageFingerprintService: PackageFingerprintService
) {

    /**
     * Creates an android target for the specified package name.
     *
     * @param packageName   Package name for which to create the target.
     * @return              Target for the Android app whose package name was passed as argument.
     */
    fun createAndroidTarget(
        packageName: String
    ): Target? {
        val fingerprint: ByteArray? = packageFingerprintService.getPackageFingerprint(packageName)
        if (fingerprint == null) {
            return null
        }
        val fingerprintBase64: String = Base64.encodeToString(fingerprint, Base64.DEFAULT)

        Log.d("Target", "Fingerprint for $packageName: $fingerprintBase64")

        val url: Uri = Uri.Builder()
            .scheme("android")
            .encodedAuthority("$fingerprintBase64@$packageName")
            .path("/")
            .build()

        return Target(
            name = packageName,
            url = url
        )
    }

}
