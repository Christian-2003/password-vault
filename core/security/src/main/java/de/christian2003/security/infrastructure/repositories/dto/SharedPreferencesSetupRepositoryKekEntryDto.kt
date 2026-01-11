package de.christian2003.security.infrastructure.repositories.dto


/**
 * DTO models an entry of a KEK in combination with a salt for the auth setup repository.
 *
 * @param encryptedKekBytes Bytes of the encrypted KEK.
 * @param salt              Salt used to derive the key with which the KEK was encrypted.
 */
data class SharedPreferencesSetupRepositoryKekEntryDto(
    val encryptedKekBytes: ByteArray,
    val salt: ByteArray
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SharedPreferencesSetupRepositoryKekEntryDto

        if (!encryptedKekBytes.contentEquals(other.encryptedKekBytes)) return false
        if (!salt.contentEquals(other.salt)) return false

        return true
    }


    override fun hashCode(): Int {
        var result = encryptedKekBytes.contentHashCode()
        result = 31 * result + salt.contentHashCode()
        return result
    }

}
