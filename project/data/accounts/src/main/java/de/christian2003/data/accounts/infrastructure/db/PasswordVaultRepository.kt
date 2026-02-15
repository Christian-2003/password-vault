package de.christian2003.data.accounts.infrastructure.db

import de.christian2003.core.security.domain.services.HmacCipherService
import de.christian2003.data.accounts.domain.entities.Account
import de.christian2003.data.accounts.domain.entities.AccountCapability
import de.christian2003.data.accounts.domain.entities.AccountDescriptor
import de.christian2003.data.accounts.domain.entities.Detail
import de.christian2003.data.accounts.domain.entities.DetailType
import de.christian2003.data.accounts.domain.entities.Tag
import de.christian2003.data.accounts.domain.repositories.AccountRepository
import de.christian2003.data.accounts.domain.repositories.TagRepository
import de.christian2003.data.accounts.domain.entities.Target
import de.christian2003.data.accounts.domain.repositories.TargetRepository
import de.christian2003.data.accounts.infrastructure.db.dao.AccountDao
import de.christian2003.data.accounts.infrastructure.db.dao.DetailDao
import de.christian2003.data.accounts.infrastructure.db.dao.TagDao
import de.christian2003.data.accounts.infrastructure.db.dao.TargetDao
import de.christian2003.data.accounts.infrastructure.db.dto.AccountDetailsDto
import de.christian2003.data.accounts.infrastructure.db.dto.AccountWithTags
import de.christian2003.data.accounts.infrastructure.db.entities.AccountEntity
import de.christian2003.data.accounts.infrastructure.db.entities.DetailEntity
import de.christian2003.data.accounts.infrastructure.db.entities.TagEntity
import de.christian2003.data.accounts.infrastructure.db.entities.TargetEntity
import de.christian2003.data.accounts.infrastructure.db.mapper.AccountCapabilityDbMapper
import de.christian2003.data.accounts.infrastructure.db.mapper.AccountDbMapper
import de.christian2003.data.accounts.infrastructure.db.mapper.DetailDbMapper
import de.christian2003.data.accounts.infrastructure.db.mapper.TagDbMapper
import de.christian2003.data.accounts.infrastructure.db.mapper.TargetDbMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.cbor.Cbor
import javax.inject.Inject
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
internal class PasswordVaultRepository @Inject constructor(
    private val accountDao: AccountDao,
    private val detailDao: DetailDao,
    private val tagDao: TagDao,
    private val targetDao: TargetDao,
    private val cipherService: HmacCipherService
): AccountRepository, TagRepository, TargetRepository {

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
     * Mapper for account capabilities.
     */
    private val accountCapabilityMapper: AccountCapabilityDbMapper = AccountCapabilityDbMapper()


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
     * Returns a list with the accounts whose IDs are passed as argument.
     *
     * @param accountIds    List of IDs whose accounts to return.
     * @return              Accounts with the provided IDs.
     */
    override suspend fun getAccountsByIds(accountIds: List<Uuid>): List<Account> {
        val accountEntities: List<AccountEntity> = accountDao.selectAccountsByIds(accountIds)
        val accounts: List<Account> = accountEntities.map { accountEntity ->
            val targetEntities: List<TargetEntity> = targetDao.selectAllForAccount(accountEntity.id).first()
            val targets: List<Target> = targetEntities.map { targetEntity ->
                targetMapper.toDomain(targetEntity)
            }
            val account: Account = accountMapper.toDomain(accountEntity, targets)

            val details: List<Detail> = detailDao.selectAllForAccount(account.descriptor.id).first().map { detailEntity ->
                detailMapper.toDomain(detailEntity)
            }
            account.details = details

            return@map account
        }


        return accounts
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
     * Returns an account capability (i.e. account IDs and IDs of details) that whose account and
     * details match the specified metadata.
     *
     * @param targetName    Target name (e.g. Android package name or website host).
     * @param detailTypes   Types of details.
     * @return              List of account capabilities.
     */
    override suspend fun getAccountsByMetadata(targetName: String, detailTypes: List<DetailType>): List<AccountCapability> {
        val capabilityEntities: List<AccountDetailsDto> = accountDao.selectAccountsAndDetailsByMetadata(targetName, detailTypes)
        val capabilities: List<AccountCapability> = accountCapabilityMapper.toDomain(capabilityEntities)
        return capabilities
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


    /**
     * Returns the targets with the specified package name (e.g. "de.christian2003.passwordvault") or
     * URL host (e.g. "passwordvault.christian2003.de").
     *
     * @param name  Name of the targets to return (either package name or URL host).
     * @return      List of targets with the specified name.
     */
    override fun getTargetsByName(name: String): Flow<List<Target>> {
        val targetEntities: Flow<List<TargetEntity>> = targetDao.selectByName(name)
        val targets: Flow<List<Target>> = targetEntities.map { list ->
            list.map { entity ->
                targetMapper.toDomain(entity)
            }
        }
        return targets
    }


    /**
     * For the specified target, the account is returned. If no account can be retrieved, null is
     * returned.
     *
     * @param target    Target whose account to return.
     * @return          Account of the specified target.
     */
    override suspend fun getAccountForTarget(target: Target): Account? {
        val targetEntity: TargetEntity? = targetDao.selectById(target.id)
        if (targetEntity != null) {
            val accountEntity: AccountEntity? = accountDao.selectAccountById(targetEntity.account)?.account
            if (accountEntity != null) {
                val targets: List<Target> = targetDao.selectAllForAccount(accountEntity.id).first().map { targetEntity ->
                    targetMapper.toDomain(targetEntity)
                }
                val account: Account = accountMapper.toDomain(accountEntity, targets)
                return account
            }
        }

        return null
    }

}
