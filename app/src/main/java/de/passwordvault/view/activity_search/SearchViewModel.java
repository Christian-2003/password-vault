package de.passwordvault.view.activity_search;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import java.lang.reflect.Array;
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

    @NonNull
    private ArrayList<SearchResult> searchResults;

    @NonNull
    private SearchHandler searchHandler;

    private boolean loading;

    private boolean loaded;



    public SearchViewModel() {
        searchResults = new ArrayList<>();
        searchHandler = new SearchHandler();
        loading = false;
        loaded = false;
    }


    @Nullable
    public ArrayList<SearchResult> getSearchResults() {
        if (!loaded) {
            return null;
        }
        return searchResults;
    }


    public void search(@Nullable Runnable callback, @NonNull String query, boolean force) {
        if (!loading) {
            if (!loaded || force) {
                loading = true;
                Thread thread = new Thread(() -> {
                    searchResults = searchHandler.search(query);
                    loaded = true;
                    loading = false;
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
