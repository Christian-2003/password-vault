package de.christian2003.data.accounts.domain.repositories

import de.christian2003.data.accounts.domain.entities.BatchOption
import de.christian2003.data.accounts.domain.entities.Tag
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid


/**
 * Repository through which to access tags.
 */
internal interface TagRepository {

    /**
     * Returns a list containing all tags.
     *
     * @return  Flow containing a list of all tags.
     */
    fun getAllTags(): Flow<List<Tag>>


    /**
     * Returns the tag with the specified ID. If no tag with the specified ID exists, null is
     * returned.
     *
     * @param id    ID of the tag to return.
     * @return      Tag with the specified ID or null.
     */
    suspend fun getTagById(id: Uuid): Tag?


    /**
     * Creates the new tag that is passed as argument.
     *
     * @param tag   Tag to create.
     */
    suspend fun createTag(tag: Tag)


    /**
     * Creates a batch of tags.
     *
     * @param tags          List of tags to create.
     * @param batchOption   Batch option describing what should happen with existing data.
     */
    suspend fun createTagsBatch(tags: List<Tag>, batchOption: BatchOption)


    /**
     * Updates the tag that is passed as argument.
     *
     * @param tag   Tag to update.
     */
    suspend fun updateTag(tag: Tag)


    /**
     * Deletes the tag that is passed as argument.
     *
     * @param tag   Tag to delete.
     */
    suspend fun deleteTag(tag: Tag)

}
