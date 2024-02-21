package de.passwordvault.view.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import de.passwordvault.R;
import de.passwordvault.viewmodel.dialogs.EnterPasswordViewModel;


/**
 * Class implements a dialog through which the user can confirm the
 * {@link de.passwordvault.model.security.login.Account}-password. If the entered password is correct,
 * the positive callback is triggered.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public class EnterPasswordDialog extends DialogFragment {

    /**
     * Field stores the key that needs to be used when passing a
     * {@link de.passwordvault.view.utils.DialogCallbackListener} as argument.
     */
    public static final String KEY_CALLBACK_LISTENER = "callback_listener";

    /**
     * Field stores the key that needs to be used when passing a title as argument.
     */
    public static final String KEY_TITLE = "title";

    /**
     * Field stores the key that needs to be used when passing an optional info text as argument.
     */
    public static final String KEY_INFO = "info";


    /**
     * Attribute stores the {@link EnterPasswordViewModel} for this dialog.
     */
    private EnterPasswordViewModel viewModel;

    /**
     * Attribute stores the inflated view of this dialog.
     */
    private View view;


    /**
     * Method is called whenever a {@link EnterPasswordViewModel} is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      Created dialog.
     */
    @SuppressLint("InflateParams") //Ignore passing 'null' as root for the dialog's view.
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) throws ClassCastException {
        super.onCreateDialog(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(EnterPasswordViewModel.class);

        try {
            viewModel.processArguments(getArguments());
        }
        catch (Exception e) {
            throw new ClassCastException(e.getMessage());
        }

        view = requireActivity().getLayoutInflater().inflate(R.layout.enter_password_dialog, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle(viewModel.getTitle());
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
     * Method is called whenever the dialog is started. The method configures the click listeners
     * for the dialog buttons.
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
            if (!viewModel.processUserInput(EnterPasswordDialog.this.view)) {
                //Some inputs are incorrect:
                return;
            }
            dismiss();
            viewModel.getCallbackListener().onPositiveCallback(EnterPasswordDialog.this);
        });

        //Configure negative button:
        Button negativeButton = dialog.getButton(Dialog.BUTTON_NEGATIVE);
        negativeButton.setOnClickListener(view -> {
            dismiss();
            viewModel.getCallbackListener().onNegativeCallback(EnterPasswordDialog.this);
        });
    }

}
