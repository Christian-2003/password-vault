package de.christian2003.security.domain.exceptions


/**
 * Exception can be thrown by the service to unlock the master key if the provided input (e.g.
 * master password or recovery codes) are invalid.
 *
 * @param message   Optional message.
 */
class UnlockSourceInvalidException(
    message: String? = null
): Exception(message)
