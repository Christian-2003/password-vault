package de.passwordvault.viewmodel.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import androidx.lifecycle.ViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Objects;
import de.passwordvault.R;
import de.passwordvault.model.security.login.Account;
import de.passwordvault.view.dialogs.ConfigureLoginDialog;
import de.passwordvault.view.utils.DialogArgumentException;
import de.passwordvault.view.utils.DialogCallbackListener;


/**
 * Class implements a {@linkplain ViewModel} for the {@link ConfigureLoginDialog}.
 *
 * @author  Christian-2003
 * @version 3.2.0
 */
public class ConfigureLoginViewModel extends ViewModel {

    /**
     * Attribute stores the {@link DialogCallbackListener} for the {@link ConfigureLoginDialog}.
     */
    private DialogCallbackListener callbackListener;


    /**
     * Method returns the {@link #callbackListener} of the dialog.
     *
     * @return  Callback listener.
     */
    public DialogCallbackListener getCallbackListener() {
        return callbackListener;
    }

    /**
     * Method changes the {@link #callbackListener} of the dialog.
     *
     * @param callbackListener      New callback listener for the dialog.
     * @throws NullPointerException The passed argument is {@code null}.
     */
    public void setCallbackListener(DialogCallbackListener callbackListener) throws NullPointerException {
        if (callbackListener == null) {
            throw new NullPointerException("Null is invalid CallbackListener");
        }
        this.callbackListener = callbackListener;
    }


    /**
     * Method processes and validates the user input. If some input is incorrect, the visuals of
     * the passed {@linkplain View} are changed to inform the user about the incorrect input. In this
     * case, {@code false} is returned. If the input is correct, {@code true} is returned.
     *
     * @param view  View from which the input shall be retrieved.
     * @return      Whether the user input is correct.
     */
    public boolean processUserInput(View view) {
        if (view == null) {
            return false;
        }
        Context context = view.getContext();
        boolean inputCorrect = true;

        //Test whether passwords were entered correctly:
        TextInputEditText passwordEditText = view.findViewById(R.id.dialog_configure_login_password);
        TextInputLayout passwordLayout = view.findViewById(R.id.dialog_configure_login_password_hint);
        String password = Objects.requireNonNull(passwordEditText.getText()).toString();
        TextInputEditText confirmEditText = view.findViewById(R.id.dialog_configure_login_confirm);
        TextInputLayout confirmLayout = view.findViewById(R.id.dialog_configure_login_confirm_hint);
        String confirm = Objects.requireNonNull(confirmEditText.getText()).toString();
        if (password.isEmpty()) {
            passwordLayout.setError(context.getString(R.string.error_empty_input));
            inputCorrect = false;
        }
        else {
            passwordLayout.setErrorEnabled(false);
        }
        if (confirm.isEmpty()) {
            confirmLayout.setError(context.getString(R.string.error_empty_input));
            inputCorrect = false;
        }
        else {
            confirmLayout.setErrorEnabled(false);
        }
        if (inputCorrect && !password.equals(confirm)) {
            confirmLayout.setError(context.getString(R.string.error_passwords_not_matching));
            inputCorrect = false;
        }

        if (!inputCorrect) {
            return false;
        }

        Account.getInstance().setPassword(password);

        return true;
    }


    /**
     * Method processes the arguments that were passed to the {@link ConfigureLoginDialog}.
     *
     * @param args                      Passed arguments to be processed.
     * @throws NullPointerException     Some arguments are {@code null}.
     * @throws ClassCastException       The {@linkplain java.io.Serializable} cannot be casted to
     *                                  {@link DialogCallbackListener}.
     * @throws DialogArgumentException  Some arguments are missing.
     */
    public void processArguments(Bundle args) throws NullPointerException, ClassCastException, DialogArgumentException {
        if (args == null) {
            throw new NullPointerException("Null is invalid bundle");
        }

        //Process KEY_CALLBACK_LISTENER:
        if (args.containsKey(ConfigureLoginDialog.KEY_CALLBACK_LISTENER)) {
            try {
                setCallbackListener((DialogCallbackListener) args.getSerializable(ConfigureLoginDialog.KEY_CALLBACK_LISTENER));
            }
            catch (ClassCastException e) {
                e.printStackTrace();
                throw e;
            }
        }
        else {
            throw new DialogArgumentException("Missing argument KEY_CALLBACK_LISTENER");
        }
    }

}
