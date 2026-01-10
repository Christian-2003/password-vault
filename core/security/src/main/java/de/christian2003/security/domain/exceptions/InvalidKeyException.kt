package de.christian2003.security.domain.exceptions


/**
 * Exception that can be thrown by a cipher service implementation using AEAD mode (such as GCM)
 * where the authentication tag does not match.
 *
 * @param message   Optional message.
 */
class InvalidKeyException(
    message: String = ""
): Exception(message)
