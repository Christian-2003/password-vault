package de.christian2003.security.application.usecases.dto


/**
 * DTO for the SetupNewRecoveryCodesUseCase which returns the result for a single recovery code that
 * was freshly generated.
 *
 * @param index             Index of the recovery code.
 * @param recoveryCodeBytes Bytes of the recovery code.
 * @param salt              Salt.
 * @param encryptedKek      KEK encrypted with the generated recovery code.
 */
data class RecoveryCodeGeneratorResultDto(
    val index: Int,
    val recoveryCodeBytes: ByteArray,
    val salt: ByteArray,
    val encryptedKek: ByteArray
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RecoveryCodeGeneratorResultDto

        if (index != other.index) return false
        if (!recoveryCodeBytes.contentEquals(other.recoveryCodeBytes)) return false
        if (!salt.contentEquals(other.salt)) return false
        if (!encryptedKek.contentEquals(other.encryptedKek)) return false

        return true
    }


    override fun hashCode(): Int {
        var result = index
        result = 31 * result + recoveryCodeBytes.contentHashCode()
        result = 31 * result + salt.contentHashCode()
        result = 31 * result + encryptedKek.contentHashCode()
        return result
    }

}
