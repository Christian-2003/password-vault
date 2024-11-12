package de.passwordvault.view.activity_search;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import de.passwordvault.model.search.SearchHandler;
import de.passwordvault.model.search.SearchResult;


/**
 * Class implements the view model for the {@link SearchActivity}.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class SearchViewModel extends ViewModel {

    /**
     * Attribute stores the list of search results.
     */
    @NonNull
    private ArrayList<SearchResult> searchResults;

    /**
     * Attribute stores the handler used to perform searches.
     */
    @NonNull
    private final SearchHandler searchHandler;

    /**
     * Attribute stores whether the {@link #searchHandler} is currently searching.
     */
    private boolean searching;

    /**
     * Attribute stores whether the {@link #searchHandler} has finished searching.
     */
    private boolean finished;


    /**
     * Constructor instantiates a new view model.
     */
    public SearchViewModel() {
        searchResults = new ArrayList<>();
        searchHandler = new SearchHandler();
        searching = false;
        finished = false;
    }


    /**
     * Method returns a list of search results. If nothing has been searched so far,
     * {@code null} is returned.
     *
     * @return  List of search results.
     */
    @Nullable
    public ArrayList<SearchResult> getSearchResults() {
        if (!finished) {
            return null;
        }
        return searchResults;
    }

    /**
     * Method returns whether the search operation has finished.
     *
     * @return  Whether the search operation has finished.
     */
    public boolean isFinished() {
        return finished;
    }


    /**
     * Method searches the app for the specified query and invokes the callback after searching.
     *
     * @param callback  Callback to invoke after the search finishes.
     * @param query     Search query.
     * @param force     Whether to force search, even if searching has already finished.
     */
    public void search(@Nullable Runnable callback, @NonNull String query, boolean force) {
        if (!searching) {
            if (!finished || force) {
                searching = true;
                Thread thread = new Thread(() -> {
                    searchResults = searchHandler.search(query);
                    finished = true;
                    searching = false;
                    if (callback != null) {
                        callback.run();
                    }
                });
                thread.start();
            }
            else {
                if (callback != null) {
                    callback.run();
                }
            }
        }
    }

}
