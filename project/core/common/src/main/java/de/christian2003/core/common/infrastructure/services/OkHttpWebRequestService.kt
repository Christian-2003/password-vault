package de.christian2003.core.common.infrastructure.services

import android.content.Context
import android.net.Uri
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.core.common.domain.entities.WebResponse
import de.christian2003.core.common.domain.exceptions.WebRequestFailedException
import de.christian2003.core.common.domain.services.WebRequestService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import javax.inject.Inject


/**
 * Service through which to interact with the web.
 *
 * This implementation uses OkHttp for web requests.
 */
internal class OkHttpWebRequestService @Inject constructor(
    @param:ApplicationContext private val context: Context
): WebRequestService {

    /**
     * OkHttp client used for all web requests.
     */
    private val okHttpClient: OkHttpClient = createOkHttpClient()

    /**
     * Caches image requests.
     */
    private val imageRequestCache: MutableMap<Uri, ImageRequest> = mutableMapOf()

    /**
     * Image loader that can be used to load SVG images.
     */
    private var imageLoader: ImageLoader? = null


    /**
     * Performs a request to the specified web URL endpoint.
     *
     * @param url                           URL of the web endpoint.
     * @return                              Response.
     * @throws WebRequestFailedException    The web request failed.
     */
    override suspend fun request(url: String): WebResponse = withContext(Dispatchers.IO) {
        try {
            //Build request:
            val httpRequest: Request = Request.Builder()
                .url(url)
                .build()

            //Fetch response:
            val httpResponse: Response = okHttpClient
                .newCall(httpRequest)
                .execute()

            //Handle response:
            val response = WebResponse(
                content = httpResponse.body?.string() ?: "",
                statusCode = httpResponse.code
            )

            return@withContext response
        }
        catch (e: Exception) {
            throw WebRequestFailedException(e.message ?: "Undefined exception")
        }
    }


    /**
     * Requests the image of the specified URL.
     *
     * @param url   URL of the image to request.
     * @return      Image request.
     */
    override fun requestImage(url: Uri): ImageRequest {
        var request: ImageRequest? = imageRequestCache[url]
        if (request != null) {
            return request
        }

        request = ImageRequest.Builder(context)
            .data(url)
            .build()

        imageRequestCache[url] = request
        return request
    }


    /**
     * Retrieves an image loader used to load SVG images.
     *
     * @return SVG image loader.
     */
    override fun getSvgImageLoader(): ImageLoader {
        var imageLoader: ImageLoader? = imageLoader
        if (imageLoader != null) {
            //Can reuse existing loader:
            return imageLoader
        }

        //Create new loader:
        imageLoader = ImageLoader.Builder(context)
            .okHttpClient(okHttpClient)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()

        this.imageLoader = imageLoader
        return imageLoader
    }


    /**
     * Creates a new OkHttp client that can be used for web requests.
     *
     * @return  OkHttp client.
     */
    private fun createOkHttpClient(): OkHttpClient {
        val cacheSize: Long = 10L * 1024 * 1024 //10 MB
        val cacheDir = File(context.applicationContext.cacheDir, "http")

        val client: OkHttpClient = OkHttpClient.Builder()
            .cache(Cache(cacheDir, cacheSize))
            .addNetworkInterceptor { chain ->
                val request: Request = chain.request()
                val response: Response = chain.proceed(request)
                val maxAge = 900 //900 seconds = 15 minutes

                response.newBuilder()
                    .header("Cache-Control", "public, max-age=$maxAge")
                    .build()
            }
            .build()

        return client
    }

}
