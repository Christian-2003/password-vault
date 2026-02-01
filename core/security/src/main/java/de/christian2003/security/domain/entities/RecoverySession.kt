package de.christian2003.security.domain.entities


/**
 * Value object contains the session data for the recovery of the master password.
 *
 * @param recoveryCode      Existing recovery code entered in order to recover the master password.
 * @param newMasterPassword New master password to use after recovery finishes.
 * @param newRecoveryCodes  List of new recovery codes to use after this recovery finishes.
 */
data class RecoverySession(
    val recoveryCode: CharArray,
    val newMasterPassword: CharArray,
    val newRecoveryCodes: List<CharArray>
) {

    /**
     * Initializes a new session for recovery.
     */
    init {
        require (recoveryCode.isNotEmpty()) { "Recovery code cannot be empty" }
        require(newMasterPassword.isNotEmpty()) { "New master password cannot be empty" }
        require(newRecoveryCodes.isNotEmpty()) { "New recovery codes cannot be empty" }
        newRecoveryCodes.forEach { recoveryCode ->
            require(recoveryCode.isNotEmpty()) { "New recovery code cannot be empty" }
        }
    }


    /**
     * Clears sensitive data from the session.
     */
    fun clear() {
        recoveryCode.fill('\u0000')
        newMasterPassword.fill('\u0000')
        newRecoveryCodes.forEach { recoveryCode ->
            recoveryCode.fill('\u0000')
        }
    }


    //Auto-generated
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RecoverySession

        if (!recoveryCode.contentEquals(other.recoveryCode)) return false
        if (!newMasterPassword.contentEquals(other.newMasterPassword)) return false
        if (newRecoveryCodes.size != other.newRecoveryCodes.size) return false
        if (!newRecoveryCodes.zip(other.newRecoveryCodes).all { (a,b) -> a.contentEquals(b) }) return false

        return true
    }


    //Auto-generated
    override fun hashCode(): Int {
        var result = recoveryCode.contentHashCode()
        result = 31 * result + newMasterPassword.contentHashCode()
        result = 31 * result + newRecoveryCodes.fold(1) { acc, code -> 31 * acc + code.contentHashCode() }
        return result
    }

}
