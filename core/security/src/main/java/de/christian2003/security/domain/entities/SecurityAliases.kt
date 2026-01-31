package de.christian2003.security.domain.entities


/**
 * Enum stores all aliases that are required by the :core:security-module for storing keys, salts, ...
 *
 * @param securityAlias Alias with which the key, salt, etc. is stored
 */
enum class SecurityAliases(
    private val securityAlias: String
) {

    //Global keys:
    HardwareBackedKey("hardware_backed_key"),
    MasterKey("master_key"),

    //Master password:
    MasterPasswordKek("master_password_kek"),
    MasterPasswordSalt("master_password_salt"),

    //Recovery codes:
    RecoveryCodeKek("recovery_%1\$d_kek"),
    RecoveryCodeSalt("recovery_%1\$d_salt"),
    NumberOfRecoveryCodes("recovery_count"),

    //Biometrics:
    BiometricsHardwareBackedKey("hardware_backed_key_biometrics"),
    BiometricsKek("biometrics_kek");


    /**
     * Returns the alias for the specified key.
     *
     * @param index Optional index used within the retrieved alias.
     */
    fun getAlias(index: Int? = null): String {
        return if (index == null) {
            securityAlias
        } else {
            String.format(securityAlias, index)
        }
    }

}
