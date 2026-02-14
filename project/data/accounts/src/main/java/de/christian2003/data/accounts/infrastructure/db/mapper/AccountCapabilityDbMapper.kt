package de.christian2003.data.accounts.infrastructure.db.mapper

import de.christian2003.data.accounts.domain.entities.AccountCapability
import de.christian2003.data.accounts.infrastructure.db.dto.AccountDetailsDto
import kotlin.uuid.Uuid


/**
 * Mapper can map the domain model 'AccountCapability' from it's database model.
 */
internal class AccountCapabilityDbMapper {

    /**
     * Maps the list of database entities that is passed as argument to a list of domain model
     * instances of the type 'AccountCapability'.
     *
     * @param entities  List of database entities to map to the domain models.
     * @return          List of mapped domain models.
     */
    fun toDomain(entities: List<AccountDetailsDto>): List<AccountCapability> {
        val accountCapabilities: MutableList<AccountCapability> = mutableListOf()

        entities.forEach { entity ->
            val existingCapability: AccountCapability? = accountCapabilities.find { it.account == entity.account }
            if (existingCapability == null) {
                val detailsForCurrentAccount: MutableList<Uuid> = mutableListOf()

                entities.forEach { e ->
                    if (e.account == entity.account) {
                        detailsForCurrentAccount.add(e.detail)
                    }
                }

                val capability = AccountCapability(
                    account = entity.account,
                    details = detailsForCurrentAccount,
                    targetUrl = entity.url
                )
                accountCapabilities.add(capability)
            }
        }

        return accountCapabilities
    }

}
