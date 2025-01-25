package de.passwordvault.model.rest.help;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import de.passwordvault.App;
import de.passwordvault.R;
import de.passwordvault.model.rest.RestCallback;
import de.passwordvault.model.rest.RestError;


/**
 * Class implements the REST client which can make calls to the REST API for help pages.
 *
 * @author  Christian-2003
 * @version 3.7.2
 */
public class HelpRestClient {

    /**
     * Field stores the URL of the REST API endpoint.
     */
    private static final String URL = "https://api.passwordvault.christian2003.de/data/help.json";


    /**
     * Attribute stores the list of localized help pages fetched from the server.
     */
    private final ArrayList<LocalizedHelpPage> helpPages;

    /**
     * Attribute stores the error code generated during the response.
     */
    private RestError error;

    /**
     * Attribute indicates whether the API call is currently underway.
     */
    private boolean fetching;

    /**
     * Attribute indicates whether the API call has finished.
     */
    private boolean finished;


    /**
     * Constructor instantiates a new REST client.
     */
    public HelpRestClient() {
        helpPages = new ArrayList<>();
        error = null;
        fetching = false;
        finished = false;
    }


    /**
     * Method returns a list of all localized help pages.
     *
     * @return  List of help pages.
     */
    @NonNull
    public ArrayList<LocalizedHelpPage> getHelpPages() {
        return helpPages;
    }

    /**
     * Method returns whether the REST client is currently fetching data.
     *
     * @return  Whether the client is currently fetching data.
     */
    public boolean isFetching() {
        return fetching;
    }

    /**
     * Method returns whether the REST client has finished fetching data.
     *
     * @return  Whether the client has finished fetching data.
     */
    public boolean isFinished() {
        return finished;
    }


    /**
     * Method fetches the data from the REST API.
     *
     * @param callback  Callback to invoke once the client finishes fetching data.
     */
    public void fetchData(@Nullable RestCallback callback) {
        if (fetching || finished) {
            if (callback != null) {
                callback.onFetchFinished(error);
            }
            return;
        }
        fetching = true;
        Thread thread = new Thread(() -> {
            //Test internet availability:
            if (!hasInternet()) {
                error = RestError.ERROR_INTERNET;
                finished = true;
                fetching = false;
                if (callback != null) {
                    callback.onFetchFinished(error);
                }
                return;
            }

            //Fetch data:
            String response = null;
            try {
                response = getResult();
            }
            catch (Exception e) {
                error = RestError.ERROR_404;
                finished = true;
                fetching = false;
                if (callback != null) {
                    callback.onFetchFinished(error);
                }
                return;
            }

            //Deserialize:
            HelpResponse helpResponse = null;
            try {
                helpResponse = deserializeResponse(response);
            }
            catch (Exception e) {
                error = RestError.ERROR_SERIALIZATION;
                finished = true;
                fetching = false;
                if (callback != null) {
                    callback.onFetchFinished(error);
                }
                return;
            }

            //Filter data:
            try {
                filterResponse(helpResponse);
            }
            catch (Exception e) {
                error = RestError.ERROR_SERIALIZATION;
                finished = true;
                fetching = false;
                if (callback != null) {
                    callback.onFetchFinished(error);
                }
                return;
            }

            //Success:
            error = RestError.SUCCESS;
            finished = true;
            fetching = false;
            if (callback != null) {
                callback.onFetchFinished(error);
            }
        });
        thread.start();
    }


    /**
     * Method fetches the data from the server and returns the response string.
     *
     * @return              Response from the REST API.
     * @throws Exception    Data could not be fetched.
     */
    private String getResult() throws Exception {
        URL url = new URL(URL);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        StringBuilder result = new StringBuilder();

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
        }
        return result.toString();
    }


    /**
     * Method tests whether the REST client has access to the internet.
     *
     * @return  Whether the client has access to the internet.
     */
    private boolean hasInternet() {
        try {
            ConnectivityManager manager = (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkCapabilities networkCapabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
            if (networkCapabilities == null || !networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                return false;
            }
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }


    /**
     * Method deserializes the response returned from the REST API.
     *
     * @param response      Response to deserialize.
     * @return              Deserialized response.
     * @throws Exception    The response cannot be deserialized.
     */
    private HelpResponse deserializeResponse(String response) throws Exception {
        if (response == null) {
            throw new NullPointerException("Response is null");
        }
        Gson gson = new Gson();
        return gson.fromJson(response, HelpResponse.class);
    }


    /**
     * Method filters the deserialized response to only include the most necessary data that can
     * be displayed to the user.
     *
     * @param response      Deserialized response to filter.
     * @throws Exception    Response cannot be deserialized.
     */
    private void filterResponse(HelpResponse response) throws Exception {
        if (response == null) {
            throw new NullPointerException("HelpResponse is null");
        }

        String locale = App.getContext().getString(R.string.settings_help_locale);
        String defaultLocale = App.getContext().getString(R.string.settings_help_locale_default);
        helpPages.clear();

        for (HelpPage helpPage : response.getHelpPages()) {
            boolean pageAdded = false;

            //Find help page for locale:
            for (LocalizedHelpPage localizedHelpPage : helpPage.getLocalizedHelpPages()) {
                if (localizedHelpPage.getLanguage().equals(locale)) {
                    helpPages.add(localizedHelpPage);
                    pageAdded = true;
                    break;
                }
            }

            if (!pageAdded) {
                //Find english help page:
                for (LocalizedHelpPage localizedHelpPage : helpPage.getLocalizedHelpPages()) {
                    if (localizedHelpPage.getLanguage().equals(defaultLocale)) {
                        helpPages.add(localizedHelpPage);
                        pageAdded = true;
                        break;
                    }
                }
            }

            if (!pageAdded && helpPage.getLocalizedHelpPages().length != 0) {
                //Add first help page since no other help page is available:
                helpPages.add(helpPage.getLocalizedHelpPages()[0]);
            }
        }
    }

}
