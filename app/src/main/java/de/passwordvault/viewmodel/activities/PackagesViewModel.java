package de.passwordvault.viewmodel.activities;

import android.os.Bundle;

import androidx.lifecycle.ViewModel;
import de.passwordvault.view.activities.PackagesActivity;


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
     * Constructor instantiates a new view model.
     */
    public PackagesViewModel() {
        selectedPackageName = null;
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
            return true;
        }
        if (args.containsKey(PackagesActivity.KEY_PACKAGE)) {
            setSelectedPackageName(args.getString(PackagesActivity.KEY_PACKAGE));
        }
        return true;
    }

}
