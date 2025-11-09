package de.christian2003.passwordvault.application.usecases.tag

import de.christian2003.passwordvault.application.repository.TagRepository
import de.christian2003.passwordvault.domain.model.tag.Tag
import javax.inject.Inject
import kotlin.uuid.Uuid


/**
 * Use case to update a tag.
 *
 * @param tagRepository Repository to access the tags.
 */
class UpdateTagUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {

    /**
     * Updates the tag with the specified ID. If no tag with the ID exists, nothing happens.
     *
     * @param id    ID of the tag to update.
     * @param name  Name for the tag.
     */
    suspend fun updateTag(
        id: Uuid,
        name: String
    ) {
        val tag: Tag? = tagRepository.getTagById(id)

        if (tag != null) {
            tag.name = name
            tagRepository.updateTag(tag)
        }
    }

}
