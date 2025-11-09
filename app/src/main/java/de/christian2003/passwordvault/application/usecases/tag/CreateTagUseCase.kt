package de.christian2003.passwordvault.application.usecases.tag

import de.christian2003.passwordvault.application.repository.TagRepository
import de.christian2003.passwordvault.domain.model.tag.Tag
import javax.inject.Inject


/**
 * Use case to create a tag.
 *
 * @param tagRepository Repository to access the tags.
 */
class CreateTagUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {

    /**
     * Creates a tag and stores it.
     *
     * @param name  Name of the tag to create.
     */
    suspend fun createTag(
        name: String
    ) {
        val tag = Tag(
            name = name
        )

        tagRepository.createTag(tag)
    }

}
