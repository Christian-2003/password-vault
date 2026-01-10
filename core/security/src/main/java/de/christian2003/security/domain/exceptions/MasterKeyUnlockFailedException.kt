package de.christian2003.security.domain.exceptions


/**
 * Exception that is thrown if the master key cannot be unlocked.
 *
 * @param message   Optional message.
 */
class MasterKeyUnlockFailedException(
    message: String = ""
): Exception(message)
