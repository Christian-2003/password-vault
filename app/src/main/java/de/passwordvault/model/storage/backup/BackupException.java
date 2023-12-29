package de.passwordvault.model.storage.backup;


/**
 * Class models an {@link Exception} which can be thrown whenever something goes wrong while
 * creating a backup to the user's Android device.
 *
 * @author  Christian-2003
 * @version 3.1.0
 */
public class BackupException extends Exception {

    /**
     * Constructor instantiates a new {@link Exception} which can be thrown when something goes
     * wrong while creating a backup to the device.
     *
     * @param message   Error message.
     */
    public BackupException(String message) {
        super(message);
    }

}
