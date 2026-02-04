package de.christian2003.data.accounts.application.usecases

import de.christian2003.data.accounts.domain.entities.Tag
import de.christian2003.data.accounts.domain.repositories.TagRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


/**
 * Use case to get all tags.
 *
 * @param tagRepository Repository to access the tags.
 */
class GetAllTagsUseCase @Inject internal constructor(
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
