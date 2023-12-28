package de.passwordvault.view.viewmodel;

import androidx.lifecycle.ViewModel;

import de.passwordvault.view.fragments.EntriesFragment;


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
     * Constructor instantiates a new {@link EntriesViewModel} with default values for all
     * attributes.
     */
    public EntriesViewModel() {
        searchBarVisible = false;
    }


    public boolean isSearchBarVisible() {
        return searchBarVisible;
    }

    public void setSearchBarVisible(boolean searchBarVisible) {
        this.searchBarVisible = searchBarVisible;
    }

}
