package de.passwordvault.view.settings.activity_help;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import de.passwordvault.model.rest.RestCallback;
import de.passwordvault.model.rest.RestError;
import de.passwordvault.model.rest.help.HelpRestClient;
import de.passwordvault.model.rest.help.LocalizedHelpPage;


/**
 * Class implements the view model for the {@link SettingsHelpActivity}.
 *
 * @author  Christian-2003
 * @version 3.7.2
 */
public class SettingsHelpViewModel extends ViewModel {

    /**
     * Attribute stores the REST client fetching data.
     */
    @NonNull
    private final HelpRestClient restClient;

    /**
     * Attribute stores the REST error generated when fetching data.
     */
    @Nullable
    private RestError error;


    /**
     * Constructor instantiates a new view model.
     */
    public SettingsHelpViewModel() {
        restClient = new HelpRestClient(null);
        error = null;
    }


    /**
     * Method returns the list of localized help pages fetched from the REST API.
     *
     * @return  List of localized help pages.
     */
    @NonNull
    public ArrayList<LocalizedHelpPage> getHelpPages() {
        return restClient.getHelpPages();
    }

    /**
     * Method returns whether the REST client is currently fetching data.
     *
     * @return  Whether the client is currently fetching data.
     */
    public boolean isFetching() {
        return restClient.isFetching();
    }

    /**
     * Method returns whether the REST client finished fetching data.
     *
     * @return  Whether the client finished fetching data.
     */
    public boolean isFinished() {
        return restClient.isFinished();
    }

    /**
     * Method returns the error generated while fetching data. This is {@code null} before the REST
     * client finished fetching data.
     *
     * @return  Error generated.
     */
    @Nullable
    public RestError getError() {
        return error;
    }

    /**
     * Method changes the error generated while fetching.
     *
     * @param error New error generated while fetching.
     */
    public void setError(@NonNull RestError error) {
        this.error = error;
    }

    /**
     * Method begins fetching data from the REST API.
     *
     * @param callback  Callback to invoke once the data is fetched.
     */
    public void fetchData(@Nullable RestCallback callback) {
        restClient.fetch(callback);
    }

}
