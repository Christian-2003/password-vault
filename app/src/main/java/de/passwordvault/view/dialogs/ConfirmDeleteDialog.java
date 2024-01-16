package de.passwordvault.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import de.passwordvault.R;
import de.passwordvault.view.utils.DialogCallbackListener;
import de.passwordvault.viewmodel.dialogs.ConfirmDeleteViewModel;


/**
 * Class models a {@linkplain Dialog} which allows the user to confirm a deletion of something.
 * The {@linkplain android.app.Activity} that creates this dialog, must implement the interface
 * {@linkplain DialogCallbackListener}!
 *
 * @author  Christian-2003
 * @version 3.2.0
 */
public class ConfirmDeleteDialog extends DialogFragment {

    /**
     * Field contains the key that needs to be used when passing the detail as argument.
     */
    public static final String KEY_OBJECT = "object";

    /**
     * Field contains the key that needs to be used when passing a
     * {@link de.passwordvault.view.utils.DialogCallbackListener} as argument.
     */
    public static final String KEY_CALLBACK_LISTENER = "callback_listener";


    /**
     * Attribute stores the {@link ConfirmDeleteViewModel} for the dialog.
     */
    private ConfirmDeleteViewModel viewModel;


    /**
     * Constructor constructs a new ConfirmDeleteDialogFragment which allows the user to confirm
     * the deletion of something whose name is the passed argument through
     * {@link DialogFragment#setArguments(Bundle)}.
     */
    public ConfirmDeleteDialog() {
        //Require empty public constructor.
    }


    /**
     * Method is called whenever a ConfirmDeleteDialog is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      Created dialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ConfirmDeleteViewModel.class);

        try {
            viewModel.processArguments(getArguments());
        }
        catch (Exception e) {
            //Converting every occurring exception to 'ClassCastException' to match methods signature
            //must surely be the stupidest decision made in this project so far...
            throw new ClassCastException(e.getMessage());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.button_delete);
        builder.setMessage(requireContext().getString(R.string.confirm_delete_dialog_confirm) + " " + viewModel.getDeletedObjectName() + ".");

        builder.setPositiveButton(R.string.button_delete, (dialog, id) -> {
            //Delete button:
            viewModel.getCallbackListener().onPositiveCallback(ConfirmDeleteDialog.this);
        });
        builder.setNegativeButton(R.string.button_cancel, (dialog, id) -> {
            //Cancel button:
            viewModel.getCallbackListener().onNegativeCallback(ConfirmDeleteDialog.this);
        });

        return builder.create();
    }

}
