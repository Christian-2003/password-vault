package de.passwordvault.view.utils;

import androidx.fragment.app.DialogFragment;

import java.io.Serializable;

/**
 * Interface must be implemented by all activities that create an instance of
 * {@linkplain DialogFragment}.
 */
public interface DialogCallbackListener extends Serializable {

    /**
     * Method is called whenever the {@linkplain DialogFragment} is closed through the
     * 'positive' button (i.e. the SAVE button).
     *
     * @param dialog    Dialog which called the method.
     */
    void onPositiveCallback(DialogFragment dialog);

    /**
     * Method is called whenever the {@linkplain DialogFragment} is closed through the
     * 'negative' button (i.e. the CANCEL button).
     *
     * @param dialog    Dialog which called the method.
     */
    void onNegativeCallback(DialogFragment dialog);

}
