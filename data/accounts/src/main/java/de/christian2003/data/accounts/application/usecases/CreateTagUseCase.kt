package de.christian2003.data.accounts.application.usecases

import de.christian2003.data.accounts.domain.entities.Tag
import de.christian2003.data.accounts.domain.repositories.TagRepository
import javax.inject.Inject


/**
 * Use case to create a tag.
 *
 * @param tagRepository Repository to access the tags.
 */
class CreateTagUseCase @Inject internal constructor(
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
