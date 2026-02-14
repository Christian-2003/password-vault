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
class ValidatePackageAgainstTargetUseCase @Inject internal constructor(
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
            val fingerprint: ByteArray? = getFingerprintFromAndroidTarget(target)
            if (fingerprint != null) {
                return packageFingerprintService.validate(packageName, fingerprint)
            }
        }
        return false
    }


    /**
     * Retrieves the package fingerprint from the target. The provided target must be an Android app.
     *
     * @param target    Target from which to extract the package fingerprint.
     * @return          Bytes of the fingerprint.
     */
    private fun getFingerprintFromAndroidTarget(target: Target): ByteArray? {
        try {
            val url: Uri = target.url
            val fingerprint: String? = url.userInfo
            if (fingerprint != null) {
                val decodedFingerprint: ByteArray = fingerprintEncoderService.decode(fingerprint)
                return decodedFingerprint
            }
        }
        catch (_: Exception) { }
        return null
    }

}
