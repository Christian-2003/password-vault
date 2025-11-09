package de.christian2003.passwordvault.application.usecases.tag

import de.christian2003.passwordvault.application.repository.TagRepository
import de.christian2003.passwordvault.domain.model.tag.Tag
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


/**
 * Use case to get all tags.
 *
 * @param tagRepository Repository to access the tags.
 */
class GetAllTagsUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {

    /**
     * Returns a list of all tags.
     *
     * @return  List of all tags.
     */
    fun getAllTags(): Flow<List<Tag>> {
        return tagRepository.getAllTags()
    }

}
