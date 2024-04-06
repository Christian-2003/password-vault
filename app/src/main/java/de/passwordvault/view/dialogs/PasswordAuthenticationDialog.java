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
import de.passwordvault.viewmodel.dialogs.PasswordAuthenticationViewModel;


/**
 * Class implements a dialog with which the user can authenticate (or register) using the master
 * password of the application.
 * This dialog is used by {@link de.passwordvault.model.security.authentication.Authenticator}.
 *
 * @author  Christian-2003
 * @version 3.5.1
 */
public class PasswordAuthenticationDialog extends DialogFragment {

    /**
     * Field stores the key that needs to be used when passing the callback listener for the dialog.
     */
    public static final String KEY_CALLBACK = "callback_listener";

    /**
     * Field stores the key that needs to be used when passing the ID of the resource-string used
     * as title.
     */
    public static final String KEY_TITLE_ID = "title_id";

    /**
     * Field stores the key that needs to be used to when passing a flag indicating whether the
     * dialog shall be used to register a new password.
     */
    public static final String KEY_REGISTER = "register";


    /**
     * Attribute stores the view model of the dialog.
     */
    private PasswordAuthenticationViewModel viewModel;

    /**
     * Attribute stores the inflated view of the dialog.
     */
    private View view;


    /**
     * Method is called whenever the dialog is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      Created dialog.
     */
    @SuppressLint("InflateParams") //Ignore passing 'null' as root for the dialog's view.
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PasswordAuthenticationViewModel.class);

        view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_password_authentication, null);

        viewModel.processArguments(getArguments());

        if (!viewModel.isRegistering()) {
            view.findViewById(R.id.dialog_password_authentication_confirm_container).setVisibility(View.GONE);
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle(viewModel.getTitleId());
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
     * Method is called whenever the dialog is started. The method's purpose is to attach actions
     * to the buttons of the dialog.
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
            if (!viewModel.processUserInput(PasswordAuthenticationDialog.this.view)) {
                //Some inputs are incorrect:
                return;
            }
            dismiss();
            viewModel.getCallbackListener().onPositiveCallback(PasswordAuthenticationDialog.this);
        });

        //Configure negative button:
        Button negativeButton = dialog.getButton(Dialog.BUTTON_NEGATIVE);
        negativeButton.setOnClickListener(view -> {
            dismiss();
            viewModel.getCallbackListener().onNegativeCallback(PasswordAuthenticationDialog.this);
        });
    }

}
