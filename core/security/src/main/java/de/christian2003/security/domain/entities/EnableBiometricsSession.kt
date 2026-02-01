package de.christian2003.security.domain.entities


/**
 * Value object stores the session data to enable the biometric authentication.
 *
 * @param masterPassword    Master password.
 */
data class EnableBiometricsSession(
    val masterPassword: CharArray
) {

    /**
     * Initializes a new session to enable biometrics.
     */
    init {
        require(masterPassword.isNotEmpty()) { "Master password cannot be empty" }
    }


    /**
     * Clears sensitive data from the session.
     */
    fun clear() {
        masterPassword.fill('\u0000')
    }


    //Auto-generated
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EnableBiometricsSession

        if (!masterPassword.contentEquals(other.masterPassword)) return false

        return true
    }

    //Auto-generated
    override fun hashCode(): Int {
        return masterPassword.contentHashCode()
    }

}
