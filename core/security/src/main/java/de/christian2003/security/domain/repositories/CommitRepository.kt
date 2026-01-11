package de.christian2003.security.domain.repositories


/**
 * Repository for the setup of the authentication. This repository can commit all changes that
 * were done during the authentication setup.
 */
interface CommitRepository {

    /**
     * Commits all changes that were done during the setup of the authentication.
     */
    fun commitAllChanges()

}
