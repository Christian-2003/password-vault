package de.passwordvault.view.settings.dialog_license;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

public class LicenseViewModel extends ViewModel {

    @NonNull
    private String title;

    @NonNull
    private String license;


    public LicenseViewModel() {
        title = "";
        license = "";
    }


    @NonNull
    public String getTitle() {
        return title;
    }

    @NonNull
    public String getLicense() {
        return license;
    }


    public void processArguments(@NonNull Bundle extras) {
        if (extras.containsKey(LicenseDialog.ARG_TITLE)) {
            String title = extras.getString(LicenseDialog.ARG_TITLE);
            if (title != null) {
                this.title = title;
            }
        }
        if (extras.containsKey(LicenseDialog.ARG_LICENSE)) {
            String license = extras.getString(LicenseDialog.ARG_LICENSE);
            if (license != null) {
                this.license = license;
            }
        }
    }

}
