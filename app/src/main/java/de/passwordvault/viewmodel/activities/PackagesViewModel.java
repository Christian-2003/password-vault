package de.passwordvault.viewmodel.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.ViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import de.passwordvault.R;
import de.passwordvault.model.packages.PackageCollection;
import de.passwordvault.model.packages.SerializablePackage;
import de.passwordvault.model.packages.SerializablePackageCollection;
import de.passwordvault.view.activities.PackagesActivity;


/**
 * Class implements a view model for {@link PackagesActivity}.
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public class PackagesViewModel extends ViewModel {

    /**
     * Attribute stores the packages that are selected by the activity.
     */
    private PackageCollection packages;


    /**
     * Constructor instantiates a new view model for the {@link PackagesActivity} and it's fragments.
     */
    public PackagesViewModel() {
        packages = null;
    }


    /**
     * Method returns the packages that are selected. This is {@code null} only when they were not
     * set previously.
     *
     * @return  Selected packages.
     */
    public PackageCollection getPackages() {
        return packages;
    }

    /**
     * Method changes the selected packages to the passed argument.
     *
     * @param packages              New collection of selected packages.
     * @throws NullPointerException The passed argument is {@code null}.
     */
    public void setPackages(PackageCollection packages) throws NullPointerException {
        if (packages == null) {
            throw new NullPointerException();
        }
        this.packages = packages;
    }


    /**
     * Method processes the arguments that were passed to the activity.
     *
     * @param args  Passed arguments.
     * @return      Whether the arguments were successfully processed.
     */
    public boolean processArguments(Bundle args) {
        if (args == null) {
            setPackages(new PackageCollection());
            return true;
        }
        if (args.containsKey(PackagesActivity.KEY_PACKAGES)) {
            try {
                ArrayList<SerializablePackage> packages = (ArrayList<SerializablePackage>)args.getSerializable(PackagesActivity.KEY_PACKAGES);
                SerializablePackageCollection serializablePackages = new SerializablePackageCollection(packages);
                setPackages(serializablePackages.toPackageCollection());
            }
            catch (Exception e) {
                Log.d("PA", e.getMessage());
                return false;
            }
        }
        return true;
    }


    /**
     * Method updates the name of the passed tab at the specified position. The name is determined
     * based on the specified position within the tab bar.
     *
     * @param tab       Tab whose name to update.
     * @param position  Position of the tab within the tab bar.
     */
    public void updateTabName(TabLayout.Tab tab, int position) {
        switch (position) {
            case 0:
                tab.setText(R.string.packages_selected_title);
                break;
            case 1:
                tab.setText(R.string.packages_list_title);
                break;
        }
    }

}
