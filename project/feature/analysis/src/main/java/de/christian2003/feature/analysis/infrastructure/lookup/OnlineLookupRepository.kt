package de.christian2003.feature.analysis.infrastructure.lookup

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.feature.analysis.domain.entities.LookupType
import de.christian2003.feature.analysis.domain.repositories.LookupRepository
import de.christian2003.feature.analysis.infrastructure.lookup.dto.RestLookupFileDto
import de.christian2003.feature.analysis.infrastructure.lookup.dto.RestLookupRootDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.util.Locale
import javax.inject.Inject


/**
 * Implementation of the lookup repository which retrieves the lookup files from a webserver.
 * Once lookup words (i.e. common passwords or dictionary words) are requested, the lookup files are
 * downloaded from a webserver and stored inside the internal app storage for later use. If a new
 * version of the lookup files are available online, they are downloaded and replace the currently
 * downloaded files in the app storage.
 *
 * @param context   Application context.
 * @param client    HTTP client for web requests.
 */
internal class OnlineLookupRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val client: OkHttpClient
): LookupRepository {

    /**
     * Directory in which the lookup files are stored: "files/additionalContent/lookup"
     */
    private val dir = File(File(context.filesDir, "additionalContent"), "lookup")

    /**
     * Preferences stores metadata for the lookup files.
     */
    private val preferences: SharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE)


    /**
     * Returns a set of words (can be up to 100k entries) from a lookup.
     *
     * @param type  Type of the lookup words to return.
     * @return      Set of words from the specified type.
     */
    override suspend fun getWords(type: LookupType): Set<String> {
        val preferencesKeyPrefix: String = when (type) {
            LookupType.CommonPasswords -> "ac_lookup_pw"
            LookupType.DictionaryWords -> "ac_lookup_dic"
        }

        val downloadedVersion: Int = preferences.getInt("${preferencesKeyPrefix}_version", -1)
        val downloadedLanguage: String? = preferences.getString("${preferencesKeyPrefix}_lang", null)
        var downloadedFileName: String? = preferences.getString("${preferencesKeyPrefix}_file", null)

        val apiResponse: RestLookupRootDto? = fetchApiResponse()
        if (apiResponse == null) {
            return if (downloadedFileName != null) {
                getWordsFromFile(downloadedFileName, preferencesKeyPrefix)
            } else {
                setOf()
            }
        }

        val apiResponseFile: RestLookupFileDto? = findFileFromApiResponse(apiResponse)
        if (apiResponseFile == null) {
            return if (downloadedFileName != null) {
                getWordsFromFile(downloadedFileName, preferencesKeyPrefix)
            } else {
                setOf()
            }
        }

        val fileVersion: Int = apiResponseFile.version
        if (downloadedVersion < fileVersion || downloadedLanguage != apiResponseFile.language || downloadedFileName == null) {
            //File not downloaded or not up-to-date:
            val fileUrl: String = when (type) {
                LookupType.CommonPasswords -> apiResponseFile.passwordsUrl
                LookupType.DictionaryWords -> apiResponseFile.dictionaryUrl
            }
            val fileName: String? = downloadFile(fileUrl)
            if (fileName != null) {
                preferences.edit {
                    putInt("${preferencesKeyPrefix}_version", fileVersion)
                    putString("${preferencesKeyPrefix}_lang", apiResponseFile.language)
                    putString("${preferencesKeyPrefix}_file", fileName)
                }
                downloadedFileName = fileName
            }
        }

        if (downloadedFileName == null) {
            //File cannot be downloaded at the moment:
            return setOf()
        }

        val downloadedFile = File(dir, downloadedFileName)
        if (!downloadedFile.exists()) {
            cleanupFileMetadata(preferencesKeyPrefix)
            return setOf()
        }

        return getWordsFromFile(downloadedFileName, preferencesKeyPrefix)
    }


    /**
     * Returns the words from the specified file.
     *
     * @param fileName              Name of the lookup file whose words to return.
     * @param preferencesKeyPrefix  Prefix for the preferences metadata keys required to clear
     *                              metadata if the file is unavailable.
     * @return                      Set of all words from the lookup file.
     */
    private fun getWordsFromFile(fileName: String, preferencesKeyPrefix: String): Set<String> {
        val downloadedFile = File(dir, fileName)
        if (!downloadedFile.exists()) {
            cleanupFileMetadata(preferencesKeyPrefix)
            return setOf()
        }

        val result: MutableSet<String> = mutableSetOf()
        downloadedFile.inputStream().bufferedReader().useLines { lines ->
            lines.forEach { line ->
                result.add(line)
            }
        }

        return result
    }


    /**
     * Cleanups file metadata.
     *
     * @param preferencesKeyPrefix  Prefix for the preferences keys.
     */
    private fun cleanupFileMetadata(preferencesKeyPrefix: String) {
        preferences.edit {
            remove("${preferencesKeyPrefix}_version")
            remove("${preferencesKeyPrefix}_lang")
            remove("${preferencesKeyPrefix}_file")
        }
    }


    /**
     * Fetches the API response from the webserver.
     *
     * @return  Response from the webserver or null if the request fails.
     */
    private suspend fun fetchApiResponse(): RestLookupRootDto? = withContext(Dispatchers.IO) {
        try {
            val httpRequest: Request = Request.Builder()
                .url("https://api.christian2003.de/v1/passwordvault/security/lookup")
                .build()

            val httpResponse: Response = client.newCall(httpRequest).execute()
            val responseString: String? = httpResponse.body?.string()

            if (responseString != null) {
                val json: Json = Json { }
                val apiResponse: RestLookupRootDto = json.decodeFromString<RestLookupRootDto>(responseString)
                return@withContext apiResponse
            }
        }
        catch (e: Exception) {
            Log.e("Online Lookup", "Cannot reach API endpoint: ${e.message ?: "Unknown error"}")
        }

        return@withContext null
    }


    /**
     * Depending on the locale used by the app, the corresponding file is determined in the provided
     * API response and then returned. If no file can be found in the API response, null is returned.
     *
     * @param apiResponse   API response in which to search for the file based on language.
     * @return              File with the corresponding language or null.
     */
    private fun findFileFromApiResponse(apiResponse: RestLookupRootDto): RestLookupFileDto? {
        val appLocale: Locale = context.resources.configuration.locales.get(0)

        apiResponse.files.forEach { fileDto ->
            try {
                val fileLocale: Locale = Locale.forLanguageTag(fileDto.language)
                if (fileLocale == appLocale) {
                    return fileDto
                }
            }
            catch (e: Exception) {
                Log.e("Online Lookup", "Cannot find file in API response for locale ${fileDto.language}: ${e.message ?: "Unknown error"}")
            }
        }

        return null
    }


    /**
     * Downloads the file from the specified web URL and stores them in the internal app storage.
     * If a file with the same name as on the webserver already exists, it is replaced. If the file
     * cannot be downloaded, null is returned.
     *
     * @param url   URL of the file to download.
     * @return      The name of the file (e.g. "dictionary_v1_en-US.txt") inside the dir-folder or
     *              null if the file cannot be downloaded.
     */
    private suspend fun downloadFile(url: String): String? = withContext(Dispatchers.IO) {
        try {
            val httpRequest: Request = Request.Builder().url(url).build()
            val httpResponse: Response = client.newCall(httpRequest).execute()

            if (!httpResponse.isSuccessful) {
                return@withContext null
            }

            if (!dir.exists()) {
                dir.mkdirs()
            }

            val lastSeparatorIndex: Int = url.lastIndexOf('/')
            val fileName: String = if (lastSeparatorIndex >= 0 && lastSeparatorIndex < url.length - 1) {
                url.substring(lastSeparatorIndex)
            } else {
                url
            }

            val file = File(dir, fileName)
            if (file.exists()) {
                file.delete()
            }

            httpResponse.body?.byteStream()?.use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            return@withContext fileName
        }
        catch (e: Exception) {
            Log.e("Online Lookup", "Cannot download lookup files: ${e.message ?: "Unknown error"}")
            return@withContext null
        }
    }

}
