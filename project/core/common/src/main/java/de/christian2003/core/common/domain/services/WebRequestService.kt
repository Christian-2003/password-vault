package de.christian2003.core.common.domain.services

import android.net.Uri
import coil.ImageLoader
import coil.request.ImageRequest
import de.christian2003.core.common.domain.entities.WebResponse
import de.christian2003.core.common.domain.exceptions.WebRequestFailedException


/**
 * Service through which to interact with the web.
 */
interface WebRequestService {

    /**
     * Performs a request to the specified web URL endpoint.
     *
     * @param url                           URL of the web endpoint.
     * @return                              Response.
     * @throws WebRequestFailedException    The web request failed.
     */
    suspend fun request(url: String): WebResponse


    /**
     * Requests the image of the specified URL.
     *
     * @param url   URL of the image to request.
     * @return      Image request.
     */
    fun requestImage(url: Uri): ImageRequest


    /**
     * Retrieves an image loader used to load SVG images.
     *
     * @return SVG image loader.
     */
    fun getSvgImageLoader(): ImageLoader

}
