package de.christian2003.data.accounts.domain.services


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

}
