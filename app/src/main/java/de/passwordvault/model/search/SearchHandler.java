package de.passwordvault.model.search;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import de.passwordvault.model.detail.Detail;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.model.entry.EntryExtended;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.model.tags.Tag;


/**
 * Class implements the search handler which searches all entries and details for a
 * specified search query.
 * The generated list of search results contains instances of {@link SearchResultEntry}.
 * If details match the search query, the list contains instances of {@link SearchResultDetail}
 * immediately after the instance of {@link SearchResultEntry} of which the detail is a member.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class SearchHandler {

    /**
     * Attribute stores the list of search results.
     */
    @NonNull
    private final ArrayList<SearchResult> searchResults;

    /**
     * Attribute stores the search query.
     */
    private String query;


    /**
     * Constructor instantiates a new search handler.
     */
    public SearchHandler() {
        searchResults = new ArrayList<>();
    }


    /**
     * Method searches all entries and details for the specified query.
     *
     * @param query Query for which to search.
     * @return      List of search results.
     */
    public ArrayList<SearchResult> search(String query) {
        this.query = query.toLowerCase();
        searchResults.clear();

        for (EntryAbbreviated entryAbbreviated : EntryManager.getInstance().getData()) {
            boolean entryAdded = addEntryToSearchResults(entryAbbreviated);
            EntryExtended entryExtended = EntryManager.getInstance().get(entryAbbreviated.getUuid(), false);
            if (entryExtended != null) {
                for (Detail detail : entryExtended.getDetails()) {
                    boolean detailAdded = addDetailToSearchResults(detail, entryAbbreviated, entryAdded);
                    if (detailAdded) {
                        entryAdded = true;
                    }
                }
            }
        }

        return searchResults;
    }


    /**
     * Method adds the specified entry to the list of search results if it matches the query.
     *
     * @param entry Entry to add to the search results if it matches the query.
     * @return      Whether the entry was added to the search results.
     */
    private boolean addEntryToSearchResults(EntryAbbreviated entry) {
        SearchResultEntry searchResult = null;
        if (entry.getName().toLowerCase().contains(query) || entry.getDescription().toLowerCase().contains(query)) {
            searchResult = new SearchResultEntry(entry);
        }
        for (Tag tag : entry.getTags()) {
            if (tag.getName().toLowerCase().contains(query)) {
                if (searchResult == null) {
                    searchResult = new SearchResultEntry(entry);
                }
                searchResult.addMatchingTag(tag);
            }
        }

        if (searchResult != null) {
            searchResults.add(searchResult);
        }
        return searchResult != null;
    }


    /**
     * Method adds the specified detail to the list of search results, if it matches the query.
     *
     * @param detail                Detail to add to the search results if it matches the query.
     * @param entry                 Entry of which the detail is a member.
     * @param entryAddedBeforehand  Whether the passed entry was added to the search results before
     *                              this method is called.
     * @return                      Whether the detail is added to the search results.
     */
    private boolean addDetailToSearchResults(Detail detail, EntryAbbreviated entry, boolean entryAddedBeforehand) {
        SearchResultDetail searchResult = null;
        if (detail.getName().toLowerCase().contains(query) || (!detail.isObfuscated() && detail.getContent().toLowerCase().contains(query))) {
            searchResult = new SearchResultDetail(detail, entry);
        }

        if (searchResult != null) {
            if (!entryAddedBeforehand) {
                searchResults.add(new SearchResultEntry(entry));
            }
            searchResults.add(searchResult);
        }
        return searchResult != null;
    }

}
