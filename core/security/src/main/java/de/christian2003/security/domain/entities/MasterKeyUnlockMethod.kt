package de.christian2003.security.domain.entities


/**
 * Enum describes the input source from which the master key is being unlocked.
 *
 * @property MasterPassword The input used for unlocking is the master password.
 * @property RecoveryCodes  The input used for unlocking are the recovery codes.
 */
enum class MasterKeyUnlockMethod {

    MasterPassword,

    RecoveryCodes

}
