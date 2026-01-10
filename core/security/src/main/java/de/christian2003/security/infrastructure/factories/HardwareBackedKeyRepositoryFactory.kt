package de.christian2003.security.infrastructure.factories

import de.christian2003.security.domain.repositories.HardwareBackedKeyRepository
import de.christian2003.security.infrastructure.repositories.KeyStoreHardwareBackedKeyRepository


/**
 * Factory to create a repository for hardware-backed keys.
 */
class HardwareBackedKeyRepositoryFactory {

    /**
     * Creates a new repository for hardware-backed keys.
     *
     * @return  Repository for hardware-backed keys.
     */
    fun create(): HardwareBackedKeyRepository {
        //TODO: Add StrongBox implementation and return when supported on device
        return KeyStoreHardwareBackedKeyRepository()
    }

}
