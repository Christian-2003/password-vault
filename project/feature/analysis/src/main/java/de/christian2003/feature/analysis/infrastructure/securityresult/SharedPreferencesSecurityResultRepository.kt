package de.christian2003.feature.analysis.infrastructure.securityresult

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.feature.analysis.domain.entities.SecurityResultOverview
import de.christian2003.feature.analysis.domain.repositories.SecurityResultRepository
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject


/**
 * Repository for security results.
 * This implementation uses "data" shared preferences to store the latest overview results.
 *
 * @param context   Application context.
 */
internal class SharedPreferencesSecurityResultRepository @Inject constructor(
    @ApplicationContext context: Context
): SecurityResultRepository {

    /**
     * Shared preferences used to store the security result overview.
     */
    private val preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE)

    /**
     * Max number of latest results to keep remembered.
     */
    private val maxNumberOfResults: Int = 64

    /**
     * Caches the latest security overview. Can be null if no result is cached.
     * First:   Result number
     * Second:  Result overview
     */
    private var latestSecurityResultOverviewCache: Pair<Int, SecurityResultOverview>? = null


    /**
     * Adds the specified security result overview to the repository.
     *
     * @param overview  Overview to add.
     */
    override fun addSecurityResultOverview(overview: SecurityResultOverview) {
        val minResult: Int = preferences.getInt("sr_min", -1)
        val maxResult: Int = preferences.getInt("sr_max", -1)
        val resultAsString: String = securityResultOverviewToString(overview)

        if (minResult == -1 || maxResult == -1) {
            //No results available so far:
            preferences.edit {
                putString("sr_result_0", resultAsString)
                putInt("sr_min", 0)
                putInt("sr_max", 0)
            }
        }
        else {
            //Results available:
            val newMax: Int = maxResult + 1

            if (maxResult - minResult < maxNumberOfResults) {
                //Max number of results not reached yet:
                preferences.edit {
                    putString("sr_result_$newMax", resultAsString)
                    putInt("sr_max", newMax)
                }
            }
            else {
                //Max number of results reached:
                val newMin: Int = minResult + 1
                preferences.edit {
                    putString("sr_result_$newMax", resultAsString)
                    putInt("sr_max", newMax)
                    putInt("sr_min", newMin)
                    remove("sr_result_$minResult")
                }
            }
        }
    }


    /**
     * Gets the latest security result overview. May be null if no overview is available.
     *
     * @return  Latest security result overview or null if unavailable.
     */
    override fun getLatestSecurityResultOverview(): SecurityResultOverview? {
        val maxResult: Int = preferences.getInt("sr_max", -1)

        //Try to use cache to determine result:
        val cache: Pair<Int, SecurityResultOverview>? = latestSecurityResultOverviewCache
        if (cache != null && maxResult == cache.first) {
            return cache.second
        }

        //No item cached or newer result available:
        if (maxResult != -1) {
            val resultString: String? = preferences.getString("sr_result_$maxResult", null)
            if (resultString != null) {
                val parts: List<String> = resultString.split(',')
                if (parts.isNotEmpty()) {
                    val timestampString: String? = parts.getOrNull(0)
                    val weakPasswordCountString: String? = parts.getOrNull(1)
                    val reusedPasswordCountString: String? = parts.getOrNull(2)
                    val analyzedAccountsCountString: String? = parts.getOrNull(3)

                    val timestampEpochSecond: Long = timestampString?.toLongOrNull() ?: 0

                    val timestamp: LocalDateTime = LocalDateTime.ofEpochSecond(timestampEpochSecond, 0, ZoneOffset.UTC)
                    val weakPasswordCount = weakPasswordCountString?.toIntOrNull() ?: -1
                    val reusedPasswordCount = reusedPasswordCountString?.toIntOrNull() ?: -1
                    val analyzedAccountsCount = analyzedAccountsCountString?.toIntOrNull() ?: -1

                    val overview = SecurityResultOverview(
                        timestamp = timestamp,
                        weakPasswordCount = weakPasswordCount,
                        reusedPasswordCount = reusedPasswordCount,
                        analyzedAccountsCount = analyzedAccountsCount
                    )

                    latestSecurityResultOverviewCache = Pair(maxResult, overview)
                    return overview
                }
            }
        }

        //Now result available:
        return null
    }


    /**
     * Clears the stored security result overviews.
     */
    override fun clear() {
        val minResult: Int = preferences.getInt("sr_min", -1)
        val maxResult: Int = preferences.getInt("sr_max", -1)

        if (minResult != -1 && maxResult != -1) {
            preferences.edit {
                remove("sr_min")
                remove("sr_max")
                for (i: Int in minResult..maxResult) {
                    remove("sr_result_$i")
                }
            }
        }
    }


    /**
     * Converts the provided overview to a string that can be stored.
     *
     * @param overview  Overview to convert.
     * @return          Converted overview.
     */
    private fun securityResultOverviewToString(overview: SecurityResultOverview): String {
        val epochSeconds: Long = overview.timestamp.toEpochSecond(ZoneOffset.UTC)

        val builder = StringBuilder()
        builder.append(epochSeconds)
        builder.append(',')
        builder.append(overview.weakPasswordCount)
        builder.append(',')
        builder.append(overview.reusedPasswordCount)
        builder.append(',')
        builder.append(overview.analyzedAccountsCount)

        return builder.toString()
    }

}
