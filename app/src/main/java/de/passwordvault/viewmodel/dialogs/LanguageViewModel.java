package de.passwordvault.viewmodel.dialogs;

import android.os.Bundle;
import androidx.lifecycle.ViewModel;
import de.passwordvault.view.dialogs.LanguageDialog;
import de.passwordvault.view.utils.DialogCallbackListener;


/**
 * Class implements a view model for the dialog used to change the app language.
 *
 * @author  Christian-2003
 * @version 3.5.1
 */
public class LanguageViewModel extends ViewModel {

    /**
     * Attribute stores the callback listener for the dialog.
     */
    private DialogCallbackListener callbackListener;


    /**
     * Method returns the callback listener for the dialog.
     *
     * @return  Callback listener.
     */
    public DialogCallbackListener getCallbackListener() {
        return callbackListener;
    }


    /**
     * Method processes the arguments that were passed to the language dialog.
     *
     * @param bundle                Arguments passed to the dialog.
     * @return                      Whether the arguments were processed successfully.
     * @throws ClassCastException   Passed arguments could not be casted.
     */
    public boolean processArguments(Bundle bundle) throws ClassCastException {
        if (bundle == null) {
            return false;
        }

        if (bundle.containsKey(LanguageDialog.KEY_CALLBACK_LISTENER)) {
            callbackListener = (DialogCallbackListener)bundle.getSerializable(LanguageDialog.KEY_CALLBACK_LISTENER);
        }
        else {
            return false;
        }
        return true;
    }

}
