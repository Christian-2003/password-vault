package de.passwordvault.view.activity_main.fragment_entries;

import androidx.lifecycle.ViewModel;
import de.passwordvault.R;


/**
 * Class models a {@linkplain ViewModel} for the {@linkplain EntriesFragment} which contains all
 * relevant data that shall be persistent throughout activity changes.
 *
 * @author  Christian-2003
 * @version 2.2.0
 */
public class EntriesViewModel extends ViewModel {

    /**
     * Attribute stores whether the search bar is visible.
     */
    private boolean searchBarVisible;

    /**
     * Attribute stores the item number of the selected sorting.
     */
    private int selectedEntrySorting;


    /**
     * Constructor instantiates a new {@link EntriesViewModel} with default values for all
     * attributes.
     */
    public EntriesViewModel() {
        searchBarVisible = false;
        selectedEntrySorting = R.id.sort_entries_not_sorted;
    }


    public boolean isSearchBarVisible() {
        return searchBarVisible;
    }

    public void setSearchBarVisible(boolean searchBarVisible) {
        this.searchBarVisible = searchBarVisible;
    }

    public int getSelectedEntrySorting() {
        return selectedEntrySorting;
    }

    public void setSelectedEntrySorting(int selectedEntrySorting) throws IllegalArgumentException {
        if (selectedEntrySorting < 0) {
            throw new IllegalArgumentException(selectedEntrySorting + " is illegal, since index must be greater than 0");
        }
        this.selectedEntrySorting = selectedEntrySorting;
    }

}
