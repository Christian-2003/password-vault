package de.passwordvault.viewmodel.dialogs;

import android.os.Bundle;
import androidx.lifecycle.ViewModel;
import de.passwordvault.view.dialogs.ConfirmDeleteDialog;
import de.passwordvault.view.utils.DialogArgumentException;
import de.passwordvault.view.utils.DialogCallbackListener;


/**
 * Class implements the {@linkplain ViewModel} for the {@link ConfirmDeleteDialog}-class.
 *
 * @author  Christian-2003
 * @version 3.2.0
 */
public class ConfirmDeleteViewModel extends ViewModel {

    /**
     * Attribute stores the callback listener which waits for the
     * {@link de.passwordvault.view.dialogs.ConfirmDeleteDialog} to be closed.
     */
    private DialogCallbackListener callbackListener;

    /**
     * Attribute stores the name of the object which shall be deleted.
     */
    private String deletedObjectName;


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
     * Method changes the {@link #deletedObjectName} to the passed argument.
     *
     * @param deletedObjectName     Name of the object which shall be deleted.
     * @throws NullPointerException The passed argument is {@code null}.
     */
    public void setDeletedObjectName(String deletedObjectName) throws NullPointerException {
        if (deletedObjectName == null) {
            throw new NullPointerException("Null is invalid name for object");
        }
        this.deletedObjectName = deletedObjectName;
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

        //Process KEY_DETAIL:
        if (args.containsKey(ConfirmDeleteDialog.KEY_OBJECT)) {
            setDeletedObjectName(args.getString(ConfirmDeleteDialog.KEY_OBJECT));
        }
        else {
            throw new DialogArgumentException("Missing argument KEY_OBJECT");
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
