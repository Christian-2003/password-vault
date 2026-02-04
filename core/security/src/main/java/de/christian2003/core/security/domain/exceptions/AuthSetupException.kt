package de.christian2003.core.security.domain.exceptions

import java.lang.Exception


/**
 * Exception is thrown when the setup of the recovery fails.
 *
 * @param message   Optional message.
 */
class AuthSetupException(
    message: String? = null
): Exception(message)
