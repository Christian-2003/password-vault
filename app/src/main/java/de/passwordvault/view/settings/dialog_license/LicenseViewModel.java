package de.passwordvault.view.settings.dialog_license;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;


/**
 * Class implements the view model for the {@link LicenseDialog}.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class LicenseViewModel extends ViewModel {

    /**
     * Attribute stores the title of the dialog.
     */
    @NonNull
    private String title;

    /**
     * Attribute stores the license text of the dialog.
     */
    @NonNull
    private String license;


    /**
     * Constructor instantiates a new view model.
     */
    public LicenseViewModel() {
        title = "";
        license = "";
    }


    /**
     * Method returns the title of the dialog.
     *
     * @return  Title of the dialog.
     */
    @NonNull
    public String getTitle() {
        return title;
    }

    /**
     * Method returns the license text of the dialog.
     *
     * @return  License text of the dialog.
     */
    @NonNull
    public String getLicense() {
        return license;
    }


    /**
     * Method processes the arguments passed to the dialog.
     *
     * @param args  Arguments to process.
     */
    public void processArguments(@NonNull Bundle args) {
        if (args.containsKey(LicenseDialog.ARG_TITLE)) {
            String title = args.getString(LicenseDialog.ARG_TITLE);
            if (title != null) {
                this.title = title;
            }
        }
        if (args.containsKey(LicenseDialog.ARG_LICENSE)) {
            String license = args.getString(LicenseDialog.ARG_LICENSE);
            if (license != null) {
                this.license = license;
            }
        }
    }

}
