package de.passwordvault.model.security.authentication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * Interface must be implemented by all views that require the user to authenticate through their
 * master password (or biometrics).
 *
 * @author  Christian-2003
 * @version 3.5.1
 */
public interface AuthenticationCallback {

    /**
     * Method is called when the authentication succeeds.
     *
     * @param tag   Tag that was passed with the authentication request.
     */
    void onAuthenticationSuccess(@Nullable String tag);


    /**
     * Method is called when the authentication fails.
     *
     * @param tag   Tag that was passed with the authentication request.
     * @param code  Authentication failure code indicating why the authentication failed.
     */
    void onAuthenticationFailure(@Nullable String tag, @NonNull AuthenticationFailure code);

}
