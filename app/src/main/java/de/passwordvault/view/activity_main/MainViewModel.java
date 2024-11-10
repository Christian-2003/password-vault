package de.passwordvault.view.activity_main;

import androidx.lifecycle.ViewModel;
import java.util.ArrayList;

import de.passwordvault.model.UpdateManager;
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
     * Attribute stores whether the warning to download a new version is dismissed.
     */
    private boolean downloadWarningDismissed;

    private boolean updateAvailable;


    /**
     * Constructor instantiates a new MainViewModel with default values.
     */
    public MainViewModel() {
        openedEntryAdapterPosition = -1;
        downloadWarningDismissed = false;
        updateAvailable = false;
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
     * Method returns whether the download warning is dismissed.
     *
     * @return  Whether the download warning is dismissed.
     */
    public boolean isDownloadWarningDismissed() {
        return downloadWarningDismissed;
    }

    /**
     * Method dismisses the download warning.
     */
    public void dismissDownloadWarning() {
        downloadWarningDismissed = true;
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public void updateAvailable() {
        updateAvailable = true;
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
