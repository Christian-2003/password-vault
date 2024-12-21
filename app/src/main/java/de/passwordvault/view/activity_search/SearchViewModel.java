package de.passwordvault.view.activity_search;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
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
 * @version 3.7.1
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
     * Attribute stores the query searched in lowercase. This is {@code null} before any query is
     * searched.
     */
    @Nullable
    private String lowercaseQuery;

    /**
     * Attribute stores whether the {@link #searchHandler} is currently searching.
     */
    private boolean searching;

    /**
     * Attribute stores whether the {@link #searchHandler} has finished searching.
     */
    private boolean finished;

    /**
     * Attribute stores the position of the first search result of the entry opened or
     * -1 if no entry is opened.
     */
    private int firstOpenedPosition;

    /**
     * Attribute stores the position of the last search result of the entry opened or
     * -1 if no entry is opened.
     */
    private int lastOpenedPosition;

    /**
     * Attribute stores the result code for the activity.
     */
    private int resultCode;


    /**
     * Constructor instantiates a new view model.
     */
    public SearchViewModel() {
        searchResults = new ArrayList<>();
        searchHandler = new SearchHandler();
        lowercaseQuery = null;
        searching = false;
        finished = false;
        firstOpenedPosition = -1;
        lastOpenedPosition = -1;
        resultCode = SearchActivity.RESULT_OK;
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
     * Method returns the position of the first search result of the entry opened or
     * -1 if no entry is opened.
     *
     * @return  Position of the first search result of the entry opened.
     */
    public int getFirstOpenedPosition() {
        return firstOpenedPosition;
    }

    /**
     * Method returns the position of the lat search result of the entry opened or
     * -1 if no entry is opened.
     *
     * @return  Position of the last search result of the entry opened.
     */
    public int getLastOpenedPosition() {
        return lastOpenedPosition;
    }

    /**
     * Method returns the result code for the activity.
     *
     * @return  Result code.
     */
    public int getResultCode() {
        return resultCode;
    }

    /**
     * Method changes the result code for the activity.
     *
     * @param resultCode    New result code.
     */
    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    /**
     * Method calculates {@link #firstOpenedPosition} and {@link #lastOpenedPosition} for the
     * entry whose search result at the passed position is opened.
     *
     * @param position  Position of the search result to open.
     */
    public void openEntry(int position) {
        if (position < 0) {
            this.firstOpenedPosition = -1;
            this.lastOpenedPosition = -1;
            return;
        }
        int first = 0;
        int last;

        //Determine first position:
        if (searchResults.get(position).getType() == SearchResult.TYPE_ENTRY) {
            first = position;
        }
        else {
            for (int i = position; i >= 0; i--) {
                if (searchResults.get(i).getType() == SearchResult.TYPE_ENTRY) {
                    first = i;
                    break;
                }
            }
        }

        //Determine last position:
        last = first;
        for (int i = first + 1; i < searchResults.size(); i++) {
            if (searchResults.get(i).getType() != SearchResult.TYPE_DETAIL) {
                last = i - 1;
                break;
            }
        }

        this.firstOpenedPosition = first;
        this.lastOpenedPosition = last;
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
                    this.lowercaseQuery = query.toLowerCase();
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


    /**
     * Method converts the passed string to a spannable highlighting the query entered by the user.
     *
     * @param s String to convert to a spannable.
     * @return  Spannable created from the passed string.
     */
    @NonNull
    public SpannableString toSpannable(@NonNull String s) {
        SpannableString spannable = new SpannableString(s);
        int index = -1;
        if (lowercaseQuery != null) {
            index = s.toLowerCase().indexOf(lowercaseQuery);
        }
        if (index != -1) {
            spannable.setSpan(new BackgroundColorSpan(Color.YELLOW), index, index + lowercaseQuery.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(Color.BLACK), index, index + lowercaseQuery.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

}
