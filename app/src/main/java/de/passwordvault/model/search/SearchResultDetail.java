package de.passwordvault.model.search;

import androidx.annotation.NonNull;
import de.passwordvault.model.detail.Detail;
import de.passwordvault.model.entry.EntryAbbreviated;


/**
 * Class models a search result for a detail.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class SearchResultDetail extends SearchResult {

    /**
     * Detail of the search result.
     */
    @NonNull
    private final Detail detail;

    /**
     * Entry of which the detail is a part of.
     */
    @NonNull
    private final EntryAbbreviated entry;


    /**
     * Constructor instantiates a new search result for the specified detail.
     *
     * @param detail    Detail for which to create the search result.
     * @param entry     Entry of which the detail is a part of.
     */
    public SearchResultDetail(@NonNull Detail detail, @NonNull EntryAbbreviated entry) {
        super(TYPE_DETAIL);
        this.detail = detail;
        this.entry = entry;
    }


    /**
     * Method returns the detail of the search result.
     *
     * @return  Detail of the search result.
     */
    @NonNull
    public Detail getDetail() {
        return detail;
    }

    /**
     * Method returns the entry of which the detail is a part of.
     *
     * @return  Entry of which the detail is a part of.
     */
    @NonNull
    public EntryAbbreviated getEntry() {
        return entry;
    }

}
