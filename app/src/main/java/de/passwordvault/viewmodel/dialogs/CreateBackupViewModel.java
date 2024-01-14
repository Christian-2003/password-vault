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
import de.passwordvault.view.dialogs.CreateBackupDialog;
import de.passwordvault.view.utils.DialogArgumentException;
import de.passwordvault.view.utils.DialogCallbackListener;


/**
 * Class implements the {@linkplain ViewModel} for the {@link CreateBackupDialog}-
 * class.
 *
 * @author  Christian-2003
 * @version 3.2.0
 */
public class CreateBackupViewModel extends ViewModel {

    /**
     * Attribute stores the callback listener for the dialog.
     */
    private DialogCallbackListener callbackListener;

    /**
     * Attribute stores the URI to the directory into which the backup shall be stored.
     */
    private Uri directory;

    /**
     * Attribute stores the filename that was entered by the user.
     */
    private String filename;

    /**
     * Attribute stores the password that was entered by the user. This is {@code null} if the
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
     * Method returns the URI to the {@link #directory} into which the backup shall be stored.
     *
     * @return  URI to the directory for the backup.
     */
    public Uri getDirectory() {
        return directory;
    }

    /**
     * Method changes the uri to the {@link #directory} into which the backup shall be stored.
     *
     * @param directory             New URI to the directory.
     * @throws NullPointerException The passed URI is {@code null}.
     */
    public void setDirectory(Uri directory) throws NullPointerException {
        if (directory == null) {
            throw new NullPointerException("Null is invalid URI");
        }
        this.directory = directory;
    }

    /**
     * Method returns the {@link #filename} that was entered by the user.
     *
     * @return  Entered filename.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Method changes the {@link #filename} that was entered by the user.
     *
     * @param filename              New filename.
     * @throws NullPointerException The filename is {@code null}.
     */
    public void setFilename(String filename) throws NullPointerException {
        if (filename == null) {
            throw new NullPointerException("Null is invalid filename");
        }
        this.filename = filename;
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
     * Method creates the {@linkplain View} for the {@link CreateBackupDialog}.
     *
     * @param view  Inflated view which shall be created.
     * @return      Created view.
     */
    public View createView(View view) {
        if (view == null) {
            return null;
        }
        Context context = view.getContext();

        TextInputEditText directoryEditText = view.findViewById(R.id.dialog_backup_directory);
        directoryEditText.setText(directory.getPath());

        TextInputEditText filenameEditText = view.findViewById(R.id.dialog_backup_filename);
        filenameEditText.setText(filename);

        TextInputLayout passwordLayout = view.findViewById(R.id.dialog_backup_password_hint);
        passwordLayout.setHint(context.getString(R.string.hint_optional) + " " + context.getString(R.string.hint_password));

        TextInputLayout confirmLayout = view.findViewById(R.id.dialog_backup_confirm_hint);
        confirmLayout.setHint(context.getString(R.string.hint_optional) + " " + context.getString(R.string.hint_confirm_password));

        return view;
    }


    /**
     * Method processes the arguments that were passed to the {@link CreateBackupDialog}.
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

        //Process KEY_DIRECTORY:
        if (args.containsKey(CreateBackupDialog.KEY_DIRECTORY)) {
            setDirectory(Uri.parse(args.getString(CreateBackupDialog.KEY_DIRECTORY)));
        }
        else {
            throw new DialogArgumentException("Missing argument KEY_DIRECTORY");
        }

        //Process KEY_CALLBACK_LISTENER:
        if (args.containsKey(CreateBackupDialog.KEY_CALLBACK_LISTENER)) {
            try {
                setCallbackListener((DialogCallbackListener) args.getSerializable(CreateBackupDialog.KEY_CALLBACK_LISTENER));
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

        //Test whether passwords are entered correctly:
        TextInputEditText passwordEditText = view.findViewById(R.id.dialog_backup_password);
        String password = Objects.requireNonNull(passwordEditText.getText()).toString();
        TextInputEditText confirmEditText = view.findViewById(R.id.dialog_backup_confirm);
        String confirm = Objects.requireNonNull(confirmEditText.getText()).toString();
        TextInputLayout confirmLayout = view.findViewById(R.id.dialog_backup_confirm_hint);
        if (!password.equals(confirm)) {
            //Entered passwords do not match:
            confirmLayout.setError(context.getString(R.string.error_passwords_not_matching));
            inputCorrect = false;
        }
        else {
            confirmLayout.setErrorEnabled(false);
        }

        //Test whether filename is entered correctly:
        TextInputEditText filenameEditText = view.findViewById(R.id.dialog_backup_filename);
        String filename = Objects.requireNonNull(filenameEditText.getText()).toString();
        TextInputLayout filenameLayout = view.findViewById(R.id.dialog_backup_filename_hint);
        if (filename.isEmpty()) {
            filenameLayout.setError(context.getString(R.string.error_empty_input));
            inputCorrect = false;
        }
        else {
            filenameLayout.setErrorEnabled(false);
        }

        //Process entered data:
        if (!inputCorrect) {
            return false;
        }
        setPassword(password);
        setFilename(filename);
        return true;
    }

}
