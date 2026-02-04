package de.christian2003.core.security.domain.entities


/**
 * Value object contains the session data to change the master password.
 *
 * @param currentMasterPassword Current master password.
 * @param newMasterPassword     New master password.
 */
data class ChangePasswordSession(
    val currentMasterPassword: CharArray,
    val newMasterPassword: CharArray
) {

    /**
     * Initializes a new session for changing the master password.
     */
    init {
        require(currentMasterPassword.isNotEmpty()) { "Current master password cannot be empty" }
        require(newMasterPassword.isNotEmpty()) { "New master password cannot be empty" }
    }


    /**
     * Clears sensitive data from the session.
     */
    fun clear() {
        currentMasterPassword.fill('\u0000')
        newMasterPassword.fill('\u0000')
    }


    //Auto-generated
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChangePasswordSession

        if (!currentMasterPassword.contentEquals(other.currentMasterPassword)) return false
        if (!newMasterPassword.contentEquals(other.newMasterPassword)) return false

        return true
    }


    //Auto-generated
    override fun hashCode(): Int {
        var result = currentMasterPassword.contentHashCode()
        result = 31 * result + newMasterPassword.contentHashCode()
        return result
    }

}
