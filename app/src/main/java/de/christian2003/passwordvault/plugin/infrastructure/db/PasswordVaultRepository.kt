package de.christian2003.passwordvault.plugin.infrastructure.db

import de.christian2003.passwordvault.domain.entry.Detail
import de.christian2003.passwordvault.domain.entry.Entry
import de.christian2003.passwordvault.domain.entry.Tag
import de.christian2003.passwordvault.domain.repository.DetailRepository
import de.christian2003.passwordvault.domain.repository.EntryRepository
import de.christian2003.passwordvault.domain.repository.TagRepository
import de.christian2003.passwordvault.domain.security.CipherService
import de.christian2003.passwordvault.plugin.infrastructure.db.dao.DetailDao
import de.christian2003.passwordvault.plugin.infrastructure.db.dao.EntryDao
import de.christian2003.passwordvault.plugin.infrastructure.db.dao.TagDao
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.DetailEntity
import de.christian2003.passwordvault.plugin.infrastructure.db.dto.EntryWithTags
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.TagEntity
import de.christian2003.passwordvault.plugin.infrastructure.db.mapper.DetailDbMapper
import de.christian2003.passwordvault.plugin.infrastructure.db.mapper.EntryDbMapper
import de.christian2003.passwordvault.plugin.infrastructure.db.mapper.TagDbMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.cbor.Cbor
import kotlin.uuid.Uuid


/**
 * Repository for the app.
 */
