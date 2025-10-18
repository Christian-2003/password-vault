package de.christian2003.passwordvault.plugin

import android.app.Application
import de.christian2003.passwordvault.plugin.infrastructure.security.AesCipherService
import de.christian2003.passwordvault.plugin.infrastructure.db.PasswordVaultDatabase
import de.christian2003.passwordvault.plugin.infrastructure.db.PasswordVaultRepository


/**
 * Application implementation for the entire program.
 */
class PasswordVaultApplication(): Application() {

    /**
     * Database for the application.
     */
    private lateinit var database: PasswordVaultDatabase

    /**
     * Repository for the application.
     */
    private var repository: PasswordVaultRepository? = null


    /**
     * Returns the repository of the application.
     *
     * @return  Repository.
     */
    fun getRepository(): PasswordVaultRepository {
        if (repository == null) {
            database = PasswordVaultDatabase.getInstance(this)
            repository = PasswordVaultRepository(
                accountDao = database.accountDao,
                detailDao = database.detailDao,
                tagDao = database.tagDao,
                targetDao = database.targetDao,
                cipherService = AesCipherService()
            )
        }
        return repository!!
    }

}
