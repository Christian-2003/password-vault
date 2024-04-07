package de.passwordvault.view.dialogs;

import android.annotation.SuppressLint;
import androidx.appcompat.app.AlertDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.passwordvault.R;
import de.passwordvault.view.utils.Utils;
import de.passwordvault.viewmodel.dialogs.CreateBackupViewModel;


/**
 * Class implements a {@linkplain Dialog} which allows the user to enter a password to create an
 * encrypted backup. The class that calls this dialog must always implement the interface
 * {@link de.passwordvault.view.utils.DialogCallbackListener}.
 *
 * @author  Christian-2003
 * @version 3.2.0
 */
public class CreateBackupDialog extends DialogFragment {

    /**
     * Field contains the key that needs to be used when passing a directory as argument.
     */
    public static final String KEY_DIRECTORY = "directory";

    /**
     * Field contains the key that needs to be used when passing a
     * {@link de.passwordvault.view.utils.DialogCallbackListener} as
     * argument.
     */
    public static final String KEY_CALLBACK_LISTENER = "callback_listener";


    /**
     * Attribute stores the {@link CreateBackupViewModel} for this {@link CreateBackupDialog}.
     */
    private CreateBackupViewModel viewModel;

    /**
     * Attribute stores the view for the dialog.
     */
    private View view;


    /**
     * Method returns the URI of the directory, into which the backup shall be saved.
     *
     * @return  URI of the directory of the backup.
     */
    public Uri getDirectory() {
        return viewModel.getDirectory();
    }

    /**
     * Method returns the password that was entered by the user. This is {@code null} if the user
     * did not enter any password.
     *
     * @return  Password entered by the user.
     */
    public String getPassword() {
        return viewModel.getPassword();
    }

    /**
     * Method returns the filename, that was entered by the user.
     *
     * @return  Filename entered by the user.
     */
    public String getFilename() {
        return viewModel.getFilename();
    }


    /**
     * Method is called whenever a {@link CreateBackupDialog} is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      Created dialog.
     */
    @SuppressLint("InflateParams") //Ignore passing 'null' as root for the dialog's view.
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) throws ClassCastException {
        super.onCreateDialog(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CreateBackupViewModel.class);

        try {
            viewModel.processArguments(getArguments());
        }
        catch (Exception e) {
            //Converting every occurring exception to 'ClassCastException' to match methods signature
            //must surely be the stupidest decision made in this project so far...
            throw new ClassCastException(e.getMessage());
        }

        viewModel.setFilename(getString(R.string.settings_data_backup_file).replace("{date}", Utils.formatDate(Calendar.getInstance(), "yyyy-MM-dd")));

        view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_create_backup, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle(getString(R.string.settings_data_backup_create));
        builder.setView(viewModel.createView(view));

        builder.setPositiveButton(R.string.button_ok, (dialog, id) -> {
            //Action implemented in onStart()-method!
        });
        builder.setNegativeButton(R.string.button_cancel, (dialog, id) -> {
            //Action implemented in onStart()-method!
        });

        return builder.create();
    }


    /**
     * Method is called whenever the {@link CreateBackupDialog} is started. The method configures the
     * click listeners for the dialog buttons.
     */
    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog)getDialog();
        if (dialog == null) {
            //No dialog available:
            return;
        }

        //Configure positive button:
        Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(view -> {
            if (!viewModel.processUserInput(CreateBackupDialog.this.view)) {
                //Some inputs are incorrect:
                return;
            }
            dismiss();
            viewModel.getCallbackListener().onPositiveCallback(CreateBackupDialog.this);
        });

        //Configure negative button:
        Button negativeButton = dialog.getButton(Dialog.BUTTON_NEGATIVE);
        negativeButton.setOnClickListener(view -> {
            dismiss();
            viewModel.getCallbackListener().onNegativeCallback(CreateBackupDialog.this);
        });
    }

}
