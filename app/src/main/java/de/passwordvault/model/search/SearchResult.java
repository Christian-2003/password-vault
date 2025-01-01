package de.passwordvault.model.search;


/**
 * Class models a search result. All search results must extend this class.
 *
 * @author  Christian-2003
 * @version 3.7.1
 */
public class SearchResult {

    /**
     * Field stores the type for entry search results used with {@link SearchResultEntry}.
     */
    public static final int TYPE_ENTRY = 0;

    /**
     * Field stores the type for detail search results used with {@link SearchResultDetail}.
     */
    public static final int TYPE_DETAIL = 2;

    /**
     * Field stores the priority to use when the entry tag matches the query.
     */
    public static final int PRIORITY_MATCHING_ENTRY_TAG = 20;

    /**
     * Field stores the priority to use when the entry name matches the query.
     */
    public static final int PRIORITY_MATCHING_ENTRY_NAME = 10;

    /**
     * Field stores the priority to use when the entry description matches the query.
     */
    public static final int PRIORITY_MATCHING_ENTRY_DESCRIPTION = 5;

    /**
     * Field stores the priority to use when the detail name matches the query.
     */
    public static final int PRIORITY_MATCHING_DETAIL_NAME = 8;

    /**
     * Field stores the priority to use when the detail content matches the query.
     */
    public static final int PRIORITY_MATCHING_DETAIL_CONTENT = 5;


    /**
     * Attribute stores the type of the search result.
     */
    private final int type;

    /**
     * Attribute stores the priority for the search result. Search results with a higher priority
     * shall be displayed higher within the list of search results.
     */
    private final int priority;


    /**
     * Constructor instantiates a new search result.
     *
     * @param type      Type for the search result.
     * @param priority  Priority for the search result.
     */
    public SearchResult(int type, int priority) {
        this.type = type;
        this.priority = priority;
    }


    /**
     * Method returns the type of the search result.
     *
     * @return  Type of the search result.
     */
    public int getType() {
        return type;
    }

    /**
     * Method returns the priority of the search result.
     *
     * @return  Priority of the search result.
     */
    public int getPriority() {
        return priority;
    }

}
