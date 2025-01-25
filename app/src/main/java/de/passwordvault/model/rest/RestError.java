package de.passwordvault.model.rest;


/**
 * Enum contains all available errors that can be generated when fetching data from a REST API.
 *
 * @author  Christian-2003
 * @version 3.7.2
 */
public enum RestError {

    /**
     * Field indicates success, meaning that no error occurred.
     */
    SUCCESS,

    /**
     * Field indicates that a connection to the internet cannot be established.
     */
    ERROR_INTERNET,

    /**
     * Field indicates that the REST server cannot be reached.
     */
    ERROR_404,

    /**
     * Field indicates that the response returned from the REST API cannot be deserialized.
     */
    ERROR_SERIALIZATION

}
