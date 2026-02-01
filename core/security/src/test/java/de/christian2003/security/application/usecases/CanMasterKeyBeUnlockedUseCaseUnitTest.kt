package de.christian2003.security.application.usecases

import de.christian2003.security.domain.repositories.ReadonlyAuthRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever


class CanMasterKeyBeUnlockedUseCaseUnitTest {

    private lateinit var readonlyAuthRepository: ReadonlyAuthRepository
    private lateinit var useCase: CanMasterKeyBeUnlockedUseCase


    @Before
    fun setup() {
        readonlyAuthRepository = mock()
        useCase = CanMasterKeyBeUnlockedUseCase(readonlyAuthRepository)
    }


    @Test
    fun `canBeUnlocked returns true when all data is present`() {
        val kek = byteArrayOf(1, 2, 3)
        val salt = byteArrayOf(4, 5, 6)
        val masterKey = byteArrayOf(7, 8, 9)

        whenever(readonlyAuthRepository.getMasterPasswordKek()).thenReturn(kek)
        whenever(readonlyAuthRepository.getMasterPasswordSalt()).thenReturn(salt)
        whenever(readonlyAuthRepository.getEncryptedMasterKey()).thenReturn(masterKey)

        val result = useCase.canBeUnlocked()

        Assert.assertTrue(result)
    }


    @Test
    fun `canBeUnlocked returns false when master password KEK is null`() {
        val salt = byteArrayOf(4, 5, 6)
        val masterKey = byteArrayOf(7, 8, 9)

        whenever(readonlyAuthRepository.getMasterPasswordKek()).thenReturn(null)
        whenever(readonlyAuthRepository.getMasterPasswordSalt()).thenReturn(salt)
        whenever(readonlyAuthRepository.getEncryptedMasterKey()).thenReturn(masterKey)

        val result = useCase.canBeUnlocked()

        Assert.assertFalse(result)
    }


    @Test
    fun `canBeUnlocked returns false when master password salt is null`() {
        val kek = byteArrayOf(1, 2, 3)
        val masterKey = byteArrayOf(7, 8, 9)

        whenever(readonlyAuthRepository.getMasterPasswordKek()).thenReturn(kek)
        whenever(readonlyAuthRepository.getMasterPasswordSalt()).thenReturn(null)
        whenever(readonlyAuthRepository.getEncryptedMasterKey()).thenReturn(masterKey)

        val result = useCase.canBeUnlocked()

        Assert.assertFalse(result)
    }


    @Test
    fun `canBeUnlocked returns false when master key is null`() {
        val kek = byteArrayOf(1, 2, 3)
        val salt = byteArrayOf(4, 5, 6)

        whenever(readonlyAuthRepository.getMasterPasswordKek()).thenReturn(kek)
        whenever(readonlyAuthRepository.getMasterPasswordSalt()).thenReturn(salt)
        whenever(readonlyAuthRepository.getEncryptedMasterKey()).thenReturn(null)

        val result = useCase.canBeUnlocked()

        Assert.assertFalse(result)
    }


    @Test
    fun `canBeUnlocked returns false when all data is null`() {
        whenever(readonlyAuthRepository.getMasterPasswordKek()).thenReturn(null)
        whenever(readonlyAuthRepository.getMasterPasswordSalt()).thenReturn(null)
        whenever(readonlyAuthRepository.getEncryptedMasterKey()).thenReturn(null)

        val result = useCase.canBeUnlocked()

        Assert.assertFalse(result)
    }

}
