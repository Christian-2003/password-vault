package de.passwordvault.viewmodel.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import de.passwordvault.view.activities.PackagesActivity;
import de.passwordvault.view.utils.ActivityCallbackListener;


/**
 * Class implements a view model for {@link PackagesActivity}.
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public class PackagesViewModel extends ViewModel {

    /**
     * Attribute stores the name of the selected package. This is {@code null} when no package is
     * selected.
     */
    private String selectedPackageName;

    /**
     * Attribute stores the callback listener that shall be notified when the activity finishes.
     */
    private ActivityCallbackListener callbackListener;


    /**
     * Constructor instantiates a new view model.
     */
    public PackagesViewModel() {
        selectedPackageName = null;
        callbackListener= null;
    }


    /**
     * Method returns the callback listener.
     *
     * @return  Callback listener.
     */
    public ActivityCallbackListener getCallbackListener() {
        return callbackListener;
    }

    /**
     * Method changes the callback listener to the passed argument.
     *
     * @param callbackListener      New callback listener.
     * @throws NullPointerException The passed argument is {@code null}.
     */
    public void setCallbackListener(ActivityCallbackListener callbackListener) throws NullPointerException {
        if (callbackListener == null) {
            throw new NullPointerException();
        }
        this.callbackListener = callbackListener;
    }

    /**
     * Method returns the name of the selected package. This is {@code null} if no package is
     * selected.
     *
     * @return  Name of the selected package.
     */
    public String getSelectedPackageName() {
        return selectedPackageName;
    }

    /**
     * Method changes the name of the selected package. Pass {@code null} if no package shall be
     * selected.
     *
     * @param selectedPackageName   New selected package name.
     */
    public void setSelectedPackageName(String selectedPackageName) {
        this.selectedPackageName = selectedPackageName;
    }


    /**
     * Method processes the arguments that were passed to the activity.
     *
     * @param args  Passed arguments.
     * @return      Whether the arguments were successfully processed.
     */
    public boolean processArguments(Bundle args) {
        if (args == null) {
            return false;
        }
        if (args.containsKey(PackagesActivity.KEY_CALLBACK_LISTENER)) {
            try {
                setCallbackListener((ActivityCallbackListener)args.getSerializable(PackagesActivity.KEY_CALLBACK_LISTENER));
            }
            catch (ClassCastException e) {
                return false;
            }
        }
        else {
            return false;
        }
        if (args.containsKey(PackagesActivity.KEY_PACKAGE)) {
            setSelectedPackageName(args.getString(PackagesActivity.KEY_PACKAGE));
        }
        return true;
    }

}
