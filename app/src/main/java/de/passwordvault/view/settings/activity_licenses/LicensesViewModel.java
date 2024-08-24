package de.passwordvault.view.settings.activity_licenses;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.view.utils.Utils;


/**
 * Class implements the view model for the activity displaying the licenses of the used software.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class LicensesViewModel extends ViewModel {

    /**
     * Class models a used software with a license that needs to be displayed within the app.
     */
    public static class UsedSoftware {

        /**
         * Attribute stores the name of the used software.
         */
        @NonNull
        private final String name;

        /**
         * Attribute stores the version of the used software.
         */
        @NonNull
        private final String version;

        /**
         * Attribute stores the name of the raw resource file in which the software license is
         * stored.
         */
        @NonNull
        private final String licenseResourceFile;

        /**
         * Attribute stores the name of the license used by the software.
         */
        @NonNull
        private final String licenseName;


        /**
         * Constructor instantiates a new used software instance.
         *
         * @param name                  Name of the used software.
         * @param version               Version of the used software.
         * @param licenseResourceFile   Name of the raw resource file in which the license text is
         *                              stored.
         * @param licenseName           Name of the license used by the software.
         */
        public UsedSoftware(@NonNull String name, @NonNull String version, @NonNull String licenseResourceFile, @NonNull String licenseName) {
            this.name = name;
            this.version = version;
            this.licenseResourceFile = licenseResourceFile;
            this.licenseName = licenseName;
        }


        /**
         * Method returns the name of the used software.
         *
         * @return  Name of the used software.
         */
        @NonNull
        public String getName() {
            return name;
        }

        /**
         * Method returns the version of the used software.
         *
         * @return  Version of the used software.
         */
        @NonNull
        public String getVersion() {
            return version;
        }

        /**
         * Method returns the name of the raw resource file in which the license text of the software
         * is stored.
         *
         * @return  Name of the raw resource file containing the license text.
         */
        @NonNull
        public String getLicenseResourceFile() {
            return licenseResourceFile;
        }

        /**
         * Method returns the name of the license used by the software.
         *
         * @return  Name of the license used by the software.
         */
        @NonNull
        public String getLicenseName() {
            return licenseName;
        }

    }


    /**
     * Attribute stores a list of the software used within the app.
     */
    @Nullable
    private ArrayList<UsedSoftware> usedSoftware;


    /**
     * Constructor instantiates a new view model.
     */
    public LicensesViewModel() {
        usedSoftware = null;
    }


    /**
     * Method returns a list of the used software.
     *
     * @return  List of the used software.
     */
    @NonNull
    public ArrayList<UsedSoftware> getUsedSoftware() {
        if (usedSoftware == null) {
            loadUsedSoftware();
        }
        return usedSoftware;
    }


    /**
     * Method loads the license text for the passed used software from a raw resource. The return
     * value can be {@code null} if no license text is available.
     *
     * @param software  Used software for which to load the license text.
     * @return          License text for the used software.
     */
    public String getLicenseText(UsedSoftware software) {
        return Utils.readRawResource(software.getLicenseResourceFile());
    }


    /**
     * Method loads the used software from a raw resource file. After the method finishes execution,
     * the {@link #usedSoftware} is not null.
     */
    private void loadUsedSoftware() {
        String csv = Utils.readRawResource(R.raw.licenses);
        usedSoftware = new ArrayList<>();
        if (csv == null) {
            return;
        }
        String[] software = csv.split("\n");
        for (String s : software) {
            String[] attributes = s.split(",");
            if (attributes.length == 4) {
                usedSoftware.add(new UsedSoftware(attributes[0], attributes[1], attributes[2], attributes[3]));
            }
        }
    }

}
