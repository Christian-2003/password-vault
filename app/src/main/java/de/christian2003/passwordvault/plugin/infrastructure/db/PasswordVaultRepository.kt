package de.christian2003.passwordvault.plugin.infrastructure.db

import de.christian2003.passwordvault.domain.model.account.Account
import de.christian2003.passwordvault.domain.model.tag.Tag
import de.christian2003.passwordvault.application.repository.AccountRepository
import de.christian2003.passwordvault.application.repository.TagRepository
import de.christian2003.passwordvault.domain.model.account.AccountDescriptor
import de.christian2003.passwordvault.domain.model.detail.Detail
import de.christian2003.passwordvault.domain.model.target.Target
import de.christian2003.passwordvault.domain.security.CipherService
import de.christian2003.passwordvault.plugin.infrastructure.db.dao.DetailDao
import de.christian2003.passwordvault.plugin.infrastructure.db.dao.AccountDao
import de.christian2003.passwordvault.plugin.infrastructure.db.dao.TagDao
import de.christian2003.passwordvault.plugin.infrastructure.db.dao.TargetDao
import de.christian2003.passwordvault.plugin.infrastructure.db.dto.AccountWithTags
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.DetailEntity
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.TagEntity
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.TargetEntity
import de.christian2003.passwordvault.plugin.infrastructure.db.mapper.DetailDbMapper
import de.christian2003.passwordvault.plugin.infrastructure.db.mapper.AccountDbMapper
import de.christian2003.passwordvault.plugin.infrastructure.db.mapper.TagDbMapper
import de.christian2003.passwordvault.plugin.infrastructure.db.mapper.TargetDbMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.cbor.Cbor
import kotlin.uuid.Uuid


/**
 * Repository for the app.
 *
 * @param accountDao        DAO through which to access the entries in the database.
 * @param detailDao         DAO through which to access the details in the database.
 * @param tagDao            DAO through which to access the tags in the database.
 * @param targetDao         DAO through which to access the targets in the database.
 * @param cipherService     Cipher service used for encryption and decryption.
 */
class PasswordVaultRepository(
    private val accountDao: AccountDao,
    private val detailDao: DetailDao,
    private val tagDao: TagDao,
    private val targetDao: TargetDao,
    private val cipherService: CipherService
): AccountRepository, TagRepository {

    /**
     * Mapper maps the domain model 'Account' to its entity.
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
     * Mapper maps the domain model 'Target' to it's entity.
     */
    private val targetMapper: TargetDbMapper = TargetDbMapper()


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
     * Returns a list which contains the account descriptors of all accounts.
     *
     * @return  List of all account descriptors.
     */
    override fun getAllAccountDescriptors(): Flow<List<AccountDescriptor>> {
        if (accountDescriptors == null) {
            accountDescriptors = accountDao.selectAllAccountsWithoutTags().map { list ->
                list.map { account ->
                    val targets: List<Target> = targetDao.selectAllForAccount(account.id).first().map { targetEntity ->
                        targetMapper.toDomain(targetEntity)
                    }
                    val descriptor: AccountDescriptor = accountMapper.toDescriptor(account, targets)
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
        val accountEntity: AccountWithTags? = accountDao.selectAccountById(id)
        if (accountEntity != null) {
            val targets: List<Target> = targetDao.selectAllForAccount(id).first().map { targetEntity ->
                targetMapper.toDomain(targetEntity)
            }

            val account: Account = accountMapper.toDomain(accountEntity.account, targets)

            val tags: List<Tag> = accountEntity.tags.map { tag ->
                tagMapper.toDomain(tag)
            }
            account.tags = tags

            val details: List<Detail> = detailDao.selectAllForAccount(id).first().map { detailEntity ->
                detailMapper.toDomain(detailEntity)
            }
            account.details = details

            return account
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
        val tagEntities: List<TagEntity> = account.tags.map { tag ->
            tagMapper.toEntity(tag)
        }

        val targetEntities: List<TargetEntity> = account.descriptor.targets.map { target ->
            targetMapper.toEntity(target, account.descriptor.id)
        }

        val detailEntities: List<DetailEntity> = account.details.map { detail ->
            detailMapper.toEntity(detail, account.descriptor.id)
        }

        val entryWithTags = AccountWithTags(
            account = accountMapper.toEntity(account),
            tags = tagEntities
        )

        accountDao.insertAccountWithTags(entryWithTags)
        targetDao.saveAllTargetsForAccount(targetEntities, account.descriptor.id)
        detailDao.saveAllDetailsForAccount(detailEntities, account.descriptor.id)
    }


    /**
     * Updates the account that is passed as argument.
     *
     * @param account   Account to update.
     */
    override suspend fun updateAccount(account: Account) {
        val tagEntities: List<TagEntity> = account.tags.map { tag ->
            tagMapper.toEntity(tag)
        }

        val targetEntities: List<TargetEntity> = account.descriptor.targets.map { target ->
            targetMapper.toEntity(target, account.descriptor.id)
        }

        val detailEntities: List<DetailEntity> = account.details.map { detail ->
            detailMapper.toEntity(detail, account.descriptor.id)
        }

        val accountWithTags = AccountWithTags(
            account = accountMapper.toEntity(account),
            tags = tagEntities
        )

        accountDao.updateAccountWithTags(accountWithTags)
        targetDao.saveAllTargetsForAccount(targetEntities, account.descriptor.id)
        detailDao.saveAllDetailsForAccount(detailEntities, account.descriptor.id)
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
     * Returns a list containing all tags.
     *
     * @return  Flow containing a list of all tags.
     */
    override fun getAllTags(): Flow<List<Tag>> {
        if (tags == null) {
            tags = tagDao.selectAll().map { list ->
                list.map { tagEntity ->
                    val tag = tagMapper.toDomain(tagEntity)
                    return@map tag
                }
            }
        }
        return tags!!
    }


    /**
     * Returns the tag with the specified ID. If no tag with the specified ID exists, null is
     * returned.
     *
     * @param id    ID of the tag to return.
     * @return      Tag with the specified ID or null.
     */
    override suspend fun getTagById(id: Uuid): Tag? {
        val tagEntity: TagEntity? = tagDao.selectById(id)
        return if (tagEntity != null) {
            tagMapper.toDomain(tagEntity)
        } else {
            null
        }
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
