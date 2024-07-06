package de.passwordvault.view.entries.activity_add_entry.dialog_delete;

import android.os.Bundle;
import androidx.lifecycle.ViewModel;
import de.passwordvault.view.entries.activity_add_entry.dialog_delete.ConfirmDeleteDialog;
import de.passwordvault.view.utils.DialogArgumentException;
import de.passwordvault.view.utils.DialogCallbackListener;


/**
 * Class implements the {@linkplain ViewModel} for the {@link ConfirmDeleteDialog}-class.
 *
 * @author  Christian-2003
 * @version 3.5.4
 */
public class ConfirmDeleteViewModel extends ViewModel {

    /**
     * Attribute stores the callback listener which waits for the
     * {@link ConfirmDeleteDialog} to be closed.
     */
    private DialogCallbackListener callbackListener;

    /**
     * Attribute stores the name of the object which shall be deleted.
     */
    private String deletedObjectName;

    /**
     * Attribute stores the message to display to the user within the dialog. Instead of some generic
     * message like "Are you sure you want to delete {deletedObjectName}?", this message can be shown
     * instead. This is {@code null} if the generic message shall be displayed instead.
     */
    private String message;


    /**
     * Method returns the {@link #callbackListener} of the dialog.
     *
     * @return  Callback listener.
     */
    public DialogCallbackListener getCallbackListener() {
        return callbackListener;
    }

    /**
     * Method changes the {@link #callbackListener} to the passed argument.
     *
     * @param callbackListener      New callback listener for the dialog.
     * @throws NullPointerException The passed argument is {@code null}.
     */
    public void setCallbackListener(DialogCallbackListener callbackListener) throws NullPointerException {
        if (callbackListener == null) {
            throw new NullPointerException("Null is invalid CallbackListener");
        }
        this.callbackListener = callbackListener;
    }

    /**
     * Method returns the {@link #deletedObjectName}.
     *
     * @return  Name of the object which shall be deleted.
     */
    public String getDeletedObjectName() {
        return deletedObjectName;
    }

    /**
     * Method returns the message to be displayed to the user instead of a generic message.
     *
     * @return  Message to display to the user.
     */
    public String getMessage() {
        return message;
    }


    /**
     * Method processes the arguments that were passed to the {@link ConfirmDeleteDialog}.
     *
     * @param args                      Passed arguments to be processed.
     * @throws NullPointerException     Some arguments are {@code null}.
     * @throws ClassCastException       The {@linkplain java.io.Serializable} cannot be casted to
     *                                  {@link DialogCallbackListener}.
     * @throws DialogArgumentException  Some arguments are missing.
     */
    public void processArguments(Bundle args) throws NullPointerException, ClassCastException, DialogArgumentException {
        if (args == null) {
            throw new NullPointerException("Null is invalid bundle");
        }

        //Process KEY_OBJECT or KEY_MESSAGE:
        if (args.containsKey(ConfirmDeleteDialog.KEY_OBJECT)) {
            deletedObjectName = args.getString(ConfirmDeleteDialog.KEY_OBJECT);
        }
        else if (args.containsKey(ConfirmDeleteDialog.KEY_MESSAGE)) {
            this.message = args.getString(ConfirmDeleteDialog.KEY_MESSAGE);
        }
        else {
            throw new DialogArgumentException("Missing argument KEY_OBJECT or KEY_MESSAGE");
        }

        //Process KEY_CALLBACK_LISTENER:
        if (args.containsKey(ConfirmDeleteDialog.KEY_CALLBACK_LISTENER)) {
            try {
                setCallbackListener((DialogCallbackListener) args.getSerializable(ConfirmDeleteDialog.KEY_CALLBACK_LISTENER));
            }
            catch (ClassCastException e) {
                e.printStackTrace();
                throw e;
            }
        }
        else {
            throw new DialogArgumentException("Missing argument KEY_CALLBACK_LISTENER");
        }
    }


}
