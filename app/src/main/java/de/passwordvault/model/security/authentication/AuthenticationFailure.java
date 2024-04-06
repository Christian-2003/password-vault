package de.passwordvault.model.security.authentication;


/**
 * Enum stores different reasons for which an
 * {@link AuthenticationCallback#onAuthenticationFailure(String, AuthenticationFailure)}
 * is called.
 *
 * @author  Christian-2003
 * @version 3.5.1
 */
public enum AuthenticationFailure {

    /**
     * Case indicates that the authentication was cancelled by the user.
     */
    CANCELLED,

    /**
     * Case indicates that the authentication failed. This can occur when the user enters a wrong
     * password multiple times or if biometric authentication fails.
     */
    AUTH_FAIL;

}
