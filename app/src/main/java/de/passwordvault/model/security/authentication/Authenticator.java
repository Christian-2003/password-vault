package de.passwordvault.model.security.authentication;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import java.io.Serializable;
import java.util.concurrent.Executor;
import de.passwordvault.R;
import de.passwordvault.model.security.login.Account;
import de.passwordvault.view.dialogs.PasswordAuthenticationDialog;
import de.passwordvault.view.utils.DialogCallbackListener;


/**
 * Class models an authenticator that can be used to authenticate the user either through the master
 * password or through biometrics.
 * Authenticate by calling {@link #authenticate(FragmentActivity)}.
 *
 * @author  Christian-2003
 * @version 3.5.1
 */
public class Authenticator {

    /**
     * Class implements the authentication callback for biometric authentication. This class handles
     * all callbacks of the authenticator.
     */
    private class BiometricAuthenticationCallback extends BiometricPrompt.AuthenticationCallback {

        /**
         * Method is called whenever an authentication error occurs.
         *
         * @param errorCode An integer ID associated with the error.
         * @param errString A human-readable string that describes the error.
         */
        @Override
        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
            if (errorCode == 13) {
                Authenticator.this.callback.onAuthenticationFailure(Authenticator.this.tag, AuthenticationFailure.CANCELLED);
            }
            else {
                Authenticator.this.callback.onAuthenticationFailure(Authenticator.this.tag, AuthenticationFailure.AUTH_FAIL);
            }
        }

