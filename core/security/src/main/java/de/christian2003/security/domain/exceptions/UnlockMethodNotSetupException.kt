package de.christian2003.security.domain.exceptions


/**
 * Exception can be thrown by the service to unlock the master key if the unlock method has not been
 * setup.
 *
 * @param message   Optional message.
 */
class UnlockMethodNotSetupException(
    message: String? = null
): Exception(message)
