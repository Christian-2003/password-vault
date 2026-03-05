package de.christian2003.feature.search.domain.repositories


/**
 * Repository for the configuration of the search feature.
 */
internal interface SearchConfigRepository {

    /**
     * Returns a list of the most recent queries.
     *
     * @return  List of the most recent queries.
     */
    fun getRecentQueries(): List<String>


    /**
     * Adds a new most recent query.
     *
     * @param query New most recent query.
     */
    fun addRecentQuery(query: String)


    /**
     * Removes all most recent queries.
     */
    fun removeRecentQueries()

}
