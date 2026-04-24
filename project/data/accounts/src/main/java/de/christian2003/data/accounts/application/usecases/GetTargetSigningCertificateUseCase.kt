package de.christian2003.data.accounts.application.usecases

import de.christian2003.data.accounts.application.services.PackageFingerprintEncoderService
import de.christian2003.data.accounts.domain.entities.Target
import de.christian2003.data.accounts.domain.services.PackageFingerprintService
import java.security.cert.X509Certificate
import javax.inject.Inject


/**
 * Use case to get the X.509 certificate used to sign an Android target.
 *
 * @param packageFingerprintService Service used for package fingerprints.
 * @param fingerprintEncoderService Service used to encode or decode fingerprints.
 */
class GetTargetSigningCertificateUseCase @Inject internal constructor(
    private val packageFingerprintService: PackageFingerprintService,
    private val fingerprintEncoderService: PackageFingerprintEncoderService
) {

    /**
     * Returns the X.509 certificate used to sign the specified Android app target. If no certificate
     * can be identified (e.g. because the target is a website or because the target is not installed
     * on the device or changed the certificate), null is returned.
     *
     * @param target    Android target whose signing certificate to return.
     * @return          X.509 certificate that was used to sign the Android target app or null.
     */
    fun getSigningCertificate(target: Target): X509Certificate? {
        if (target.isAndroidApp()) {
            val fingerprintAsString: String? = target.url.userInfo
            if (fingerprintAsString != null) {
                val fingerprintAsBytes: ByteArray = fingerprintEncoderService.decode(fingerprintAsString)
                val x509: X509Certificate? = packageFingerprintService.getCertificateForPackage(target.name, fingerprintAsBytes)
                return x509
            }
        }

        return null
    }

}
