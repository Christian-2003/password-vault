package de.christian2003.data.accounts.application.services

import javax.inject.Inject
import kotlin.io.encoding.Base64


/**
 * Service for encoding and decoding of package fingerprints into a format that can be used in the
 * "android://"-scheme URL as user info for autofill targets.
 */
internal class PackageFingerprintEncoderService @Inject constructor() {

    /**
     * Encodes the provided package fingerprint.
     *
     * @param fingerprint   Decoded fingerprint to encode.
     * @return              Encoded fingerprint.
     */
    fun encode(fingerprint: ByteArray): String {
        val base64Encoded: String = Base64.encode(fingerprint)
        val urlSafeEncoded: String = base64Encoded.replace('/', '_').replace('+', '-')
        return urlSafeEncoded
    }


    /**
     * Decodes the provided package fingerprint.
     *
     * @param fingerprint   Encoded fingerprint to decode.
     * @return              Decoded fingerprint.
     */
    fun decode(fingerprint: String): ByteArray {
        val original: String = fingerprint.replace('_', '/').replace('-', '+')
        val decoded: ByteArray = Base64.decode(original)
        return decoded
    }

}
