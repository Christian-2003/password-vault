package de.passwordvault.viewmodel.dialogs;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.lifecycle.ViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Objects;
import de.passwordvault.R;
import de.passwordvault.model.security.login.Account;
import de.passwordvault.view.dialogs.EnterPasswordDialog;
import de.passwordvault.view.utils.DialogArgumentException;
import de.passwordvault.view.utils.DialogCallbackListener;


/**
 * Class implements a {@linkplain ViewModel} for the {@link EnterPasswordDialog}.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public class EnterPasswordViewModel extends ViewModel {

    /**
     * Attribute stores the callback listener for the dialog.
     */
    private DialogCallbackListener callbackListener;

    /**
     * Attribute stores the title of the dialog.
     */
    private String title;

    /**
     * Attribute stores the optional info label of the dialog.
     */
    private String info;


    /**
     * Method returns the callback listener of the dialog.
     *
     * @return  Callback listener.
     */
    public DialogCallbackListener getCallbackListener() {
        return callbackListener;
    }

    /**
     * Method changes the callback listener of the dialog.
     *
     * @param callbackListener      New callback listener.
     * @throws NullPointerException The passed argument is {@code null}.
     */
    public void setCallbackListener(DialogCallbackListener callbackListener) throws NullPointerException {
        if (callbackListener == null) {
            throw new NullPointerException();
        }
        this.callbackListener = callbackListener;
    }

    /**
     * Method returns the title of the dialog.
     *
     * @return  Title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Method changes the title of the dialog.
     *
     * @param title                 New title.
     * @throws NullPointerException The passed title is {@code null}.
     */
    public void setTitle(String title) throws NullPointerException {
        if (title == null) {
            throw new NullPointerException();
        }
        this.title = title;
    }

    /**
     * Method returns the title of the dialog.
     *
     * @return  Info of the dialog.
     */
    public String getInfo() {
        return info;
    }

    /**
     * Method changes the info of the dialog. If no info shall be displayed, pass {@code null} as
     * argument.
     *
     * @param info  Info for the dialog.
     */
    public void setInfo(String info) {
        this.info = info;
    }


    /**
     * Method generates a new view for the dialog from the passed view.
     *
     * @param view  Inflated view of the dialog.
     * @return      Edited view.
     */
    public View createView(View view) {
        if (view == null) {
            return null;
        }

        TextView infoTextView = view.findViewById(R.id.dialog_enter_password_info);
        if (getInfo() == null) {
            infoTextView.setVisibility(View.GONE);
        }
        else {
            infoTextView.setVisibility(View.VISIBLE);
            infoTextView.setText(getInfo());
        }

        return view;
    }


    /**
     * Method processes the arguments that were passed to the {@link EnterPasswordDialog}.
     *
     * @param args                      Passed arguments to be processed.
     * @throws NullPointerException     Some arguments are {@code null}.
     * @throws ClassCastException       The {@linkplain java.io.Serializable} cannot be casted to
     *                                  {@link DialogCallbackListener}.
     * @throws DialogArgumentException  Some arguments are missing.
     */
    public void processArguments(Bundle args) throws NullPointerException, ClassCastException, DialogArgumentException {
        if (args == null) {
            throw new NullPointerException();
        }

        //Process KEY_CALLBACK_LISTENER:
        if (args.containsKey(EnterPasswordDialog.KEY_CALLBACK_LISTENER)) {
            try {
                setCallbackListener((DialogCallbackListener) args.getSerializable(EnterPasswordDialog.KEY_CALLBACK_LISTENER));
            }
            catch (ClassCastException e) {
                e.printStackTrace();
                throw e;
            }
        }
        else {
            throw new DialogArgumentException("Missing argument KEY_CALLBACK_LISTENER");
        }

        //Process KEY_TITLE:
        if (args.containsKey(EnterPasswordDialog.KEY_TITLE)) {
            setTitle(args.getString(EnterPasswordDialog.KEY_TITLE));
        }
        else {
            throw new DialogArgumentException("Missing argument KEY_TITLE");
        }

        //Process KEY_INFO:
        if (args.containsKey(EnterPasswordDialog.KEY_INFO)) {
            setInfo(args.getString(EnterPasswordDialog.KEY_INFO));
        }
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

        TextInputEditText passwordEditText = view.findViewById(R.id.dialog_enter_password);
        String password = Objects.requireNonNull(passwordEditText.getText()).toString();
        TextInputLayout passwordLayout = view.findViewById(R.id.dialog_enter_password_hint);
        if (password.isEmpty()) {
            passwordLayout.setError(view.getContext().getString(R.string.error_empty_input));
            return false;
        }
        else if (!Account.getInstance().isPassword(password)) {
            passwordLayout.setError(view.getContext().getString(R.string.error_passwords_incorrect));
            return false;
        }

        return true;
    }

}
