package de.christian2003.data.accounts.domain.services

import java.security.cert.X509Certificate


/**
 * Service through which to get access to a package's fingerprint.
 */
interface PackageFingerprintService {

    /**
     * Returns the fingerprint of the signing certificate for the package that is passed as argument.
     * If no package with the specified name exists, null is returned.
     *
     * @param packageName   Name of the package for which to return the fingerprint.
     * @return              Fingerprint of the package signing certificate.
     */
    fun getPackageFingerprint(packageName: String): ByteArray?


    /**
     * Validates whether the fingerprint of the specified package matches the provided fingerprint.
     *
     * @param packageName   Package to validate.
     * @param fingerprint   Fingerprint against which to verify the package.
     * @return              Whether the package matches the specified fingerprint.
     */
    fun validate(packageName: String, fingerprint: ByteArray): Boolean


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
    fun getCertificateForPackage(packageName: String, fingerprint: ByteArray): X509Certificate?

}
