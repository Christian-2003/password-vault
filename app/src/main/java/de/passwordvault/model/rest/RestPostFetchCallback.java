package de.passwordvault.model.rest;

import androidx.annotation.Nullable;


/**
 * Interface can be implemented by a class in order to perform some action after data has been
 * fetched from a REST API, but before a set callback is invoked.
 *
 * @param <ResponseType>    Type which models the response from the REST server.
 * @author                  Christian-2003
 * @version                 3.7.2
 */
public interface RestPostFetchCallback<ResponseType> {

    boolean onPostFetchCallback(@Nullable ResponseType response);

}
