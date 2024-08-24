package de.passwordvault.view.general.dialog_delete;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import de.passwordvault.view.utils.components.PasswordVaultBottomSheetDialog;


/**
 * Class implements the view model for the {@link DeleteDialog}.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class DeleteViewModel extends ViewModel {

    /**
     * Attribute stores the callback to notify when the dialog closes.
     */
    private PasswordVaultBottomSheetDialog.Callback callback;

    /**
     * Attribute stores the message to display.
     */
    private String message;


    /**
     * Constructor instantiates a new view model.
     */
    public DeleteViewModel() {
        callback = null;
        message = null;
    }


    /**
     * Method returns the callback for the dialog.
     *
     * @return  Callback for the dialog.
     */
    public PasswordVaultBottomSheetDialog.Callback getCallback() {
        return callback;
    }

    /**
     * Method returns the message for the dialog.
     *
     * @return  Message for the dialog.
     */
    public String getMessage() {
        return message;
    }


    /**
     * Method processes the arguments passed to the dialog within the bundle.
     *
     * @param args  Bundle containing the arguments.
     */
    public void processArguments(@NonNull Bundle args) {
        if (args.containsKey(DeleteDialog.ARG_CALLBACK)) {
            try {
                callback = (PasswordVaultBottomSheetDialog.Callback)args.getSerializable(DeleteDialog.ARG_CALLBACK);
            }
            catch (Exception e) {
                //Ignore...
            }
        }

        if (args.containsKey(DeleteDialog.ARG_MESSAGE)) {
            message = args.getString(DeleteDialog.ARG_MESSAGE);
        }
        if (message == null) {
            message = "";
        }
    }

}
