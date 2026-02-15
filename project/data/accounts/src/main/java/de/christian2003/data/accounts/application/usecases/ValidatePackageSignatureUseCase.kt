package de.christian2003.data.accounts.application.usecases

import android.net.Uri
import de.christian2003.data.accounts.application.services.PackageFingerprintEncoderService
import de.christian2003.data.accounts.domain.entities.Target
import de.christian2003.data.accounts.domain.services.PackageFingerprintService
import javax.inject.Inject


/**
 * Use case to validate an installed Android package against an autofill target.
 *
 * @param packageFingerprintService Service for package fingerprints.
 * @param fingerprintEncoderService Service for encoding and decoding package fingerprints.
 */
class ValidatePackageSignatureUseCase @Inject internal constructor(
    private val packageFingerprintService: PackageFingerprintService,
    private val fingerprintEncoderService: PackageFingerprintEncoderService,
) {

    /**
     * Validates the specified Android package against the provided target.
     *
     * @param packageName   Package to verify.
     * @param target        Android app target against which to verify the installed package.
     * @return              Whether the installed package's fingerprint matches the target fingerprint.
     */
    fun validate(packageName: String, target: Target): Boolean {
        if (target.isAndroidApp()) {
            return validate(packageName, target.url)
        }
        return false
    }


    /**
     * Validates the specified installed Android package against it's target URL (the URL should be
     * from an autofill target).
     *
     * @param packageName   Package to verify.
     * @param targetUrl     URL of the Android app target against which to verify the installed package.
     * @return              Whether the installed package's fingerprint matches the target URL fingerprint.
     */
    fun validate(packageName: String, targetUrl: Uri): Boolean {
        try {
            val fingerprint: ByteArray? = getFingerprintFromTargetUrl(targetUrl)
            if (fingerprint != null) {
                return packageFingerprintService.validate(packageName, fingerprint)
            }
        }
        catch (_: Exception) { }
        return false
    }


    /**
     * Retrieves the package fingerprint from the target URL. The provided URL must be from an
     * Android app.
     *
     * @param targetUrl URL of the target from which to extract the package fingerprint.
     * @return          Bytes of the fingerprint.
     */
    private fun getFingerprintFromTargetUrl(targetUrl: Uri): ByteArray? {
        try {
            val fingerprint: String? = targetUrl.userInfo
            if (fingerprint != null) {
                val decodedFingerprint: ByteArray = fingerprintEncoderService.decode(fingerprint)
                return decodedFingerprint
            }
        }
        catch (_: Exception) { }
        return null
    }

}
