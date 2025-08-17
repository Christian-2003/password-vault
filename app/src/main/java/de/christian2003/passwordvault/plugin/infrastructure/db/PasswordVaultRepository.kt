package de.christian2003.passwordvault.plugin.infrastructure.db

import de.christian2003.passwordvault.domain.entry.Entry
import de.christian2003.passwordvault.domain.repository.EntryRepository
import de.christian2003.passwordvault.domain.security.CipherService
import de.christian2003.passwordvault.plugin.infrastructure.db.dao.EntryDao
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.EntryEntity
import de.christian2003.passwordvault.plugin.infrastructure.db.mapper.EntryDbMapper
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
     * Cipher service used for encryption and decryption.
     */
    private val cipherService: CipherService

): EntryRepository {

    /**
     * Mapper maps the domain model 'Entry' to its entity.
     */
    private val entryMapper: EntryDbMapper = EntryDbMapper(
        cbor = Cbor { ignoreUnknownKeys = true },
        cipherService = cipherService
    )

    /**
     * Flow contains a list of all entries. Can be null until "getAllEntries"
     * is called the first time.
     */
    private var entries: Flow<List<Entry>>? = null


    /**
     * Returns a list containing all entries.
     *
     * @return  Flow containing a list of all entries.
     */
    override fun getAllEntries(): Flow<List<Entry>> {
        if (entries == null) {
            entries = entryDao.selectAllEntries().map { list ->
                list.map { entry ->
                    entryMapper.toDomain(entry)
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
        val entry: EntryEntity? = entryDao.selectById(id)
        return if (entry != null) {
            entryMapper.toDomain(entry)
        } else {
            null
        }
    }


    /**
     * Creates the new entry that is passed as argument.
     *
     * @param entry Entry to create.
     */
    override suspend fun createEntry(entry: Entry) {
        entryDao.insert(entryMapper.toEntity(entry))
    }


    /**
     * Updates the entry that is passed as argument.
     *
     * @param entry Entry to update.
     */
    override suspend fun updateEntry(entry: Entry) {
        entryDao.update(entryMapper.toEntity(entry))
    }


    /**
     * Deletes the entry that is passed as argument.
     *
     * @param entry Entry to delete.
     */
    override suspend fun deleteEntry(entry: Entry) {
        entryDao.delete(entryMapper.toEntity(entry))
    }

}
