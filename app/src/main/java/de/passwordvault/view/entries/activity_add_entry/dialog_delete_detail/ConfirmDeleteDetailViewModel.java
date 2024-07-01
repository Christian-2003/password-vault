package de.passwordvault.view.entries.activity_add_entry.dialog_delete_detail;

import android.os.Bundle;
import androidx.lifecycle.ViewModel;
import de.passwordvault.model.detail.Detail;
import de.passwordvault.view.entries.activity_add_entry.dialog_delete_detail.ConfirmDeleteDetailDialog;
import de.passwordvault.view.utils.DialogArgumentException;
import de.passwordvault.view.utils.DialogCallbackListener;


/**
 * Class implements the {@linkplain ViewModel} for the {@link ConfirmDeleteDetailDialog}-class.
 *
 * @author  Christian-2003
 * @version 3.2.0
 */
public class ConfirmDeleteDetailViewModel extends ViewModel {

    /**
     * Attribute stores the callback listener for the {@link ConfirmDeleteDetailDialog}.
     */
    private DialogCallbackListener callbackListener;

    /**
     * Attribute stores the {@link Detail} to be deleted.
     */
    private Detail detail;


    /**
     * Method returns the {@link #callbackListener} of the dialog.
     *
     * @return  Callback listener.
     */
    public DialogCallbackListener getCallbackListener() {
        return callbackListener;
    }

    /**
     * Method changes the {@link #callbackListener} of the dialog.
     *
     * @param callbackListener      New callback listener.
     * @throws NullPointerException The passed argument is {@code null}.
     */
    public void setCallbackListener(DialogCallbackListener callbackListener) throws NullPointerException {
        if (callbackListener == null) {
            throw new NullPointerException("Null is invalid CallbackListener");
        }
        this.callbackListener = callbackListener;
    }

    /**
     * Method returns the {@link #detail} that shall be deleted.
     *
     * @return  Detail which shall be deleted.
     */
    public Detail getDetail() {
        return detail;
    }

    /**
     * Method changes the {@link #detail} of the dialog.
     *
     * @param detail                New detail which shall be deleted.
     * @throws NullPointerException The passed argument is {@code null}.
     */
    public void setDetail(Detail detail) throws NullPointerException {
        if (detail == null) {
            throw new NullPointerException("Null is invalid detail");
        }
        this.detail = detail;
    }


    /**
     * Method processes the arguments that were passed to the {@link ConfirmDeleteDetailDialog}.
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
        if (args.containsKey(ConfirmDeleteDetailDialog.KEY_DETAIL)) {
            setDetail((Detail) args.getSerializable(ConfirmDeleteDetailDialog.KEY_DETAIL));
        }
        else {
            throw new DialogArgumentException("Missing argument KEY_DETAIL");
        }

        //Process KEY_CALLBACK_LISTENER:
        if (args.containsKey(ConfirmDeleteDetailDialog.KEY_CALLBACK_LISTENER)) {
            try {
                setCallbackListener((DialogCallbackListener) args.getSerializable(ConfirmDeleteDetailDialog.KEY_CALLBACK_LISTENER));
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
