package de.christian2003.core.security.domain.exceptions


/**
 * Exception that is thrown if the master key cannot be unlocked.
 *
 * @param message   Optional message.
 */
class UnlockFailedException(
    message: String? = null
): Exception(message)
