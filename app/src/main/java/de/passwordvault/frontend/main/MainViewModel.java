package de.passwordvault.frontend.main;

import androidx.lifecycle.ViewModel;

import de.passwordvault.R;
import de.passwordvault.frontend.main.entries.EntriesFragment;
import de.passwordvault.frontend.main.home.HomeFragment;
import de.passwordvault.frontend.main.settings.SettingsFragment;


/**
 * Class models the {@linkplain ViewModel} for the {@linkplain MainActivity} which stores all data
 * that shall be persistent throughout activity changes.
 *
 * @author  Christian-2003
 * @version 1.0.0
 */
public class MainViewModel extends ViewModel {

    /**
     * Attribute stores the HomeFragment of the activity.
     */
    private HomeFragment homeFragment;

    /**
     * Attribute stores the EntriesFragment of the activity.
     */
    private EntriesFragment entriesFragment;

    /**
     * Attribute stores the SettingsFragment of the activity.
     */
    private SettingsFragment settingsFragment;

    /**
     * Attribute stores the ID of the item that was selected in the
     * {@linkplain com.google.android.material.bottomnavigation.BottomNavigationView}.
     */
    private int selectedItem;


    /**
     * Constructor instantiates a new MainViewModel with default values.
     */
    public MainViewModel() {
        homeFragment = new HomeFragment();
        entriesFragment = new EntriesFragment();
        settingsFragment = new SettingsFragment();
        selectedItem = R.id.menu_home;
    }


    public HomeFragment getHomeFragment() {
        return homeFragment;
    }

    public void setHomeFragment(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
    }

    public EntriesFragment getEntriesFragment() {
        return entriesFragment;
    }

    public void setEntriesFragment(EntriesFragment entriesFragment) {
        this.entriesFragment = entriesFragment;
    }

    public SettingsFragment getSettingsFragment() {
        return settingsFragment;
    }

    public void setSettingsFragment(SettingsFragment settingsFragment) {
        this.settingsFragment = settingsFragment;
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }

}
