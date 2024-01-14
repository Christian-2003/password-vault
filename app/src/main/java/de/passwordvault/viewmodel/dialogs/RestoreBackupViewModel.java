package de.passwordvault.viewmodel.dialogs;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.lifecycle.ViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Objects;
import de.passwordvault.R;
import de.passwordvault.view.dialogs.RestoreBackupDialog;
import de.passwordvault.view.utils.DialogArgumentException;
import de.passwordvault.view.utils.DialogCallbackListener;


/**
 * Class implements the {@linkplain ViewModel} for the {@link RestoreBackupDialog}-class.
 *
 * @author  Christian-2003
 * @version 3.2.0
 */
public class RestoreBackupViewModel extends ViewModel {

    /**
     * Attribute stores the callback listener for the dialog.
     */
    private DialogCallbackListener callbackListener;

    /**
     * Attribute stores the URI to the file in which the backup is stored.
     */
    private Uri file;

    /**
     * Attribute stores the password that was entered by the user. This shall be {@code null} if the
     * user did not enter any password.
     */
    private String password;


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
     * @param callbackListener      New callback listener.
     * @throws NullPointerException The passed callback listener is {@code null}.
     */
    public void setCallbackListener(DialogCallbackListener callbackListener) throws NullPointerException {
        if (callbackListener == null) {
            throw new NullPointerException("Null is invalid callback listener");
        }
        this.callbackListener = callbackListener;
    }

    /**
     * Method returns the URI to the {@link #file} from which the backup shall be restored.
     *
     * @return  URI to the file of the backup.
     */
    public Uri getFile() {
        return file;
    }

    /**
     * Method changes the uri to the {@link #file} from which the backup shall be restored.
     *
     * @param file                  New URI to the file.
     * @throws NullPointerException The passed URI is {@code null}.
     */
    public void setFile(Uri file) throws NullPointerException {
        if (file == null) {
            throw new NullPointerException("Null is invalid URI");
        }
        this.file = file;
    }

    /**
     * Method returns the {@link #password} that was entered by the user. If the user did not enter
     * any password, {@code null} is returned.
     *
     * @return  Entered password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Method changes the {@link #password} that was entered by the user. If the user did not enter any
     * password, pass {@code null} or an empty string as argument.
     *
     * @param password  New password.
     */
    public void setPassword(String password) {
        if (password == null || password.isEmpty()) {
            this.password = null;
        }
        else {
            this.password = password;
        }
    }


    /**
     * Method processes the arguments that were passed to the {@link RestoreBackupDialog}.
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

        //Process KEY_FILE:
        if (args.containsKey(RestoreBackupDialog.KEY_FILE)) {
            setFile(Uri.parse(args.getString(RestoreBackupDialog.KEY_FILE)));
        }
        else {
            throw new DialogArgumentException("Missing argument KEY_FILE");
        }

        //Process KEY_CALLBACK_LISTENER:
        if (args.containsKey(RestoreBackupDialog.KEY_CALLBACK_LISTENER)) {
            try {
                setCallbackListener((DialogCallbackListener) args.getSerializable(RestoreBackupDialog.KEY_CALLBACK_LISTENER));
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

        //Test whether password was entered correctly:
        TextInputEditText passwordEditText = view.findViewById(R.id.dialog_restore_password);
        String password = Objects.requireNonNull(passwordEditText.getText()).toString();
        TextInputLayout passwordLayout = view.findViewById(R.id.dialog_restore_password_hint);
        if (password.isEmpty()) {
            passwordLayout.setError(context.getString(R.string.error_empty_input));
            inputCorrect = false;
        }

        //Process entered data:
        if (!inputCorrect) {
            return false;
        }
        setPassword(password);
        return true;
    }

}
