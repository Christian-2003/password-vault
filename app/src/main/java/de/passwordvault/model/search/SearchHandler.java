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
 * @version 3.7.1
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

        reorderAccordingToPriority(searchResults);

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
        int priority = 0;
        ArrayList<Tag> matchingTags = new ArrayList<>();
        if (entry.getName().toLowerCase().contains(query)) {
            priority += SearchResult.PRIORITY_MATCHING_ENTRY_NAME;
        }
        if (entry.getDescription().toLowerCase().contains(query)) {
            priority += SearchResult.PRIORITY_MATCHING_ENTRY_DESCRIPTION;
        }
        for (Tag tag : entry.getTags()) {
            if (tag.getName().toLowerCase().contains(query)) {
                if (matchingTags.isEmpty()) {
                    priority += SearchResult.PRIORITY_MATCHING_ENTRY_TAG;
                }
                matchingTags.add(tag);
            }
        }
        if (priority != 0) {
            searchResult = new SearchResultEntry(entry, priority);
            for (Tag tag : matchingTags) {
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
        int priority = 0;
        if (detail.getName().toLowerCase().contains(query)) {
            priority += SearchResult.PRIORITY_MATCHING_DETAIL_NAME;
        }
        if (!detail.isObfuscated() && detail.getContent().toLowerCase().contains(query)) {
            priority += SearchResult.PRIORITY_MATCHING_DETAIL_CONTENT;
        }
        if (priority != 0) {
            searchResult = new SearchResultDetail(detail, entry);
        }

        if (searchResult != null) {
            if (!entryAddedBeforehand) {
                searchResults.add(new SearchResultEntry(entry, priority));
            }
            searchResults.add(searchResult);
        }
        return searchResult != null;
    }


    /**
     * Method uses a quicksort algorithm to reorder the passed list of search results based on their
     * priority. Afterwards, search results with a high priority will be placed at the beginning of
     * the list while search results with a low priority are placed at the end.
     *
     * @param list  List to sort.
     */
    private void reorderAccordingToPriority(@NonNull ArrayList<SearchResult> list) {
        if (list.isEmpty()) {
            return;
        }
        ArrayList<SearchResult> leftBlocks = new ArrayList<>();
        ArrayList<SearchResult> rightBlocks = new ArrayList<>();
        ArrayList<SearchResult> pivotBlocks = new ArrayList<>();

        //Determine pivot blocks:
        pivotBlocks.add(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i) instanceof SearchResultDetail) {
                pivotBlocks.add(list.get(i));
            }
            else {
                break;
            }
        }
        int pivotPriority = pivotBlocks.get(0).getPriority();

        //Divide list into left or right:
        for (int i = pivotBlocks.size(); i < list.size(); i++) {
            //Insert first item of current block (entry search result) into left or right:
            SearchResult searchResult = list.get(i);
            boolean left;
            if (searchResult.getPriority() > pivotPriority) {
                left = true;
                leftBlocks.add(searchResult);
            }
            else {
                left = false;
                rightBlocks.add(searchResult);
            }

            //Insert other items of block (detail search results) into left or right:
            if (++i < list.size()) {
                searchResult = list.get(i);
                while (searchResult instanceof SearchResultDetail) {
                    if (left) {
                        leftBlocks.add(searchResult);
                    }
                    else {
                        rightBlocks.add(searchResult);
                    }
                    if (++i < list.size()) {
                        searchResult = list.get(i);
                    }
                    else {
                        break;
                    }
                }
                i--;
            }
        }

        reorderAccordingToPriority(leftBlocks);
        reorderAccordingToPriority(rightBlocks);
        list.clear();
        list.addAll(leftBlocks);
        list.addAll(pivotBlocks);
        list.addAll(rightBlocks);
    }

}
