package de.christian2003.passwordvault.presentation.viewmodels

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.core.common.application.usecases.GetImageLoaderUseCase
import de.christian2003.core.common.application.usecases.RequestImageUseCase
import de.christian2003.core.ui.model.HelpCard
import de.christian2003.passwordvault.application.GetHelpPagesUseCase
import de.christian2003.passwordvault.domain.entities.HelpPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * View model for the screen through which users can view all available help pages.
 *
 * @param application           Application.
 * @param getHelpPagesUseCase   Use case to get a list of all help pages available.
 * @param getImageLoaderUseCase Use case to get an image loader.
 * @param requestImageUseCase   Use case to request a web image.
 */
@HiltViewModel
internal class HelpPagesViewModel @Inject constructor(
    application: Application,
    getHelpPagesUseCase: GetHelpPagesUseCase,
    getImageLoaderUseCase: GetImageLoaderUseCase,
    private val requestImageUseCase: RequestImageUseCase,
): AndroidViewModel(application) {

    /**
     * Indicates whether the help card is visible.
     */
    var isHelpCardVisible: Boolean by mutableStateOf(HelpCard.HelpPages.getVisible(application))

    /**
     * Image loader to load preview images for the help pages.
     */
    val imageLoader: ImageLoader = getImageLoaderUseCase.getImageLoader()

    /**
     * List of help pages available to the user.
     */
    val helpPages: MutableList<HelpPage> = mutableStateListOf()

    /**
     * Indicates whether the page info is currently being fetched.
     */
    var isLoading: Boolean by mutableStateOf(true)
        private set


    /**
     * Initializes the view model.
     */
    init {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            val pages: List<HelpPage> = getHelpPagesUseCase.getHelpPages()
            helpPages.clear()
            helpPages.addAll(pages)
            isLoading = false
        }
    }


    /**
     * Queries an image web request for the provided URL.
     *
     * @param url   URL to query.
     * @return      Image request.
     */
    fun queryImageRequest(url: Uri): ImageRequest {
        return requestImageUseCase.requestImage(url)
    }


    /**
     * Dismisses the help card.
     */
    fun dismissHelpCard() {
        HelpCard.HelpPages.setVisible(application, false)
        isHelpCardVisible = false
    }

}
