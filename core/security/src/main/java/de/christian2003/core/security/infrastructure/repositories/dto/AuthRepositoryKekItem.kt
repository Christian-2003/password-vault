package de.christian2003.core.security.infrastructure.repositories.dto


/**
 * Stores a single KEK as well as the corresponding salt.
 *
 * @param keyBytes  Bytes of the KEK.
 * @param salt      Bytes of the salt.
 */
internal data class AuthRepositoryKekItem(
    val keyBytes: ByteArray,
    val salt: ByteArray
) {

    //Auto-generated
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AuthRepositoryKekItem

        if (!keyBytes.contentEquals(other.keyBytes)) return false
        if (!salt.contentEquals(other.salt)) return false

        return true
    }


    //Auto-generated
    override fun hashCode(): Int {
        var result = keyBytes.contentHashCode()
        result = 31 * result + salt.contentHashCode()
        return result
    }

}
