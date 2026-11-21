package de.christian2003.passwordvault.application

import de.christian2003.passwordvault.domain.entities.OtherSoftware
import de.christian2003.passwordvault.domain.repositories.OtherSoftwareRepository
import javax.inject.Inject


/**
 * Use case to get a list of all other software to advertise inside Password Vault.
 *
 * @param otherSoftwareRepository   Repository to access other software.
 */
internal class GetOtherSoftwareUseCase @Inject constructor(
    private val otherSoftwareRepository: OtherSoftwareRepository
) {

    /**
     * Returns a list of other software which to advertise inside Password Vault.
     *
     * @return  List of other software.
     */
    suspend fun getOtherSoftware(): List<OtherSoftware> {
        return otherSoftwareRepository.getOtherSoftware()
    }

}
