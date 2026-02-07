package de.christian2003.core.security.infrastructure.repositories

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.security.KeyStore
import javax.crypto.SecretKey


class KeyStoreHardwareBackedKeyRepositoryUnitTest {

    private lateinit var repo: KeyStoreHardwareBackedKeyRepository
    private lateinit var keyStoreMock: KeyStore


    @Before
    fun setup() {
        repo = KeyStoreHardwareBackedKeyRepository()
        keyStoreMock = mock(KeyStore::class.java)

        // Inject mock KeyStore via reflection (since it's private)
        val field = KeyStoreHardwareBackedKeyRepository::class.java.getDeclaredField("keyStore")
        field.isAccessible = true
        field.set(repo, keyStoreMock)
    }


    @Test
    fun `getKey returns secret key if alias exists`() {
        val alias = "test"
        val secretKey = mock(SecretKey::class.java)
        val entry = mock(KeyStore.SecretKeyEntry::class.java)
        whenever(keyStoreMock.containsAlias(alias)).thenReturn(true)
        whenever(keyStoreMock.getEntry(alias, null)).thenReturn(entry)
        whenever(entry.secretKey).thenReturn(secretKey)

        val result = repo.getKey(alias)
        Assert.assertEquals(secretKey, result)
    }


    @Test
    fun `getKey returns null if alias does not exist`() {
        val alias = "missing"
        whenever(keyStoreMock.containsAlias(alias)).thenReturn(false)
        val result = repo.getKey(alias)
        Assert.assertEquals(null, result)
    }


    @Test
    fun `containsKey returns true if alias exists`() {
        val alias = "exists"
        whenever(keyStoreMock.containsAlias(alias)).thenReturn(true)
        Assert.assertTrue(repo.containsKey(alias))
    }


    @Test
    fun `containsKey returns false if alias does not exist`() {
        val alias = "missing"
        whenever(keyStoreMock.containsAlias(alias)).thenReturn(false)
        Assert.assertFalse(repo.containsKey(alias))
    }


    @Test
    fun `deleteKey returns true if alias exists`() {
        val alias = "delete"
        doNothing().whenever(keyStoreMock).deleteEntry(alias)
        whenever(keyStoreMock.containsAlias(alias)).thenReturn(true)

        val result = repo.deleteKey(alias)
        Assert.assertTrue(result)
        verify(keyStoreMock).deleteEntry(alias)
    }


    @Test
    fun `deleteKey returns false if alias does not exist`() {
        val alias = "missing"
        whenever(keyStoreMock.containsAlias(alias)).thenReturn(false)
        val result = repo.deleteKey(alias)
        Assert.assertFalse(result)
    }

}
