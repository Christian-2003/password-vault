package de.christian2003.passwordvault.domain.model.target

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

}