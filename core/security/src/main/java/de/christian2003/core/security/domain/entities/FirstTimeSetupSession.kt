package de.christian2003.core.security.domain.entities


/**
 * Value object contains the session data for the first-time app setup.
 *
 * @param masterPassword    Master password entered by the user.
 * @param recoveryCodes     List of generated recovery codes.
 * @param useBiometrics     Whether to use biometrics.
 */
data class FirstTimeSetupSession(
    val masterPassword: CharArray,
    val recoveryCodes: List<CharArray>,
    val useBiometrics: Boolean
) {

    /**
     * Initializes a new session for the first-time setup.
     */
    init {
        require(masterPassword.isNotEmpty()) { "Master password cannot be empty" }
        require(recoveryCodes.isNotEmpty()) { "Recovery codes cannot be empty" }
        recoveryCodes.forEach { recoveryCode ->
            require(recoveryCode.isNotEmpty()) { "Recovery code cannot be empty" }
        }
    }


    /**
     * Clears sensitive session data.
     */
    fun clear() {
        masterPassword.fill('\u0000')
        recoveryCodes.forEach { code ->
            code.fill('\u0000')
        }
    }


    //Auto-generated
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FirstTimeSetupSession

        if (useBiometrics != other.useBiometrics) return false
        if (!masterPassword.contentEquals(other.masterPassword)) return false
        if (recoveryCodes != other.recoveryCodes) return false

        return true
    }


    //Auto-generated
    override fun hashCode(): Int {
        var result = useBiometrics.hashCode()
        result = 31 * result + masterPassword.contentHashCode()
        result = 31 * result + recoveryCodes.hashCode()
        return result
    }

}
