package de.christian2003.passwordvault.domain.repositories

import de.christian2003.passwordvault.domain.entities.OtherSoftware


/**
 * Repository accesses other software that should be advertised by Password Vault.
 */
internal interface OtherSoftwareRepository {

    /**
     * Retrieves a list of other software that should be advertised by Password Vault.
     *
     * @return  List of other software.
     */
    suspend fun getOtherSoftware(): List<OtherSoftware>

}
