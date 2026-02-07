package de.christian2003.core.security.domain.exceptions


/**
 * Exception is thrown by the AuthTransactionRepository when some error regarding a transaction
 * occurs.
 *
 * @param message   Optional message.
 */
class AuthTransactionException(
    message: String? = null
): Exception(message)
