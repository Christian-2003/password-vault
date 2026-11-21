package de.christian2003.core.common.application.usecases

import android.net.Uri
import coil.request.ImageRequest
import de.christian2003.core.common.domain.services.WebRequestService
import javax.inject.Inject


/**
 * Use case to request an image over web.
 *
 * @param webRequestService Web request service performing the image request.
 */
class RequestImageUseCase @Inject constructor(
    private val webRequestService: WebRequestService
) {

    /**
     * Requests the image of the specified URL.
     *
     * @param url   URL of the image to request.
     * @return      Image request.
     */
    fun requestImage(url: Uri): ImageRequest {
        return webRequestService.requestImage(url)
    }

}
