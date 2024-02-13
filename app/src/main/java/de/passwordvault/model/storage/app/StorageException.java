package de.passwordvault.model.storage.app;


/**
 * Class models an {@linkplain Exception} which can be thrown when some error regarding data
 * storage occurs.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public class StorageException extends Exception {

    /**
     * Constructor instantiates a new StorageException without any message.
     */
    public StorageException() {
        super();
    }

    /**
     * Constructor instantiates a new StorageException with the passed message.
     *
     * @param message   Message to be delivered alongside the exception.
     */
    public StorageException(String message) {
        super(message);
    }

}
