package de.passwordvault.model.search;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.model.tags.Tag;


/**
 * Class models a search result for an entry.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class SearchResultEntry extends SearchResult {

    /**
     * Attribute stores the entry of the search result.
     */
    @NonNull
    private final EntryAbbreviated entry;

    /**
     * Attribute stores a list of tags from the entry matching the search query.
     */
    @NonNull
    private final ArrayList<Tag> matchingTags;


    /**
     * Constructor instantiates a new search result for an entry.
     *
     * @param entry Entry for which to create the search result.
     */
    public SearchResultEntry(@NonNull EntryAbbreviated entry) {
        super(TYPE_ENTRY);
        this.entry = entry;
        matchingTags = new ArrayList<>();
    }


    /**
     * Method returns the entry of the search result.
     *
     * @return  Entry of the search result.
     */
    @NonNull
    public EntryAbbreviated getEntry() {
        return entry;
    }

    /**
     * Method returns the list of matching tags from the entry.
     *
     * @return  List of matching tags.
     */
    @NonNull
    public ArrayList<Tag> getMatchingTags() {
        return matchingTags;
    }

    /**
     * Method adds a matching tag to the search result.
     *
     * @param tag   Matching tag to add.
     */
    public void addMatchingTag(Tag tag) {
        matchingTags.add(tag);
    }

}
