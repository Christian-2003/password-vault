package de.christian2003.feature.analysis.infrastructure.lookup

import android.content.res.AssetManager
import android.util.Log
import de.christian2003.feature.analysis.domain.entities.LookupType
import de.christian2003.feature.analysis.domain.repositories.LookupRepository
import javax.inject.Inject


/**
 * Repository that can be used to lookup words for the password analysis. Examples for looked-up
 * words are common passwords or dictionary words.
 *
 * @param assetManager  Asset manager used to load the asset files.
 */
internal class AssetLookupRepository @Inject constructor(
    private val assetManager: AssetManager
): LookupRepository {

    /**
    * Returns a set of words (can be up to 100k entries) from a lookup.
    *
    * @param type  Type of the lookup words to return.
    * @return      Set of words from the specified type.
    */
    override suspend fun getWords(type: LookupType): Set<String> {
        val assetFileName: String = when (type) {
            LookupType.DictionaryWords -> "dictionary_v1_en-US.txt"
            LookupType.CommonPasswords -> "passwords_v1_en-US.txt"
        }

        val result: MutableSet<String> = mutableSetOf()
        try {
            assetManager.open(assetFileName).bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    result.add(line)
                }
            }
        }
        catch (e: Exception) {
            Log.e("Asset Lookup", "Cannot lookup asset file '$assetFileName': ${e.message ?: "Unknown error"}")
        }

        return result
    }

}
