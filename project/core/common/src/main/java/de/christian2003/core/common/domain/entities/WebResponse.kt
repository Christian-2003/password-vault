package de.christian2003.core.common.domain.entities


/**
 * Value object models a response from a web request.
 *
 * @param content       Content of the web response. May be null if response does not contain a body.
 * @param statusCode    HTTP response status code (e.g. 200, 404, 405, 500, ...).
 */
data class WebResponse(
    val content: String?,
    val statusCode: Int
) {

    /**
     * Determines whether the response is successful.
     * Checks whether the status code is in the range [200, 299]
     */
    val isSuccessful: Boolean
        get() = statusCode in 200..299

}
