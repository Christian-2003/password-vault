package de.christian2003.security.application.usecases

import de.christian2003.security.application.services.SaltGeneratorService
import de.christian2003.security.application.services.SourceKeyService
import de.christian2003.security.domain.entities.ChangePasswordSession
import de.christian2003.security.domain.exceptions.AuthSetupException
import de.christian2003.security.domain.repositories.AuthTransactionRepository
import de.christian2003.security.domain.repositories.ReadonlyAuthRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


class SaveChangePasswordSessionUseCaseUnitTest {

    private lateinit var authRepository: AuthTransactionRepository
    private lateinit var readonlyAuthRepository: ReadonlyAuthRepository
    private lateinit var sourceKeyService: SourceKeyService
    private lateinit var saltGeneratorService: SaltGeneratorService
    private lateinit var useCase: SaveChangePasswordSessionUseCase


    @Before
    fun setup() {
        authRepository = mock()
        readonlyAuthRepository = mock()
        sourceKeyService = mock()
        saltGeneratorService = mock()

        useCase = SaveChangePasswordSessionUseCase(
            authRepository,
            readonlyAuthRepository,
            sourceKeyService,
            saltGeneratorService
        )
    }


    @Test(expected = IllegalArgumentException::class)
    fun `save throws exception if current master password is empty`() = runBlocking {
        val session = ChangePasswordSession(
            currentMasterPassword = charArrayOf(),
            newMasterPassword = "newpass".toCharArray()
        )

        try {
            useCase.save(session)
            Assert.fail("Expected AuthSetupException")
        } catch (e: AuthSetupException) {
            Assert.assertEquals("Current master password cannot be empty", e.message)
        }
    }


    @Test(expected = IllegalArgumentException::class)
    fun `save throws exception if new master password is empty`() = runBlocking {
        val session = ChangePasswordSession(
            currentMasterPassword = "current".toCharArray(),
            newMasterPassword = charArrayOf()
        )

        try {
            useCase.save(session)
            Assert.fail("Expected AuthSetupException")
        } catch (e: AuthSetupException) {
            Assert.assertEquals("New master password cannot be empty", e.message)
        }
    }


    @Test
    fun `save successfully saves master password`() = runBlocking {
        val currentPassword = "current".toCharArray()
        val newPassword = "newpass".toCharArray()
        val decryptedKek = byteArrayOf(1, 2, 3)
        val encryptedKek = byteArrayOf(4, 5, 6)
        val salt = byteArrayOf(7, 8, 9)

        val session = ChangePasswordSession(currentPassword, newPassword)

        // Mock repository and services
        whenever(readonlyAuthRepository.getMasterPasswordSalt()).thenReturn(byteArrayOf(10, 11))
        whenever(readonlyAuthRepository.getMasterPasswordKek()).thenReturn(byteArrayOf(12, 13))
        whenever(sourceKeyService.decryptKekWithSource(any(), any(), any())).thenReturn(decryptedKek)
        whenever(saltGeneratorService.generateSalt()).thenReturn(salt)
        whenever(sourceKeyService.encryptKekWithSource(any(), any(), any(), any())).thenReturn(encryptedKek)

        // Call use case
        useCase.save(session)

        // Verify transaction flow
        verify(authRepository, times(1)).beginTransaction()
        verify(authRepository, times(1)).setMasterPassword(encryptedKek, salt)
        verify(authRepository, times(1)).commitTransaction()
    }


    @Test
    fun `decrypted KEK is wiped after save`() = runBlocking {
        val currentPassword = "current".toCharArray()
        val newPassword = "newpass".toCharArray()
        val decryptedKek = byteArrayOf(1, 2, 3)
        val encryptedKek = byteArrayOf(4, 5, 6)
        val salt = byteArrayOf(7, 8, 9)

        val session = ChangePasswordSession(currentPassword, newPassword)

        whenever(readonlyAuthRepository.getMasterPasswordSalt()).thenReturn(byteArrayOf(10, 11))
        whenever(readonlyAuthRepository.getMasterPasswordKek()).thenReturn(byteArrayOf(12, 13))
        whenever(sourceKeyService.decryptKekWithSource(any(), any(), any())).thenReturn(decryptedKek)
        whenever(saltGeneratorService.generateSalt()).thenReturn(salt)
        whenever(sourceKeyService.encryptKekWithSource(any(), any(), any(), any())).thenReturn(encryptedKek)

        useCase.save(session)

        // KEK bytes should be zeroed
        decryptedKek.forEach { byte ->
            Assert.assertEquals(0, byte.toInt())
        }
    }

}
