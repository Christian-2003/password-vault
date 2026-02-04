package de.christian2003.core.security.infrastructure.repositories

import org.junit.Assert
import org.junit.Before
import org.junit.Test


class UnlockMasterKeyRepositoryImplUnitTest {

    private lateinit var repository: UnlockedMasterKeyRepositoryImpl


    @Before
    fun setup() {
        repository = UnlockedMasterKeyRepositoryImpl()
    }


    @Test
    fun `isMasterKeyUnlocked returns false when master key not set`() {
        Assert.assertFalse(repository.isMasterKeyUnlocked())
    }



    @Test
    fun `getUnlockedMasterKeyBytes returns null when master key not set`() {
        Assert.assertEquals(null, repository.getUnlockedMasterKeyBytes())
    }


    @Test
    fun `setUnlockedMasterKeyBytes stores master key and isMasterKeyUnlocked returns true`() {
        val keyBytes = byteArrayOf(1, 2, 3, 4)
        repository.setUnlockedMasterKeyBytes(keyBytes)

        Assert.assertTrue(repository.isMasterKeyUnlocked())
        Assert.assertArrayEquals(keyBytes, repository.getUnlockedMasterKeyBytes())
    }


    @Test
    fun `setUnlockedMasterKeyBytes overwrites previous value`() {
        val key1 = byteArrayOf(1, 2)
        val key2 = byteArrayOf(3, 4)

        repository.setUnlockedMasterKeyBytes(key1)
        repository.setUnlockedMasterKeyBytes(key2)

        Assert.assertArrayEquals(key2, repository.getUnlockedMasterKeyBytes())
    }

}
