package de.christian2003.security.domain.services


/**
 * Service to derive a key from a provided input.
 */
interface KdfService {

    /**
     * Derives another key based on the specified input.
     *
     * @param input Input from which to derive a key.
     * @param salt  Salt.
     * @return      Derived key.
     */
    fun derive(input: ByteArray, salt: ByteArray): ByteArray


    /**
     * Derives another key based on the specified input.
     *
     * @param input Input from which to derive a key.
     * @param salt  Salt.
     * @return      Derived key.
     */
    fun derive(input: CharArray, salt: ByteArray): ByteArray

}
