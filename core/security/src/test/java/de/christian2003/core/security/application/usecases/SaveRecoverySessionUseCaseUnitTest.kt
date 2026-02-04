package de.christian2003.core.security.application.usecases

import de.christian2003.core.security.application.services.SaltGeneratorService
import de.christian2003.core.security.application.services.SourceKeyService
import de.christian2003.core.security.domain.entities.RecoverySession
import de.christian2003.core.security.domain.exceptions.AuthSetupException
import de.christian2003.core.security.domain.repositories.AuthTransactionRepository
import de.christian2003.core.security.domain.repositories.ReadonlyAuthRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


class SaveRecoverySessionUseCaseUnitTest {

    private lateinit var authRepository: AuthTransactionRepository
    private lateinit var readonlyAuthRepository: ReadonlyAuthRepository
    private lateinit var saltGeneratorService: SaltGeneratorService
    private lateinit var sourceKeyService: SourceKeyService
    private lateinit var useCase: SaveRecoverySessionUseCase


    @Before
    fun setup() {
        authRepository = mock()
        readonlyAuthRepository = mock()
        saltGeneratorService = mock()
        sourceKeyService = mock()

        useCase = SaveRecoverySessionUseCase(
            authRepository,
            readonlyAuthRepository,
            saltGeneratorService,
            sourceKeyService
        )
    }


    @Test(expected = IllegalArgumentException::class)
    fun `save throws exception if recovery code is empty`() = runBlocking {
        val session = RecoverySession(
            recoveryCode = charArrayOf(),
            newMasterPassword = "new".toCharArray(),
            newRecoveryCodes = listOf("code".toCharArray())
        )

        try {
            useCase.save(session)
            Assert.fail("Expected AuthSetupException")
        } catch (e: AuthSetupException) {
            Assert.assertEquals("Recovery code cannot be empty", e.message)
        }
    }


    @Test(expected = IllegalArgumentException::class)
    fun `save throws exception if new master password is empty`() = runBlocking {
        val session = RecoverySession(
            recoveryCode = "old".toCharArray(),
            newMasterPassword = charArrayOf(),
            newRecoveryCodes = listOf("code".toCharArray())
        )

        try {
            useCase.save(session)
            Assert.fail("Expected AuthSetupException")
        } catch (e: AuthSetupException) {
            Assert.assertEquals("New master password cannot be empty", e.message)
        }
    }


    @Test(expected = IllegalArgumentException::class)
    fun `save throws exception if new recovery codes list is empty`() = runBlocking {
        val session = RecoverySession(
            recoveryCode = "old".toCharArray(),
            newMasterPassword = "new".toCharArray(),
            newRecoveryCodes = listOf()
        )

        try {
            useCase.save(session)
            Assert.fail("Expected AuthSetupException")
        } catch (e: AuthSetupException) {
            Assert.assertEquals("New recovery codes cannot be empty", e.message)
        }
    }


    @Test
    fun `save successfully updates master password and new recovery codes`() = runBlocking {
        val recoveryCode = "old".toCharArray()
        val newMasterPassword = "newMaster".toCharArray()
        val newRecoveryCode1 = "new1".toCharArray()
        val newRecoveryCode2 = "new2".toCharArray()
        val session = RecoverySession(
            recoveryCode = recoveryCode,
            newMasterPassword = newMasterPassword,
            newRecoveryCodes = listOf(newRecoveryCode1, newRecoveryCode2)
        )

        val decryptedKek = byteArrayOf(1, 2, 3)
        val salt1 = byteArrayOf(4, 5, 6)
        val salt2 = byteArrayOf(7, 8, 9)
        val salt3 = byteArrayOf(10, 11, 12)
        val encryptedMasterKek = byteArrayOf(13, 14, 15)
        val encryptedRecovery1 = byteArrayOf(16, 17, 18)
        val encryptedRecovery2 = byteArrayOf(19, 20, 21)

        // Mocks
        whenever(readonlyAuthRepository.getNumberOfRecoveryCodes()).thenReturn(1)
        whenever(readonlyAuthRepository.getRecoveryCodeSalt(0)).thenReturn(byteArrayOf(1, 2, 3))
        whenever(readonlyAuthRepository.getRecoveryCodeKek(0)).thenReturn(byteArrayOf(4, 5, 6))
        whenever(sourceKeyService.decryptKekWithSource(any(), any(), any())).thenReturn(decryptedKek)
        whenever(saltGeneratorService.generateSalt()).thenReturn(salt1, salt2, salt3)
        whenever(sourceKeyService.encryptKekWithSource(newMasterPassword, salt1, decryptedKek, false)).thenReturn(encryptedMasterKek)
        whenever(sourceKeyService.encryptKekWithSource(newRecoveryCode1, salt2, decryptedKek, false)).thenReturn(encryptedRecovery1)
        whenever(sourceKeyService.encryptKekWithSource(newRecoveryCode2, salt3, decryptedKek, false)).thenReturn(encryptedRecovery2)

        useCase.save(session)

        verify(authRepository, times(1)).beginTransaction()
        verify(authRepository, times(1)).setMasterPassword(encryptedMasterKek, salt1)
        verify(authRepository, times(1)).addRecoveryCode(encryptedRecovery1, salt2)
        verify(authRepository, times(1)).addRecoveryCode(encryptedRecovery2, salt3)
        verify(authRepository, times(1)).commitTransaction()
    }

}
