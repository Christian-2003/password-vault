package de.passwordvault.model.storage.export;


/**
 * Class models an {@link Exception} which can be thrown whenever something goes wrong while
 * exporting the data.
 *
 * @author  Christian-2003
 * @version 3.1.0
 */
public class ExportException extends Exception {

    /**
     * Constructor instantiates a new {@link Exception} which can be thrown when something goes
     * wrong while exporting the data.
     *
     * @param message   Error message.
     */
    public ExportException(String message) {
        super(message);
    }

}
