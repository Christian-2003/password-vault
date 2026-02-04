package de.christian2003.core.security.domain.exceptions


/**
 * Exception that can be thrown by crypto services.
 *
 * @param message   Optional message.
 */
class CryptographicException(
    message: String? = null
): Exception(message)