        /**
         * Method is called whenever the authentication succeeds.
         *
         * @param result    An object containing authentication-related data.
         */
        @Override
        public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            Authenticator.this.callback.onAuthenticationSuccess(Authenticator.this.tag);
        }

        /**
         * Method is called whenever the authentication fails.
         */
        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            Authenticator.this.callback.onAuthenticationFailure(Authenticator.this.tag, AuthenticationFailure.AUTH_FAIL);
        }

    }


    /**
     * Class implements the callback for the password authentication dialog. This class handles all
     * callbacks of the authenticator.
     */
    private class PasswordAuthenticationCallback implements DialogCallbackListener, Serializable {

        /**
         * Method is called whenever the password authentication succeeded.
         *
         * @param dialog    Dialog which called the method.
         */
        @Override
        public void onPositiveCallback(DialogFragment dialog) {
            Authenticator.this.callback.onAuthenticationSuccess(Authenticator.this.tag);
        }

        /**
         * Method is called whenever the password authentication failed. This means that the user
         * cancelled the authentication.
         *
         * @param dialog    Dialog which called the method.
         */
        @Override
        public void onNegativeCallback(DialogFragment dialog) {
            Authenticator.this.callback.onAuthenticationFailure(Authenticator.this.tag, AuthenticationFailure.CANCELLED);
        }

    }


    /**
     * Field indicates that the authenticator shall authenticate with the password.
     */
    public static final int AUTH_PASSWORD = 0;

    /**
     * Field indicates that the authenticator shall authenticate with biometrics.
     */
    public static final int AUTH_BIOMETRICS = 1;

    /**
     * Field indicates that the authenticator shall decide whether to use biometric or password
     * authentication.
     * This option is selected by default.
     */
    public static final int AUTH_AUTO = 2;

    /**
     * Field indicates that the authenticator shall be used to let the user 'register'. This means
     * that (when authentication through a password), the user needs to enter the password twice.
     */
    public static final int TYPE_REGISTER = 0;

    /**
     * Field indicates that the authenticator shall be used to confirm the users identity. Use this
     * if the user already authenticated before.
     * This option is selected by default.
     */
    public static final int TYPE_AUTHENTICATE = 1;


    /**
     * Attribute stores the context for the authenticator.
     */
    private FragmentActivity context;

    /**
     * Attribute stores the callback for the authenticator.
     */
    private final AuthenticationCallback callback;

    /**
     * Attribute stores the tag for the authenticator. This tag is passed on callback.
     */
    private final String tag;

    /**
     * Attribute stores the ID of the string-resource which stores the title to be displayed to the
     * user.
     */
    private final int titleId;


    /**
     * Constructor instantiates a new authenticator with the passed callback, tag and titleID.
     *
     * @param callback              Callback for when authentication finishes.
     * @param tag                   Tag for the authenticator is passed on callback.
     * @param titleId               ID of the string-resource storing the title for the authenticator.
     *                              The title is displayed to the user.
     * @throws NullPointerException The callback is {@code null}.
     */
    public Authenticator(AuthenticationCallback callback, String tag, int titleId) throws NullPointerException {
        if (callback == null) {
            throw new NullPointerException();
        }
        this.callback = callback;
        this.tag = tag;
        this.titleId = titleId;
    }


    /**
     * Method authenticates the user. The authenticator automatically decides whether to use password
     * or biometric authentication. Furthermore, the authenticator does not 'register' the user.<br/>
     * Calling this method is identical to calling {@link #authenticate(FragmentActivity, int, int)}
     * with {@link #AUTH_AUTO} and {@link #TYPE_AUTHENTICATE} arguments.
     *
     * @param context               Context for the authenticator.
     * @throws NullPointerException The passed context is {@code null}.
     */
    public void authenticate(FragmentActivity context) throws NullPointerException {
        authenticate(context, AUTH_AUTO, TYPE_AUTHENTICATE);
    }

    /**
     * Method authenticates the user based on the passed options.
     *
     * @param context               Context for the authenticator.
     * @param authOption            Authentication option indicates whether to use password or biometric
     *                              authentication. This is one of the following: {@link #AUTH_PASSWORD},
     *                              {@link #AUTH_BIOMETRICS} or {@link #AUTH_AUTO}.
     * @param typeOption            Type option indicates whether to 'register' or 'authenticate' the
     *                              user. This is one of the following: {@link #TYPE_AUTHENTICATE}
     *                              or {@link #TYPE_REGISTER}.
     * @throws NullPointerException The passed context is {@code null}.
     */
    public void authenticate(FragmentActivity context, int authOption, int typeOption) throws NullPointerException {
        if (context == null) {
            throw new NullPointerException();
        }
        this.context = context;

        switch (authOption) {
            case AUTH_PASSWORD:
                passwordAuthentication(typeOption);
                break;
            case AUTH_BIOMETRICS:
                biometricAuthentication();
                break;
            default:
                if (Account.getInstance().useBiometrics()) {
                    biometricAuthentication();
                }
                else {
                    passwordAuthentication(typeOption);
                }
                break;
        }
    }


    /**
     * Method starts the biometric authentication.
     */
    private void biometricAuthentication() {
        BiometricPrompt.PromptInfo.Builder biometricPromptInfoBuilder = new BiometricPrompt.PromptInfo.Builder();
        biometricPromptInfoBuilder.setTitle(context.getString(titleId));
        biometricPromptInfoBuilder.setNegativeButtonText(context.getString(R.string.button_cancel));
        BiometricPrompt.PromptInfo biometricPromptInfo = biometricPromptInfoBuilder.build();

        Executor executor = ContextCompat.getMainExecutor(context);

        BiometricPrompt biometricPrompt = new BiometricPrompt(context, executor, new BiometricAuthenticationCallback());
        biometricPrompt.authenticate(biometricPromptInfo);
    }


    /**
     * Method starts the password authentication.
     *
     * @param typeOption    Option indicates whether the password authentication shall be used to
     *                      'register' or 'authenticate' the user.
     */
    private void passwordAuthentication(int typeOption) {
        PasswordAuthenticationCallback passwordAuthenticationCallback = new PasswordAuthenticationCallback();
        Bundle args = new Bundle();
        args.putSerializable(PasswordAuthenticationDialog.KEY_CALLBACK, passwordAuthenticationCallback);
        args.putInt(PasswordAuthenticationDialog.KEY_TITLE_ID, titleId);
        args.putBoolean(PasswordAuthenticationDialog.KEY_REGISTER, typeOption == TYPE_REGISTER);

        PasswordAuthenticationDialog dialog = new PasswordAuthenticationDialog();
        dialog.setArguments(args);
        dialog.show(context.getSupportFragmentManager(), "");
    }

}
