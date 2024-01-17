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
import de.passwordvault.viewmodel.dialogs.ConfigureLoginViewModel;


/**
 * Class implements a dialog to configure the application login.
 *
 * @author  Christian-2003
 * @version 3.2.0
 */
public class ConfigureLoginDialog extends DialogFragment {

    /**
     * Field stores the key that must be used when passing the callback listener as argument.
     */
    public static final String KEY_CALLBACK_LISTENER = "callback_listener";


    /**
     * Attribute stores the {@linkplain androidx.lifecycle.ViewModel} for the dialog.
     */
    private ConfigureLoginViewModel viewModel;

    /**
     * Attribute stores the view for the dialog.
     */
    private View view;


    /**
     * Method is called whenever a {@link ConfigureLoginDialog} is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      Created dialog.
     */
    @SuppressLint("InflateParams") //Ignore passing 'null' as root for the dialog's view.
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) throws ClassCastException {
        super.onCreateDialog(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ConfigureLoginViewModel.class);

        try {
            viewModel.processArguments(getArguments());
        }
        catch (Exception e) {
            //Converting every occurring exception to 'ClassCastException' to match methods signature
            //must surely be the stupidest decision made in this project so far...
            throw new ClassCastException(e.getMessage());
        }

        view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_configure_login, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.settings_security_password);
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
     * Method is called whenever the {@link ConfigureLoginDialog} is started. The method configures the
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
            if (!viewModel.processUserInput(ConfigureLoginDialog.this.view)) {
                //Some inputs are incorrect:
                return;
            }
            dismiss();
            viewModel.getCallbackListener().onPositiveCallback(ConfigureLoginDialog.this);
        });

        //Configure negative button:
        Button negativeButton = dialog.getButton(Dialog.BUTTON_NEGATIVE);
        negativeButton.setOnClickListener(view -> {
            dismiss();
            viewModel.getCallbackListener().onNegativeCallback(ConfigureLoginDialog.this);
        });
    }
}
