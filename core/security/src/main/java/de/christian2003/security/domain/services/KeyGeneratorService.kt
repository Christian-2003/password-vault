package de.christian2003.security.domain.services


/**
 * Service can generate a cryptographic key.
 */
interface KeyGeneratorService {

    /**
     * Generates a cryptographic key.
     *
     * @return  Bytes of the generated key.
     */
    fun generate(): ByteArray

}
