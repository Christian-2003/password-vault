package de.christian2003.security.domain.entities


/**
 * Value object stores the session data for the generation of new recovery codes.
 */
data class GenerateNewRecoveryCodesSession(
    val masterPassword: CharArray,
    val recoveryCodes: List<CharArray>
) {

    /**
     * Instantiates a new session for the generation of new recovery codes.
     */
    init {
        require(masterPassword.isNotEmpty()) { "Master password cannot be empty" }
        require(recoveryCodes.isNotEmpty()) { "Recovery codes cannot be empty" }
        recoveryCodes.forEach { recoveryCode ->
            require(recoveryCode.isNotEmpty()) { "Recovery code cannot be empty" }
        }
    }


    /**
     * Clears sensitive data from the session.
     */
    fun clear() {
        masterPassword.fill('\u0000')
        recoveryCodes.forEach { recoveryCode ->
            recoveryCode.fill('\u0000')
        }
    }


    //Auto-generated
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GenerateNewRecoveryCodesSession

        if (!masterPassword.contentEquals(other.masterPassword)) return false
        if (recoveryCodes.size != other.recoveryCodes.size) return false
        if (!recoveryCodes.zip(other.recoveryCodes).all { (a, b) -> a.contentEquals(b) }) return false

        return true
    }


    //Auto-generated
    override fun hashCode(): Int {
        var result = masterPassword.contentHashCode()
        result = 31 * result + recoveryCodes.fold(1) { acc, code ->
            31 * acc + code.contentHashCode()
        }
        return result
    }

}
