package de.christian2003.security.domain.entities


/**
 * Value object stores the result for the key wrapper service contains the bytes of the wrapped key,
 * as well as the IV used.
 *
 * @param wrappedKeyBytes   Bytes of the wrapped key.
 * @param iv                Initialization vector used.
 */
data class KeyWrapperServiceResult(
    val wrappedKeyBytes: ByteArray,
    val iv: ByteArray
) {

    //Auto-generated
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KeyWrapperServiceResult

        if (!wrappedKeyBytes.contentEquals(other.wrappedKeyBytes)) return false
        if (!iv.contentEquals(other.iv)) return false

        return true
    }


    //Auto-generated
    override fun hashCode(): Int {
        var result = wrappedKeyBytes.contentHashCode()
        result = 31 * result + iv.contentHashCode()
        return result
    }

}
