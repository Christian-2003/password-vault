package de.passwordvault.model.search;


/**
 * Class models a search result. All search results must extend this class.
 *
 * @author  Christian-2003
 * @version 3.7.0
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
     * Attribute stores the type of the search result.
     */
    private final int type;


    /**
     * Constructor instantiates a new search result.
     *
     * @param type  Type for the search result.
     */
    public SearchResult(int type) {
        this.type = type;
    }


    /**
     * Method returns the type of the search result.
     *
     * @return  Type of the search result.
     */
    public int getType() {
        return type;
    }

}
