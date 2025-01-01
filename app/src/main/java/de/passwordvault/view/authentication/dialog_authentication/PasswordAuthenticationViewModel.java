package de.passwordvault.view.authentication.dialog_authentication;

import android.os.Bundle;
import android.view.View;
import androidx.lifecycle.ViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Objects;
import de.passwordvault.R;
import de.passwordvault.model.security.login.Account;
import de.passwordvault.view.utils.DialogCallbackListener;


/**
 * Class implements the view model for the dialog through which the user can authenticate using
 * the master password.
 *
 * @author  Christian-2003
 * @version 3.5.1
 */
public class PasswordAuthenticationViewModel extends ViewModel {

    /**
     * Attribute stores the ID of the resource-string containing the title for the dialog.
     */
    private int titleId;

    /**
     * Attribute indicates whether the dialog shall be used to register a new password.
     */
    private boolean registering;


    /**
     * Method returns the ID of the resource-string containing the title for the dialog.
     *
     * @return  ID of the title for the dialog.
     */
    public int getTitleId() {
        return titleId;
    }

    /**
     * Method returns whether the dialog shall be used to register a new master password (= {@code true})
     * or confirm an already entered password (= {@code false}).
     *
     * @return  Whether the dialog shall be used to register a new password.
     */
    public boolean isRegistering() {
        return registering;
    }


    /**
     * Method processes the arguments that were passed to the dialog.
     *
     * @param args  Password arguments to process.
     * @return      Whether the arguments were processed successfully.
     */
    public boolean processArguments(Bundle args) {
        if (args == null) {
            return false;
        }

        if (args.containsKey(PasswordAuthenticationDialog.ARG_TITLE_ID)) {
            titleId = args.getInt(PasswordAuthenticationDialog.ARG_TITLE_ID);
        }
        else {
            return false;
        }

        if (args.containsKey(PasswordAuthenticationDialog.ARG_REGISTER)) {
            registering = args.getBoolean(PasswordAuthenticationDialog.ARG_REGISTER);
        }
        else {
            return false;
        }

        return true;
    }


    /**
     * Method processes the input of the user from the passed view.
     *
     * @param view  Inflated view containing the user input.
     * @return      Whether the input was correct or incorrect.
     */
    public boolean processUserInput(View view) {
        if (view == null) {
            return false;
        }

        TextInputLayout passwordLayout = view.findViewById(R.id.dialog_password_authentication_input_container);
        TextInputEditText passwordEditText = view.findViewById(R.id.dialog_password_authentication_input);
        String password = Objects.requireNonNull(passwordEditText.getText()).toString();

        if (registering) {
            //Dialog used to register a new password:
            TextInputLayout confirmLayout = view.findViewById(R.id.dialog_password_authentication_confirm_container);
            TextInputEditText confirmEditText = view.findViewById(R.id.dialog_password_authentication_confirm);
            String confirm = Objects.requireNonNull(confirmEditText.getText()).toString();

            boolean inputCorrect = true;

            if (password.isEmpty()) {
                passwordLayout.setError(view.getContext().getString(R.string.error_empty_input));
                inputCorrect = false;
            }
            else {
                passwordLayout.setErrorEnabled(false);
            }
            if (confirm.isEmpty()) {
                confirmLayout.setError(view.getContext().getString(R.string.error_empty_input));
                inputCorrect = false;
            }
            else {
                confirmLayout.setErrorEnabled(false);
            }
            if (!inputCorrect) {
                return false;
            }

            if (!password.equals(confirm)) {
                confirmLayout.setError(view.getContext().getString(R.string.error_passwords_not_matching));
                return false;
            }
            else {
                Account.getInstance().setPassword(password);
                Account.getInstance().save();
                return true;
            }
        }
        else {
            //Dialog used to confirm a password:
            if (password.isEmpty()) {
                passwordLayout.setError(view.getContext().getString(R.string.error_empty_input));
                return false;
            }
            if (!Account.getInstance().isPassword(password)) {
                passwordLayout.setError(view.getContext().getString(R.string.error_passwords_incorrect));
                return false;
            }
            else {
                return true;
            }
        }
    }

}
