package de.christian2003.data.accounts.application.usecases

import de.christian2003.data.accounts.domain.entities.Tag
import de.christian2003.data.accounts.domain.repositories.TagRepository
import javax.inject.Inject
import kotlin.uuid.Uuid


/**
 * Use case to delete a tag.
 *
 * @param tagRepository Repository to access the tags.
 */
class DeleteTagUseCase @Inject internal constructor(
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
