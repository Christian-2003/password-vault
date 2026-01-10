package de.christian2003.security.domain.entities


/**
 * Enum models the key encryption key (KEK) entries that can be retrieved through the KekRepository.
 *
 * @property MasterPassword KEK entry for the master password.
 * @property RecoveryCodes  KEK entry for the recovery codes.
 */
enum class KekEntry {

    MasterPassword,

    RecoveryCodes

}
