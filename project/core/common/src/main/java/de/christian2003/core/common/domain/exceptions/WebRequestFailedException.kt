package de.christian2003.core.common.domain.exceptions

import java.io.IOException


/**
 * Exception that can be thrown if a web request fails. This is a generic exception which does not
 * indicate any further status.
 *
 * @param message   Message for the exception.
 */
class WebRequestFailedException (
    message: String
): IOException(message) {

}
