package de.passwordvault.frontend.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import de.passwordvault.R;


/**
 * Class models a {@linkplain Dialog} which allows the user to confirm a deletion of something.
 * The {@linkplain android.app.Activity} that creates this dialog, must implement the interface
 * {@linkplain DialogCallbackListener}!
 *
 * @author  Christian-2003
 * @version 1.0.0
 */
public class ConfirmDeleteDialogFragment extends DialogFragment {

    /**
     * Stores the name of the deleted object.
     */
    private String deletedObjectName;

    /**
     * Attribute stores the callback listener which waits for this dialog to be closed.
     */
    private DialogCallbackListener callbackListener;


    /**
     * Constructor constructs a new ConfirmDeleteDialogFragment which allows the user to confirm
     * the deletion of something whose name is the passed argument.
     *
     * @param deletedObjectName Name of the object to be deleted.
     */
    public ConfirmDeleteDialogFragment(String deletedObjectName) {
        if (deletedObjectName == null) {
            this.deletedObjectName = "";
        }
        else {
            this.deletedObjectName = deletedObjectName;
        }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //Configure the title:
        builder.setTitle(R.string.button_delete);

        //Configure the layout:
        builder.setMessage(getContext().getString(R.string.confirm_delete_dialog_confirm) + " " + deletedObjectName + ".");

        //Configure buttons
        builder.setPositiveButton(R.string.button_delete, (dialog, id) -> {
            //Delete button:
            callbackListener.onPositiveCallback(ConfirmDeleteDialogFragment.this);
        });
        builder.setNegativeButton(R.string.button_cancel, (dialog, id) -> {
            //Cancel button:
            callbackListener.onNegativeCallback(ConfirmDeleteDialogFragment.this);
        });

        return builder.create();
    }


    /**
     * Method is called whenever this dialog window is attached to an activity.
     *
     * @param context               Context to which the dialog window is attached.
     * @throws ClassCastException   The activity which creates this dialog window does not implement
     *                              {@linkplain DialogCallbackListener}-interface.
     */
    @Override
    public void onAttach(@NonNull Context context) throws ClassCastException {
        super.onAttach(context);
        try {
            callbackListener = (DialogCallbackListener)context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement DetailDialogCallbackListener");
        }
    }

}
