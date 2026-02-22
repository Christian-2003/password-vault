package de.christian2003.feature.search.infrastructure.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.feature.search.domain.repositories.SearchConfigRepository
import javax.inject.Inject


/**
 * Implementation of the config repository for the search feature that uses shared preferences for
 * managing the config data.
 *
 * @param context   Application context.
 */
internal class AndroidSearchConfigRepository @Inject constructor(
    @ApplicationContext context: Context
): SearchConfigRepository {

    /**
     * Max number of recent queries.
     */
    private val recentQueryLimit: Int = 5

    /**
     * Preferences through which to store the config.
     */
    private val preferences: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)


    /**
     * Returns a list of the most recent queries.
     *
     * @return  List of the most recent queries.
     */
    override fun getRecentQueries(): List<String> {
        val recentQueries: MutableList<String> = mutableListOf()

        for (i: Int in 0 until recentQueryLimit) {
            val recentQuery: String? = preferences.getString("search_recent_$i", null)
            if (recentQuery == null) {
                break
            }
            recentQueries.add(recentQuery)
        }

        return recentQueries
    }


    /**
     * Adds a new most recent query.
     *
     * @param query New most recent query.
     */
    override fun addRecentQuery(query: String) {
        val recentQueries: List<String> = getRecentQueries()

        val max = if (recentQueryLimit < recentQueries.size) {
            recentQueryLimit
        } else {
            recentQueries.size
        }

        preferences.edit {
            putString("search_recent_0", query)
            for (i: Int in 0 until max) {
                putString("search_recent_${i + 1}", recentQueries[i])
            }
        }
    }


    /**
     * Removes all most recent queries.
     */
    override fun removeRecentQueries() {
        preferences.edit {
            for (i: Int in 0 until recentQueryLimit + 1) {
                remove("search_recent_$i")
            }
        }
    }

}
