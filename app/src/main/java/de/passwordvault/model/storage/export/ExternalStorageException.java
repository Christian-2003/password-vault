package de.passwordvault.model.storage.export;


/**
 * Class models an {@linkplain Exception} that can be thrown whenever something goes wrong with
 * external file storage.
 *
 * @author  Christian-2003
 * @version 2.2.2
 */
public class ExternalStorageException extends Exception {

    /**
     * Constructor instantiates a new ExternalStorageException with the specified message.
     *
     * @param message   Message to be delivered alongside the exception.
     */
    public ExternalStorageException(String message) {
        super(message);
    }

}
