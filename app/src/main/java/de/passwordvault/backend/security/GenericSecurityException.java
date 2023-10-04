package de.passwordvault.backend.security;


/**
 * Class models an {@linkplain Exception} which can be thrown on security related errors.
 *
 * @author  Christian-2003
 * @version 2.0.0
 */
public class GenericSecurityException extends Exception {

    /**
     * Constructor instantiates a new {@link GenericSecurityException} that can be thrown whenever
     * security exceptions occur.
     *
     * @param message   Message to be delivered with the exception.
     */
    public GenericSecurityException(String message) {
        super(message);
    }

}
