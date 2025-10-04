package de.christian2003.passwordvault.application.usecases.tag

import de.christian2003.passwordvault.application.repository.TagRepository
import de.christian2003.passwordvault.domain.model.tag.Tag
import kotlin.uuid.Uuid


/**
 * Use case to delete a tag.
 *
 * @param tagRepository Repository to access the tags.
 */
class DeleteTagUseCase(
    private val tagRepository: TagRepository
) {

    /**
     * Deletes the tag with the specified ID.
     *
     * @param id    ID of the tag to delete.
     */
    suspend fun deleteTag(
        id: Uuid
    ) {
        val tag: Tag? = tagRepository.getTagById(id)

        if (tag != null) {
            tagRepository.deleteTag(tag)
        }
    }

}
