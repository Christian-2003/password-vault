package de.passwordvault.model.storage.app;


/**
 * Class models an {@linkplain Exception} which can be thrown when some error regarding data
 * conversion happens.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public class ConverterException extends Exception {

    /**
     * Constructor instantiates a new ConverterException without any message.
     */
    public ConverterException() {
        super();
    }

    /**
     * Constructor instantiates a new ConverterException with the passed message.
     *
     * @param message   Message to be delivered alongside the exception.
     */
    public ConverterException(String message) {
        super(message);
    }

}
