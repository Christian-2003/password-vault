package de.passwordvault.backend.security.encryption;


/**
 * Class models an {@linkplain Exception} that can be thrown whenever something goes wrong while
 * encrypting / decryption data.
 *
 * @author  Christian-2003
 * @version 2.0.0
 */
public class EncryptionException extends Exception {

    /**
     * Constructor instantiates a new EncryptionException with the specified message.
     *
     * @param message   Message to be delivered alongside the exception.
     */
    public EncryptionException(String message) {
        super(message);
    }

}
