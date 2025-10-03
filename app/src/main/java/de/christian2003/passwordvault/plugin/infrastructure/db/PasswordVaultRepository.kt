package de.christian2003.passwordvault.plugin.infrastructure.db

import de.christian2003.passwordvault.domain.model.detail.Detail
import de.christian2003.passwordvault.domain.model.account.Account
import de.christian2003.passwordvault.domain.model.tag.Tag
import de.christian2003.passwordvault.application.repository.DetailRepository
import de.christian2003.passwordvault.application.repository.AccountRepository
import de.christian2003.passwordvault.application.repository.TagRepository
import de.christian2003.passwordvault.domain.model.account.AccountDescriptor
import de.christian2003.passwordvault.domain.security.CipherService
import de.christian2003.passwordvault.plugin.infrastructure.db.dao.DetailDao
import de.christian2003.passwordvault.plugin.infrastructure.db.dao.AccountDao
import de.christian2003.passwordvault.plugin.infrastructure.db.dao.TagDao
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.DetailEntity
import de.christian2003.passwordvault.plugin.infrastructure.db.dto.AccountWithTags
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.TagEntity
import de.christian2003.passwordvault.plugin.infrastructure.db.mapper.DetailDbMapper
import de.christian2003.passwordvault.plugin.infrastructure.db.mapper.AccountDbMapper
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
    private val accountDao: AccountDao,

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

): AccountRepository, DetailRepository, TagRepository {

    /**
     * Mapper maps the domain model 'Entry' to its entity.
     */
    private val accountMapper: AccountDbMapper = AccountDbMapper(
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
    private var entries: Flow<List<Account>>? = null

    /**
     * Flow contains a list of all account descriptors. This can be null until
     * "getAllAccountDescriptors" is called for the first time.
     */
    private var accountDescriptors: Flow<List<AccountDescriptor>>? = null

    /**
     * Flow contains a list of all tags. Can be null until "getAllTags" is called for the first
     * time.
     */
    private var tags: Flow<List<Tag>>? = null


    /**
     * Returns a list containing all accounts.
     *
     * @return  Flow containing a list of all accounts.
     */
    @Deprecated("Use getAllAccountDescriptors instead")
    override fun getAllAccounts(): Flow<List<Account>> {
        if (entries == null) {
            entries = accountDao.selectAllAccounts().map { list ->
                list.map { entry ->
                    val domain: Account = accountMapper.toDomain(entry.account)
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
     * Returns a list which contains the account descriptors of all accounts.
     *
     * @return  List of all account descriptors.
     */
    override fun getAllAccountDescriptors(): Flow<List<AccountDescriptor>> {
        if (accountDescriptors == null) {
            accountDescriptors = accountDao.selectAllAccountsWithoutTags().map { list ->
                list.map { account ->
                    val descriptor: AccountDescriptor = accountMapper.toDescriptor(account)
                    return@map descriptor
                }
            }
        }
        return accountDescriptors!!
    }


    /**
     * Returns the account with the passed UUID. If no account exists, null is returned.
     *
     * @param id    UUID of the account to return.
     * @return      Account with the specified UUID or null.
     */
    override suspend fun getAccountById(id: Uuid): Account? {
        val entry: AccountWithTags? = accountDao.selectAccountById(id)
        if (entry != null) {
            val domain: Account = accountMapper.toDomain(entry.account)
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
     * Creates the new account that is passed as argument.
     *
     * @param account   Account to create.
     */
    override suspend fun createAccount(account: Account) {
        val tags: MutableList<TagEntity> = mutableListOf()
        account.tags.forEach { tag ->
            tags.add(tagMapper.toEntity(tag))
        }
        val entryWithTags = AccountWithTags(
            account = accountMapper.toEntity(account),
            tags = tags
        )
        accountDao.insertAccountWithTags(entryWithTags)
    }


    /**
     * Updates the account that is passed as argument.
     *
     * @param account   Account to update.
     */
    override suspend fun updateAccount(account: Account) {
        val tags: MutableList<TagEntity> = mutableListOf()
        account.tags.forEach { tag ->
            tags.add(tagMapper.toEntity(tag))
        }
        val entryWithTags = AccountWithTags(
            account = accountMapper.toEntity(account),
            tags = tags
        )
        accountDao.updateAccountWithTags(entryWithTags)
    }


    /**
     * Deletes the account that is passed as argument.
     *
     * @param account   Account to delete.
     */
    override suspend fun deleteAccount(account: Account) {
        accountDao.deleteAccount(accountMapper.toEntity(account))
    }


    /**
     * Returns a list containing all details for the entry specified.
     *
     * @param entry UUID of the entry whose details to return.
     * @return      Flow containing a list of all details for the entry.
     */
    override fun getAllDetailsForAccount(entry: Uuid): Flow<List<Detail>> {
        val details: Flow<List<Detail>> = detailDao.selectAllForAccount(entry).map { list ->
            list.map { detail ->
                detailMapper.toDomain(detail)
            }
        }
        return details
    }


    override suspend fun saveAllDetailsForAccount(details: List<Detail>, entry: Uuid) {
        val detailEntities: MutableList<DetailEntity> = mutableListOf()
        details.forEach { detail ->
            detailEntities.add(detailMapper.toEntity(detail, entry))
        }
        detailDao.saveAllDetailsForAccount(detailEntities, entry)
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
