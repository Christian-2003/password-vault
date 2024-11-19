package de.passwordvault.view.activity_main;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.model.storage.app.StorageException;


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

    /**
     * Attribute stores whether an update for the app is available.
     */
    private boolean updateAvailable;

    /**
     * Attribute stores whether the entries have been loaded.
     */
    private boolean loaded;

    /**
     * Attribute stores whether the entries are currently loading.
     */
    private boolean loading;


    /**
     * Constructor instantiates a new MainViewModel with default values.
     */
    public MainViewModel() {
        openedEntryAdapterPosition = -1;
        downloadWarningDismissed = false;
        updateAvailable = false;
        loaded = false;
        loading = false;
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

    /**
     * Method returns whether an update for the app is available.
     *
     * @return  Whether an update is available.
     */
    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    /**
     * Method informs the view model that an update is available.
     */
    public void updateAvailable() {
        updateAvailable = true;
    }


    public boolean isChangedSinceLastCacheGeneration() {
        return EntryManager.getInstance().isChangedSinceLastCacheGeneration();
    }


    /**
     * Method loads the entries from storage.
     *
     * @param callback  Callback to invoke after the entries are loaded.
     * @param force     Whether to force (re)load the entries after they were already loaded.
     */
    public void loadAllEntries(@Nullable Runnable callback, boolean force) {
        if (!loading) {
            if (!loaded || force) {
                loading = true;
                Thread thread = new Thread(() -> {
                    try {
                        EntryManager.getInstance().load();
                    }
                    catch (StorageException e) {
                        //We can ignore this exception and treat this case as if the user opens
                        //the app for the first time and has no data to load.
                    }
                    EntryManager.getInstance().sortByName(false);
                    EntryManager.getInstance().getData(); //Sorts data
                    loaded = true;
                    loading = false;
                    if (callback != null) {
                        callback.run();
                    }
                });
                thread.start();
                return;
            }
            if (callback != null) {
                callback.run();
            }
        }
    }


    /**
     * Method returns a list of all entries.
     *
     * @return  List of all entries.
     */
    @Nullable
    public ArrayList<EntryAbbreviated> getAllEntries() {
        if (!loaded) {
            return null;
        }
        return EntryManager.getInstance().getData();
    }

}
