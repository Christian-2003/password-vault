package de.christian2003.core.security.application.usecases

import de.christian2003.core.security.application.services.SourceKeyService
import de.christian2003.core.security.domain.exceptions.UnlockFailedException
import de.christian2003.core.security.domain.repositories.ReadonlyAuthRepository
import de.christian2003.core.security.domain.repositories.UnlockedMasterKeyRepository
import de.christian2003.core.security.domain.services.CipherService
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


class UnlockWithMasterPasswordUseCaseUnitTest {

    private lateinit var readonlyAuthRepository: ReadonlyAuthRepository
    private lateinit var unlockedMasterKeyRepository: UnlockedMasterKeyRepository
    private lateinit var cipherService: CipherService
    private lateinit var sourceKeyService: SourceKeyService
    private lateinit var useCase: UnlockWithMasterPasswordUseCase


    @Before
    fun setup() {
        readonlyAuthRepository = mock()
        unlockedMasterKeyRepository = mock()
        cipherService = mock()
        sourceKeyService = mock()

        useCase = UnlockWithMasterPasswordUseCase(
            readonlyAuthRepository,
            unlockedMasterKeyRepository,
            cipherService,
            sourceKeyService
        )
    }


    @Test
    fun `unlock returns false if master password is empty`() = runBlocking {
        val result = useCase.unlock(charArrayOf())
        Assert.assertFalse(result)
    }


    @Test
    fun `unlock throws UnlockFailedException if setup not completed`() = runBlocking {
        whenever(readonlyAuthRepository.getMasterPasswordKek()).thenReturn(null)
        whenever(readonlyAuthRepository.getMasterPasswordSalt()).thenReturn(null)
        whenever(readonlyAuthRepository.getEncryptedMasterKey()).thenReturn(null)

        try {
            useCase.unlock(charArrayOf('a','b','c'))
            Assert.fail("Expected UnlockFailedException")
        } catch (e: UnlockFailedException) {
            Assert.assertEquals("Master password has not yet been set up", e.message)
        }
    }


    @Test
    fun `unlock returns false if master password decryption fails`() = runBlocking {
        val masterPassword = charArrayOf('a','b','c')
        whenever(readonlyAuthRepository.getMasterPasswordKek()).thenReturn(byteArrayOf(1,2,3))
        whenever(readonlyAuthRepository.getMasterPasswordSalt()).thenReturn(byteArrayOf(4,5,6))
        whenever(readonlyAuthRepository.getEncryptedMasterKey()).thenReturn(byteArrayOf(7,8,9))
        whenever(sourceKeyService.decryptKekWithSource(any(), eq(masterPassword), any())).thenThrow(RuntimeException("Invalid"))

        val result = useCase.unlock(masterPassword)
        Assert.assertFalse(result)
    }


    @Test
    fun `unlock returns false if master key decryption fails`() = runBlocking {
        val masterPassword = charArrayOf('a','b','c')
        val decryptedKek = byteArrayOf(10,20,30)
        whenever(readonlyAuthRepository.getMasterPasswordKek()).thenReturn(byteArrayOf(1,2,3))
        whenever(readonlyAuthRepository.getMasterPasswordSalt()).thenReturn(byteArrayOf(4,5,6))
        whenever(readonlyAuthRepository.getEncryptedMasterKey()).thenReturn(byteArrayOf(7,8,9))
        whenever(sourceKeyService.decryptKekWithSource(any(), eq(masterPassword), any())).thenReturn(decryptedKek)
        whenever(cipherService.decrypt(any(), eq(decryptedKek))).thenThrow(RuntimeException("Decrypt failed"))

        val result = useCase.unlock(masterPassword)
        Assert.assertFalse(result)
    }


    @Test
    fun `unlock successfully decrypts KEK and master key`() = runBlocking {
        val masterPassword = charArrayOf('a','b','c')
        val decryptedKek = byteArrayOf(10,20,30)
        val decryptedMasterKey = byteArrayOf(40,50,60)

        whenever(readonlyAuthRepository.getMasterPasswordKek()).thenReturn(byteArrayOf(1,2,3))
        whenever(readonlyAuthRepository.getMasterPasswordSalt()).thenReturn(byteArrayOf(4,5,6))
        whenever(readonlyAuthRepository.getEncryptedMasterKey()).thenReturn(byteArrayOf(7,8,9))
        whenever(sourceKeyService.decryptKekWithSource(any(), eq(masterPassword), any())).thenReturn(decryptedKek)
        whenever(cipherService.decrypt(any(), eq(decryptedKek))).thenReturn(decryptedMasterKey)

        val result = useCase.unlock(masterPassword)
        Assert.assertTrue(result)
        verify(unlockedMasterKeyRepository, times(1)).setUnlockedMasterKeyBytes(decryptedMasterKey)
    }

}
