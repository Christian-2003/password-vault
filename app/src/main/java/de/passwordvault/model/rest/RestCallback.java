package de.passwordvault.model.rest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * Interface models the callback invoked once a REST API call is finished.
 *
 * @author  Christian-2003
 * @version 3.7.2
 */
public interface RestCallback {

    /**
     * Method is called once the REST client finishes the API call.
     *
     * @param tag   Tag used with the REST client.
     * @param error Error generated during the call to the REST API.
     */
    void onFetchFinished(@Nullable String tag, @NonNull RestError error);

}
