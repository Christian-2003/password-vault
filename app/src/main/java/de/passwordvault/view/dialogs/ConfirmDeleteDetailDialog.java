package de.passwordvault.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import de.passwordvault.R;
import de.passwordvault.view.utils.DialogCallbackListener;
import de.passwordvault.model.detail.Detail;
import de.passwordvault.viewmodel.dialogs.ConfirmDeleteDetailViewModel;


/**
 * Class models a {@linkplain Dialog} which allows the user to confirm a deletion of a {@linkplain Detail}
 * The {@linkplain android.app.Activity} that creates this dialog, must implement the interface
 * {@linkplain DialogCallbackListener}!
 *
 * @author  Christian-2003
 * @version 3.2.0
 */
public class ConfirmDeleteDetailDialog extends DialogFragment {

    /**
     * Field contains the key that needs to be used when passing the detail as argument.
     */
    public static final String KEY_DETAIL = "detail";

    /**
     * Field contains the key that needs to be used when passing a
     * {@link de.passwordvault.view.utils.DialogCallbackListener} as argument.
     */
    public static final String KEY_CALLBACK_LISTENER = "callback_listener";


    /**
     * Attribute stores the UUID of the detail to be deleted.
     */
    private ConfirmDeleteDetailViewModel viewModel;


    /**
     * Constructor constructs a new ConfirmDetailDeleteDialogFragment which allows the user to confirm
     * the deletion of the passed detail.
     */
    public ConfirmDeleteDetailDialog() {
        //Required empty constructor.
    }


    /**
     * Method returns the UUID of the {@link ConfirmDeleteDetailViewModel#getDetail()} which shall be
     * deleted.
     *
     * @return  UUID of the detail to be removed.
     */
    public String getUuid() {
        return viewModel.getDetail().getUuid();
    }


    /**
     * Method is called whenever a ConfirmDeleteDetailViewModel is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      Created dialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ConfirmDeleteDetailViewModel.class);

        try {
            viewModel.processArguments(getArguments());
        }
        catch (Exception e) {
            //Converting every occurring exception to 'ClassCastException' to match methods signature
            //must surely be the stupidest decision made in this project so far...
            throw new ClassCastException(e.getMessage());
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle(R.string.button_delete);
        builder.setMessage(requireContext().getString(R.string.confirm_delete_dialog_confirm) + " " + viewModel.getDetail().getName() + ".");

        builder.setPositiveButton(R.string.button_delete, (dialog, id) -> {
            //Delete button:
            viewModel.getCallbackListener().onPositiveCallback(ConfirmDeleteDetailDialog.this);
        });
        builder.setNegativeButton(R.string.button_cancel, (dialog, id) -> {
            //Cancel button:
            viewModel.getCallbackListener().onNegativeCallback(ConfirmDeleteDetailDialog.this);
        });

        return builder.create();
    }

}
