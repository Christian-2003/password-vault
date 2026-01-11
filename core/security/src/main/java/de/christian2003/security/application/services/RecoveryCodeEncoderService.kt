package de.christian2003.security.application.services


/**
 * Service can encode and decode recovery codes.
 */
class RecoveryCodeEncoderService {

    /**
     * Encodes the specified recovery code bytes into a Base32 char array.
     *
     * @param bytes Bytes to encode.
     * @return      Encoded char array.
     */
    fun encode(bytes: ByteArray): CharArray {
        val encodedSize: Int = ((bytes.size * 8) + 4) / 5
        val encoded = CharArray(encodedSize)

        var buffer = 0
        var bitsLeft = 0
        var index = 0

        for (byte: Byte in bytes) {
            buffer = (buffer shl 8) or (byte.toInt() and 0xFF)
            bitsLeft += 8

            while (bitsLeft >= 5) {
                val value = (buffer shr (bitsLeft - 5)) and 0x1F
                bitsLeft -= 5
                encoded[index++] = CROCKFORD_ALPHABET[value]
            }
        }

        if (bitsLeft > 0) {
            val value = (buffer shl (5 - bitsLeft)) and 0x1F
            encoded[index] = CROCKFORD_ALPHABET[value]
        }

        return encoded
    }


    /**
     * Decodes the specified characters from Base32 into a byte array.
     *
     * @param chars                     Characters to decode.
     * @return                          Decoded byte array.
     * @throws IllegalArgumentException The specified char array contains illegal Base32 characters.
     */
    fun decode(chars: CharArray): ByteArray {
        val decodedSize: Int = (chars.size * 5) / 8
        val decoded = ByteArray(decodedSize)

        var buffer = 0
        var bitsLeft = 0
        var index = 0

        for (char: Char in chars) {
            if (char.code >= CROCKFORD_LOOKUP.size) {
                throw IllegalArgumentException("Invalid Base32 character: '$char'")
            }

            val value = CROCKFORD_LOOKUP[char.code]
            if (value == -1) {
                throw IllegalArgumentException("Invalid Base32 character: '$char'")
            }

            buffer = (buffer shl 5) or value
            bitsLeft += 5

            if (bitsLeft >= 8) {
                decoded[index++] = (buffer shr (bitsLeft - 8)).toByte()
                bitsLeft -= 8
            }
        }

        return decoded
    }


    companion object {

        /**
         * Alphabet used for Base32 encoding.
         */
        private const val CROCKFORD_ALPHABET = "0123456789ABCDEFGHJKMNPQRSTVWXYZ"


        /**
         * Lookup table used for Base32 decoding.
         */
        private val CROCKFORD_LOOKUP = IntArray(128) { -1 }.apply {
            for (i: Int in CROCKFORD_ALPHABET.indices) {
                this[CROCKFORD_ALPHABET[i].code] = i
            }

            this['O'.code] = 0
            this['o'.code] = 0
            this['I'.code] = 1
            this['i'.code] = 1
            this['L'.code] = 1
            this['l'.code] = 1
        }

    }

}
