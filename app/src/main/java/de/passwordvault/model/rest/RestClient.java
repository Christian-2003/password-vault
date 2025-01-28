package de.passwordvault.model.rest;

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
import de.passwordvault.App;


/**
 * Class implements a REST client which can fetch data from a REST API.
 *
 * @param <ResponseType>    Type which models the REST response.
 * @author                  Christian-2003
 * @version                 3.7.2
 */
public abstract class RestClient<ResponseType> {

    /**
     * Attribute stores the response fetched from the server.
     */
    @Nullable
    private ResponseType response;

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
    public RestClient() {
        error = null;
        fetching = false;
        finished = false;
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
     * Method fetches the data asynchronously.
     *
     * @param url                   URL of the REST endpoint to fetch.
     * @param clazz                 Class of the response type.
     * @param callback              Callback to invoke once the data has been fetched.
     * @param restPostFetchCallback Callback to invoke in order to perform some filtering after the
     *                              data has been fetched, but before "callback" is invoked.
     */
    public void fetch(@NonNull String url, @NonNull Class<ResponseType> clazz, @Nullable RestCallback callback, @Nullable RestPostFetchCallback<ResponseType> restPostFetchCallback) {
        if (fetching || finished) {
            if (callback != null) {
                callback.onFetchFinished(error);
            }
            return;
        }
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
                response = fetchData(url);
                if (response == null || response.isEmpty()) {
                    throw new NullPointerException("Response is null");
                }
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
            try {
                this.response = deserializeResponse(response, clazz);
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
            if (restPostFetchCallback != null && !restPostFetchCallback.onPostFetchCallback(this.response)) {
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
     * Method fetches the data from the server and returns the response string.
     *
     * @param url           URL to fetch.
     * @return              Response from the REST API.
     * @throws Exception    Data could not be fetched.
     */
    private String fetchData(@NonNull String url) throws Exception {
        URL uri = new URL(url);
        HttpURLConnection connection = (HttpURLConnection)uri.openConnection();
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
     * Method deserializes the response returned from the REST API.
     *
     * @param response      Response to deserialize.
     * @param clazz         Class type of the response type.
     * @return              Deserialized response.
     * @throws Exception    The response cannot be deserialized.
     */
    private ResponseType deserializeResponse(@NonNull String response, @NonNull Class<ResponseType> clazz) throws Exception {
        Gson gson = new Gson();
        return gson.fromJson(response, clazz);
    }

}