class PasswordVaultRepository(

    /**
     * DAO through which to access the entries in the database.
     */
    private val entryDao: EntryDao,

    /**
     * DAO through which to access the details in the database.
     */
    private val detailDao: DetailDao,

    /**
     * DAO through which to access the tags in the database.
     */
    private val tagDao: TagDao,

    /**
     * Cipher service used for encryption and decryption.
     */
    private val cipherService: CipherService

): EntryRepository, DetailRepository, TagRepository {

    /**
     * Mapper maps the domain model 'Entry' to its entity.
     */
    private val entryMapper: EntryDbMapper = EntryDbMapper(
        cbor = Cbor { ignoreUnknownKeys = true },
        cipherService = cipherService
    )

    /**
     * Mapper maps the domain model 'Detail' to its entity.
     */
    private val detailMapper: DetailDbMapper = DetailDbMapper(
        cbor = Cbor { ignoreUnknownKeys = true },
        cipherService = cipherService
    )

    /**
     * Mapper maps the domain model 'Tag' to it's entity.
     */
    private val tagMapper: TagDbMapper = TagDbMapper()

    /**
     * Flow contains a list of all entries. Can be null until "getAllEntries" is called the first
     * time.
     */
    private var entries: Flow<List<Entry>>? = null

    /**
     * Flow contains a list of all tags. Can be null until "getAllTags" is called for the first
     * time.
     */
    private var tags: Flow<List<Tag>>? = null


    /**
     * Returns a list containing all entries.
     *
     * @return  Flow containing a list of all entries.
     */
    override fun getAllEntries(): Flow<List<Entry>> {
        if (entries == null) {
            entries = entryDao.selectAllEntries().map { list ->
                list.map { entry ->
                    val domain: Entry = entryMapper.toDomain(entry.entry)
                    val tags: MutableList<Tag> = mutableListOf()
                    entry.tags.forEach { tag ->
                        tags.add(tagMapper.toDomain(tag))
                    }
                    domain.tags = tags
                    domain
                }
            }
        }
        return entries!!
    }


    /**
     * Returns the entry with the passed UUID. If no entry exists, null is returned.
     *
     * @param id    UUID of the entry to return.
     * @return      Entry with the specified UUID or null.
     */
    override suspend fun getEntryById(id: Uuid): Entry? {
        val entry: EntryWithTags? = entryDao.selectEntryById(id)
        if (entry != null) {
            val domain: Entry = entryMapper.toDomain(entry.entry)
            val tags: MutableList<Tag> = mutableListOf()
            entry.tags.forEach { tag ->
                tags.add(tagMapper.toDomain(tag))
            }
            domain.tags = tags
            return domain
        }
        else {
            return null
        }
    }


    /**
     * Creates the new entry that is passed as argument.
     *
     * @param entry Entry to create.
     */
    override suspend fun createEntry(entry: Entry) {
        val tags: MutableList<TagEntity> = mutableListOf()
        entry.tags.forEach { tag ->
            tags.add(tagMapper.toEntity(tag))
        }
        val entryWithTags = EntryWithTags(
            entry = entryMapper.toEntity(entry),
            tags = tags
        )
        entryDao.insertEntryWithTags(entryWithTags)
    }


    /**
     * Updates the entry that is passed as argument.
     *
     * @param entry Entry to update.
     */
    override suspend fun updateEntry(entry: Entry) {
        val tags: MutableList<TagEntity> = mutableListOf()
        entry.tags.forEach { tag ->
            tags.add(tagMapper.toEntity(tag))
        }
        val entryWithTags = EntryWithTags(
            entry = entryMapper.toEntity(entry),
            tags = tags
        )
        entryDao.updateEntryWithTags(entryWithTags)
    }


    /**
     * Deletes the entry that is passed as argument.
     *
     * @param entry Entry to delete.
     */
    override suspend fun deleteEntry(entry: Entry) {
        entryDao.deleteEntry(entryMapper.toEntity(entry))
    }


    /**
     * Returns a list containing all details for the entry specified.
     *
     * @param entry UUID of the entry whose details to return.
     * @return      Flow containing a list of all details for the entry.
     */
    override fun getAllDetailsForEntry(entry: Uuid): Flow<List<Detail>> {
        val details: Flow<List<Detail>> = detailDao.selectAllForEntry(entry).map { list ->
            list.map { detail ->
                detailMapper.toDomain(detail)
            }
        }
        return details
    }


    /**
     * Returns the detail with the passed UUID. If no detail exists, null is returned.
     *
     * @param id    UUID of the detail to return.
     * @return      Detail with the specified UUID or null.
     */
    override suspend fun getDetailById(id: Uuid): Detail? {
        val detail: DetailEntity? = detailDao.selectById(id)
        return if (detail != null) {
            detailMapper.toDomain(detail)
        } else {
            null
        }
    }


    /**
     * Creates the new detail that is passed as argument.
     *
     * @param detail    Detail to create.
     */
    override suspend fun createDetail(detail: Detail) {
        detailDao.insert(detailMapper.toEntity(detail))
    }


    /**
     * Updates the detail that is passed as argument.
     *
     * @param detail    Detail to update.
     */
    override suspend fun updateDetail(detail: Detail) {
        detailDao.update(detailMapper.toEntity(detail))
    }


    /**
     * Deletes the detail that is passed as argument.
     *
     * @param detail    Detail to delete.
     */
    override suspend fun deleteDetail(detail: Detail) {
        detailDao.delete(detailMapper.toEntity(detail))
    }


    /**
     * Returns a list containing all tags.
     *
     * @return  Flow containing a list of all tags.
     */
    override fun getAllTags(): Flow<List<Tag>> {
        if (tags == null) {
            tags = tagDao.selectAll().map { list ->
                list.map { tag ->
                    tagMapper.toDomain(tag)
                }
            }
        }
        return tags!!
    }


    /**
     * Creates the new tag that is passed as argument.
     *
     * @param tag   Tag to create.
     */
    override suspend fun createTag(tag: Tag) {
        tagDao.insert(tagMapper.toEntity(tag))
    }


    /**
     * Updates the tag that is passed as argument.
     *
     * @param tag   Tag to update.
     */
    override suspend fun updateTag(tag: Tag) {
        tagDao.update(tagMapper.toEntity(tag))
    }


    /**
     * Deletes the tag that is passed as argument.
     *
     * @param tag   Tag to delete.
     */
    override suspend fun deleteTag(tag: Tag) {
        tagDao.delete(tagMapper.toEntity(tag))
    }

}
