package de.passwordvault.view.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import de.passwordvault.R;
import de.passwordvault.viewmodel.dialogs.ChangePasswordViewModel;


/**
 * Class implements the dialog to change a password.
 *
 * @author  Christian-2003
 * @version 3.2.0
 */
public class ChangePasswordDialog extends DialogFragment {

    /**
     * Attribute stores the ViewModel for the dialog.
     */
    private ChangePasswordViewModel viewModel;

    /**
     * Attribute stores the view of the dialog.
     */
    private View view;


    /**
     * Method is called whenever a {@link ChangePasswordDialog} is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      Created dialog.
     */
    @SuppressLint("InflateParams") //Ignore passing 'null' as root for the dialog's view.
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ChangePasswordViewModel.class);

        view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_change_password, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.settings_security_password));
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
     * Method is called whenever the {@link ChangePasswordDialog} is started. The method configures the
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
            if (!viewModel.processUserInput(ChangePasswordDialog.this.view)) {
                //Some inputs are incorrect:
                return;
            }
            dismiss();
        });

        //Configure negative button:
        Button negativeButton = dialog.getButton(Dialog.BUTTON_NEGATIVE);
        negativeButton.setOnClickListener(view -> dismiss());
    }
}
