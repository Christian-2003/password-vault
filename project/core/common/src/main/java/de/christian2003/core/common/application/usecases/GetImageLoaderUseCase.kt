package de.christian2003.core.common.application.usecases

import coil.ImageLoader
import de.christian2003.core.common.domain.services.WebRequestService
import javax.inject.Inject

/**
 * Use case to get an image loader.
 *
 * @param webRequestService Web request service used to get the image loader.
 */
class GetImageLoaderUseCase @Inject constructor(
    private val webRequestService: WebRequestService
) {

    /**
     * Retrieves an image loader used to load SVG images.
     *
     * @return SVG image loader.
     */
    fun getImageLoader(): ImageLoader {
        return webRequestService.getSvgImageLoader()
    }

}
