package de.christian2003.security.domain.repositories


/**
 * Repository for the setup of the authentication can set the biometrics.
 */
interface BiometricsRepository {

    /**
     * Sets the encrypted KEK from the biometrics.
     *
     * @param encryptedKekBytes Bytes of the encrypted KEK.
     */
    fun setEncryptedBiometricsKek(encryptedKekBytes: ByteArray)


    /**
     * Returns the current encrypted KEK for the biometrics. If no KEK exists, null is returned.
     *
     * @return  Bytes of the encrypted KEK or null.
     */
    fun getEncryptedBiometricsKek(): ByteArray?


    /**
     * Tests whether the encrypted KEK for the biometrics exists.
     *
     * @return  Whether the encrypted KEK for the biometrics exists.
     */
    fun hasEncryptedBiometricsKek(): Boolean


    /**
     * Returns whether biometrics are available on the device.
     *
     * @return  Whether biometrics are available.
     */
    fun areBiometricsAvailable(): Boolean

}
