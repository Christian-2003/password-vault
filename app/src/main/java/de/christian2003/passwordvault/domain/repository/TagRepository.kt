package de.christian2003.passwordvault.domain.repository

import de.christian2003.passwordvault.domain.entry.Tag
import kotlinx.coroutines.flow.Flow


interface TagRepository {

    /**
     * Returns a list containing all tags.
     *
     * @return  Flow containing a list of all tags.
     */
    fun getAllTags(): Flow<List<Tag>>


    /**
     * Creates the new tag that is passed as argument.
     *
     * @param tag   Tag to create.
     */
    suspend fun createTag(tag: Tag)


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
