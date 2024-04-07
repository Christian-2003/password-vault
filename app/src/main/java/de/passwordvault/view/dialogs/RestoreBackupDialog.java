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

import de.passwordvault.R;
import de.passwordvault.viewmodel.dialogs.RestoreBackupViewModel;


/**
 * Class implements a {@linkplain Dialog} which allows the user to enter a password to restore an
 * encrypted backup. The class that calls this dialog must always implement the interface
 * {@link de.passwordvault.view.utils.DialogCallbackListener}.
 *
 * @author  Christian-2003
 * @version 3.2.0
 */
public class RestoreBackupDialog extends DialogFragment {

    /**
     * Field contains the key that needs to be used when passing the file as argument.
     */
    public static final String KEY_FILE = "file";

    /**
     * Field contains the key that needs to be used when passing a
     * {@link de.passwordvault.view.utils.DialogCallbackListener} as argument.
     */
    public static final String KEY_CALLBACK_LISTENER = "callback_listener";


    /**
     * Attribute stores the {@link RestoreBackupViewModel} for this {@link RestoreBackupDialog}.
     */
    private RestoreBackupViewModel viewModel;

    /**
     * Attribute stores the view for the dialog.
     */
    private View view;

    /**
     * Method returns the URI to the file from which the backup shall be restored.
     *
     * @return  URI to the file of the backup.
     */
    public Uri getFile() {
        return viewModel.getFile();
    }

    /**
     * Method returns the password that was entered by the user. If the user did not enter
     * any password, {@code null} is returned.
     *
     * @return  Entered password.
     */
    public String getPassword() {
        return viewModel.getPassword();
    }


    /**
     * Method is called whenever a {@link RestoreBackupViewModel} is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      Created dialog.
     */
    @SuppressLint("InflateParams") //Ignore passing 'null' as root for the dialog's view.
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) throws ClassCastException {
        super.onCreateDialog(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(RestoreBackupViewModel.class);

        try {
            viewModel.processArguments(getArguments());
        }
        catch (Exception e) {
            //Converting every occurring exception to 'ClassCastException' to match methods signature
            //must surely be the stupidest decision made in this project so far...
            throw new ClassCastException(e.getMessage());
        }

        view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_restore_backup, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle(getString(R.string.settings_data_backup_restore));
        builder.setView(view);

        builder.setPositiveButton(R.string.button_ok, (dialog, id) -> {
            //Action implemented in onStart()-method!
        });
        builder.setNegativeButton(R.string.button_cancel, (dialog, id) -> {
            //Action implemented in onStart()-method!
        });

        return builder.create();
    }


    /**
     * Method is called whenever the {@link RestoreBackupDialog} is started. The method configures the
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
            if (!viewModel.processUserInput(RestoreBackupDialog.this.view)) {
                //Some inputs are incorrect:
                return;
            }
            dismiss();
            viewModel.getCallbackListener().onPositiveCallback(RestoreBackupDialog.this);
        });

        //Configure negative button:
        Button negativeButton = dialog.getButton(Dialog.BUTTON_NEGATIVE);
        negativeButton.setOnClickListener(view -> {
            dismiss();
            viewModel.getCallbackListener().onNegativeCallback(RestoreBackupDialog.this);
        });
    }

}
