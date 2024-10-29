package de.passwordvault.view.activity_main;

import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.view.activity_main.fragment_entries.EntriesFragment;
import de.passwordvault.view.activity_main.fragment_home.HomeFragment;
import de.passwordvault.view.activity_main.fragment_settings.SettingsFragment;


/**
 * Class models the {@linkplain ViewModel} for the {@linkplain MainActivity} which stores all data
 * that shall be persistent throughout activity changes.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class MainViewModel extends ViewModel {

    /**
     * Attribute stores the HomeFragment of the activity.
     */
    private final HomeFragment homeFragment;

    /**
     * Attribute stores the EntriesFragment of the activity.
     */
    private final EntriesFragment entriesFragment;

    /**
     * Attribute stores the SettingsFragment of the activity.
     */
    private final SettingsFragment settingsFragment;

    /**
     * Attribute stores the ID of the item that was selected in the
     * {@linkplain com.google.android.material.bottomnavigation.BottomNavigationView}.
     */
    private int selectedItem;

    /**
     * Attribute stores the adapter position of an entry that is currently opened from the MainActivity.
     */
    private int openedEntryAdapterPosition;


    /**
     * Constructor instantiates a new MainViewModel with default values.
     */
    public MainViewModel() {
        homeFragment = new HomeFragment();
        entriesFragment = new EntriesFragment();
        settingsFragment = new SettingsFragment();
        selectedItem = R.id.menu_home;
        openedEntryAdapterPosition = -1;
    }


    /**
     * Method returns the home fragment.
     *
     * @return  Home fragment.
     */
    public HomeFragment getHomeFragment() {
        return homeFragment;
    }

    /**
     * Method returns the entries fragment.
     *
     * @return  Entries fragment.
     */
    public EntriesFragment getEntriesFragment() {
        return entriesFragment;
    }

    /**
     * Method returns the settings fragment.
     *
     * @return  Settings fragment.
     */
    public SettingsFragment getSettingsFragment() {
        return settingsFragment;
    }

    /**
     * Method returns the index of the currently selected fragment.
     *
     * @return  Index of the currently selected fragment.
     */
    public int getSelectedItem() {
        return selectedItem;
    }

    /**
     * Method changes the index of the currently selected fragment.
     *
     * @param selectedItem  Index of the new selected fragment.
     */
    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }

    /**
     * Method returns the adapter position of the entry that is currently opened from the MainActivity.
     *
     * @return  Adapter position of the entry currently opened.
     */
    public int getOpenedEntryAdapterPosition() {
        return openedEntryAdapterPosition;
    }

    /**
     * Method changes the adapter position of the entry that is currently opened from the MainActivity.
     *
     * @param openedEntryAdapterPosition    Adapter position for the entry currently opened.
     */
    public void setOpenedEntryAdapterPosition(int openedEntryAdapterPosition) {
        this.openedEntryAdapterPosition = openedEntryAdapterPosition;
    }


    /**
     * Method returns a list of all entries.
     *
     * @return  List of all entries.
     */
    public ArrayList<EntryAbbreviated> getAllEntries() {
        EntryManager.getInstance().sortByName(false);
        return EntryManager.getInstance().getData();
    }

}
