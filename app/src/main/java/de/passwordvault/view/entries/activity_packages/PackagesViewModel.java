package de.passwordvault.view.entries.activity_packages;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.packages.Package;
import de.passwordvault.model.packages.PackageCollection;
import de.passwordvault.model.packages.PackagesManager;
import de.passwordvault.model.packages.SerializablePackage;
import de.passwordvault.model.packages.SerializablePackageCollection;
import de.passwordvault.view.entries.activity_packages.fragment_list.PackagesListFragment;


/**
 * Class implements a view model for {@link PackagesActivity}.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class PackagesViewModel extends ViewModel {

    /**
     * Attribute stores the packages that are selected by the activity.
     */
    private PackageCollection selectedPackages;

    /**
     * Attribute stores a list of all packages.
     */
    @Nullable
    private ArrayList<Package> allPackages;

    /**
     * Attribute stores whether the search bar of the {@link PackagesListFragment}
     * is visible.
     */
    private boolean searchBarVisible;


    /**
     * Constructor instantiates a new view model for the {@link PackagesActivity} and it's fragments.
     */
    public PackagesViewModel() {
        selectedPackages = null;
        allPackages = null;
        searchBarVisible = false;
    }


    /**
     * Method returns the packages that are selected. This is {@code null} only when they were not
     * set previously.
     *
     * @return  Selected packages.
     */
    public PackageCollection getSelectedPackages() {
        return selectedPackages;
    }

    /**
     * Method changes the selected packages to the passed argument.
     *
     * @param selectedPackages              New collection of selected packages.
     * @throws NullPointerException The passed argument is {@code null}.
     */
    public void setSelectedPackages(PackageCollection selectedPackages) throws NullPointerException {
        if (selectedPackages == null) {
            throw new NullPointerException();
        }
        this.selectedPackages = selectedPackages;
    }

    /**
     * Method returns a list of all packages.
     *
     * @return  List of all packages.
     */
    @Nullable
    public ArrayList<Package> getAllPackages() {
        return allPackages;
    }

    /**
     * Method returns whether the search bar is visible.
     *
     * @return  Whether the search bar is visible.
     */
    public boolean isSearchBarVisible() {
        return searchBarVisible;
    }

    /**
     * Method changes whether the search bar is visible.
     *
     * @param searchBarVisible  Whether the search bar is visible.
     */
    public void setSearchBarVisible(boolean searchBarVisible) {
        this.searchBarVisible = searchBarVisible;
    }


    /**
     * Method processes the arguments that were passed to the activity.
     *
     * @param args  Passed arguments.
     * @return      Whether the arguments were successfully processed.
     */
    public boolean processArguments(Bundle args) {
        if (args == null) {
            setSelectedPackages(new PackageCollection());
            return true;
        }
        if (args.containsKey(PackagesActivity.KEY_PACKAGES)) {
            try {
                ArrayList<SerializablePackage> packages = (ArrayList<SerializablePackage>)args.getSerializable(PackagesActivity.KEY_PACKAGES);
                SerializablePackageCollection serializablePackages = new SerializablePackageCollection(packages);
                setSelectedPackages(serializablePackages.toPackageCollection());
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


    /**
     * Method loads all packages and invokes the passed runnable afterwards.
     *
     * @param runnable  Runnable to invoke after the packages are loaded.
     */
    public void loadPackages(@Nullable Runnable runnable) {
        Thread thread = new Thread(() -> {
            allPackages = PackagesManager.getInstance().getSortedPackages();
            if (runnable != null) {
                try {
                    runnable.run();
                }
                catch (Exception e) {
                    Log.w("PackagesViewModel", "Cannot update UI after packages loaded: " + e.getMessage());
                }
            }
        });
        thread.start();
    }

}
