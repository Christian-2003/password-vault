package de.passwordvault.view.activity_main;

import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.model.entry.EntryManager;


/**
 * Class models the {@linkplain ViewModel} for the {@linkplain MainActivity} which stores all data
 * that shall be persistent throughout activity changes.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class MainViewModel extends ViewModel {

    /**
     * Attribute stores the adapter position of an entry that is currently opened from the MainActivity.
     */
    private int openedEntryAdapterPosition;


    /**
     * Constructor instantiates a new MainViewModel with default values.
     */
    public MainViewModel() {
        openedEntryAdapterPosition = -1;
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
