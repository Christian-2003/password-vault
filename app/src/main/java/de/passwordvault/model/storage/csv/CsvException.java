package de.passwordvault.model.storage.csv;


/**
 * Class models an {@linkplain Exception} which can be thrown when something regarding CSV
 * goes wrong.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public class CsvException extends Exception {

    /**
     * Constructor instantiates a new CsvException with no message.
     */
    public CsvException() {
        super();
    }

    /**
     * Constructor instantiates a new CsvException with the passed message.
     *
     * @param message   Message to be delivered with the exception.
     */
    public CsvException(String message) {
        super(message);
    }

}
