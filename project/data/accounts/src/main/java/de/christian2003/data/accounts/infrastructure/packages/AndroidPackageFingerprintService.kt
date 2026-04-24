package de.christian2003.data.accounts.infrastructure.packages

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.content.pm.SigningInfo
import de.christian2003.data.accounts.domain.services.PackageFingerprintService
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.inject.Inject
import kotlin.math.sign


/**
 * Implementation for the fingerprint service.
 *
 * @param packageManager    Package manager.
 */
internal class AndroidPackageFingerprintService @Inject constructor(
    private val packageManager: PackageManager
): PackageFingerprintService {

    /**
     * Factory used to build certificates.
     */
    private val certFactory: CertificateFactory = CertificateFactory.getInstance("X.509")

    /**
     * Message digest used for hashing.
     */
    private val digester: MessageDigest = MessageDigest.getInstance("SHA-512")


    /**
     * Returns the fingerprint of the signing certificate for the package that is passed as argument.
     * If no package with the specified name exists, null is returned.
     *
     * @param packageName   Name of the package for which to return the fingerprint.
     * @return              Fingerprint of the package signing certificate.
     */
    override fun getPackageFingerprint(packageName: String): ByteArray? {
        try {
            val signingInfo: SigningInfo? = getSigningInfo(packageName)
            if (signingInfo == null) {
                return null
            }

            if (signingInfo.apkContentsSigners.size > 0) {
                val hash = hashSignature(signingInfo.apkContentsSigners.first())
                return hash
            }

            return null
        }
        catch (_: Exception) {
            return null
        }
    }


    /**
     * Validates whether the fingerprint of the specified package matches the provided fingerprint.
     *
     * @param packageName   Package to validate.
     * @param fingerprint   Fingerprint against which to verify the package.
     * @return              Whether the package matches the specified fingerprint.
     */
    override fun validate(packageName: String, fingerprint: ByteArray): Boolean {
        try {
            val signingInfo: SigningInfo? = getSigningInfo(packageName)
            if (signingInfo == null) {
                return false
            }

            signingInfo.apkContentsSigners.forEach { signature ->
                try {
                    val hash = hashSignature(signature)
                    if (hash.contentEquals(fingerprint)) {
                        //Package fingerprint matches:
                        return true
                    }
                }
                catch (_: Exception) { }
            }
            signingInfo.signingCertificateHistory.forEach { signature ->
                try {
                    val hash = hashSignature(signature)
                    if (hash.contentEquals(fingerprint)) {
                        //Package fingerprint matches:
                        return true
                    }
                }
                catch (_: Exception) { }
            }
        }
        catch (_: Exception) { }
        return false
    }


    /**
     * Returns the X.509 certificate that was used for signing the specified package. The certificate
     * returns is determined from the list of signing certificates based on the fingerprint provided.
     * If no certificate can be determined, null is returned.
     *
     * @param packageName   Name of the package for which to return the X.509 certificate.
     * @param fingerprint   Fingerprint of the X.509 certificate to return.
     * @return              X.509 certificate that was used for signing the specified package or null
     *                      if no certificate can be determined.
     */
    override fun getCertificateForPackage(packageName: String, fingerprint: ByteArray): X509Certificate? {
        try {
            var matchingSignature: Signature? = null
            val signingInfo: SigningInfo? = getSigningInfo(packageName)
            if (signingInfo == null) {
                return null
            }

            signingInfo.apkContentsSigners.forEach { signature ->
                try {
                    val hash = hashSignature(signature)
                    if (hash.contentEquals(fingerprint)) {
                        //Package fingerprint matches:
                        matchingSignature = signature
                    }
                }
                catch (_: Exception) { }
            }
            if (matchingSignature == null) {
                signingInfo.signingCertificateHistory.forEach { signature ->
                    try {
                        val hash = hashSignature(signature)
                        if (hash.contentEquals(fingerprint)) {
                            //Package fingerprint matches:
                            matchingSignature = signature
                        }
                    }
                    catch (_: Exception) { }
                }
            }

            if (matchingSignature != null) {
                val x509: X509Certificate = getX509CertFromSignature(matchingSignature)
                return x509
            }
        }
        catch (_: Exception) { }
        return null
    }


    private fun getSigningInfo(packageName: String): SigningInfo? {
        val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
        val signingInfo: SigningInfo? = packageInfo.signingInfo
        return signingInfo
    }


    private fun getX509CertFromSignature(signature: Signature): X509Certificate {
        val cert: Certificate = certFactory.generateCertificate(ByteArrayInputStream(signature.toByteArray()))
        val x509: X509Certificate = cert as X509Certificate
        return x509
    }


    /**
     * Hashes the certificate of the signature passed as argument.
     *
     * @param signature Signature whose certificate hash to return.
     * @return          Hashed certificate.
     */
    private fun hashSignature(signature: Signature): ByteArray {
        val x509: X509Certificate = getX509CertFromSignature(signature)
        val hash: ByteArray = digester.digest(x509.encoded)
        return hash
    }

}
