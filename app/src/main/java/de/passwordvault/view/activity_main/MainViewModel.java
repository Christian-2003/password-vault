package de.passwordvault.view.activity_main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.model.rest.RestCallback;
import de.passwordvault.model.rest.RestError;
import de.passwordvault.model.rest.legal.LegalPageDto;
import de.passwordvault.model.rest.legal.LegalRestClient;
import de.passwordvault.model.storage.app.StorageException;


/**
 * Class models the {@linkplain ViewModel} for the {@linkplain MainActivity} which stores all data
 * that shall be persistent throughout activity changes.
 *
 * @author  Christian-2003
 * @version 3.7.2
 */
public class MainViewModel extends ViewModel {

    /**
     * Attribute stores the tag used with the REST client fetching the privacy policy.
     */
    public static final String TAG_PRIVACY = "privacy";

    /**
     * Attribute stores the tag used with the REST client fetching the terms of service.
     */
    public static final String TAG_TOS = "tos";


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
     * Attribute stores the REST client used to fetch the privacy policy.
     */
    @NonNull
    private final LegalRestClient privacyRestClient;

    /**
     * Attribute stores the REST client used to fetch the terms of service.
     */
    @NonNull
    private final LegalRestClient tosRestClient;

    /**
     * Attribute stores the REST error occurred when fetching the privacy policy.
     */
    @Nullable
    private RestError privacyError;

    /**
     * Attribute stores the REST error occurred when fetching the terms of service.
     */
    @Nullable
    private RestError tosError;

    /**
     * Attribute indicates whether the legal page is displayed.
     */
    private boolean legalPageDisplayed;


    /**
     * Constructor instantiates a new MainViewModel with default values.
     */
    public MainViewModel() {
        openedEntryAdapterPosition = -1;
        downloadWarningDismissed = false;
        updateAvailable = false;
        loaded = false;
        loading = false;
        privacyRestClient = new LegalRestClient(TAG_PRIVACY, "privacy");
        tosRestClient = new LegalRestClient(TAG_TOS, "tos");
        privacyError = null;
        tosError = null;
        legalPageDisplayed = false;
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

    /**
     * Method indicates whether data has changed since last cache generation.
     *
     * @return  Whether data has changed since last cache generation.
     */
    public boolean isChangedSinceLastCacheGeneration() {
        return EntryManager.getInstance().isChangedSinceLastCacheGeneration();
    }

    /**
     * Method returns the error which occurred while fetching the privacy policy.
     *
     * @return  REST error.
     */
    @Nullable
    public RestError getPrivacyError() {
        return privacyError;
    }

    /**
     * Method changes the error which occurred while fetching the privacy policy.
     *
     * @param privacyError  REST error.
     */
    public void setPrivacyError(@Nullable RestError privacyError) {
        this.privacyError = privacyError;
    }

    /**
     * Method returns the error which occurred while fetching the terms of service.
     *
     * @return  REST error.
     */
    @Nullable
    public RestError getTosError() {
        return tosError;
    }

    /**
     * Method changes the error which occurred while fetching the terms of service.
     *
     * @param tosError  REST error.
     */
    public void setTosError(@Nullable RestError tosError) {
        this.tosError = tosError;
    }

    /**
     * Method returns whether the legal page is displayed.
     *
     * @return  Whether the legal page is displayed.
     */
    public boolean isLegalPageDisplayed() {
        return legalPageDisplayed;
    }

    /**
     * Method changes whether the legal page is displayed.
     *
     * @param legalPageDisplayed    Whether the legal page is displayed.
     */
    public void setLegalPageDisplayed(boolean legalPageDisplayed) {
        this.legalPageDisplayed = legalPageDisplayed;
    }

    /**
     * Method returns the DTO to pass to {@link de.passwordvault.view.activity_legal.LegalActivity}.
     *
     * @return  DTO to pass to another activity.
     */
    public LegalPageDto getPrivacyDto() {
        return privacyRestClient.toDto();
    }

    /**
     * Method returns the DTO to pass to {@link de.passwordvault.view.activity_legal.LegalActivity}.
     *
     * @return  DTO to pass to another activity.
     */
    public LegalPageDto getTosDto() {
        return tosRestClient.toDto();
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


    /**
     * Method fetches the data from the REST API.
     *
     * @param callback  Callback to invoke once the data has been fetched.
     */
    public void fetchRestData(@Nullable RestCallback callback) {
        privacyRestClient.fetch(callback);
        tosRestClient.fetch(callback);
    }

}
