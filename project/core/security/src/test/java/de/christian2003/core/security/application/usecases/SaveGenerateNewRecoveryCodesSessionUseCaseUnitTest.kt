package de.christian2003.core.security.application.usecases

import de.christian2003.core.security.application.services.SaltGeneratorService
import de.christian2003.core.security.application.services.SourceKeyService
import de.christian2003.core.security.domain.entities.GenerateNewRecoveryCodesSession
import de.christian2003.core.security.domain.repositories.AuthTransactionRepository
import de.christian2003.core.security.domain.repositories.ReadonlyAuthRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


class SaveGenerateNewRecoveryCodesSessionUseCaseUnitTest {

    private lateinit var authRepository: AuthTransactionRepository
    private lateinit var readonlyAuthRepository: ReadonlyAuthRepository
    private lateinit var sourceKeyService: SourceKeyService
    private lateinit var saltGeneratorService: SaltGeneratorService
    private lateinit var useCase: SaveGenerateNewRecoveryCodesSessionUseCase


    @Before
    fun setup() {
        authRepository = mock()
        readonlyAuthRepository = mock()
        sourceKeyService = mock()
        saltGeneratorService = mock()

        useCase = SaveGenerateNewRecoveryCodesSessionUseCase(
            authRepository,
            readonlyAuthRepository,
            sourceKeyService,
            saltGeneratorService
        )
    }


    @Test(expected = IllegalArgumentException::class)
    fun `save throws exception if master password is empty`() = runBlocking {
        val session = GenerateNewRecoveryCodesSession(
            masterPassword = charArrayOf(),
            recoveryCodes = listOf("code".toCharArray())
        )
    }


    @Test(expected = IllegalArgumentException::class)
    fun `save throws exception if recovery codes are empty`() = runBlocking {
        val session = GenerateNewRecoveryCodesSession(
            masterPassword = "master".toCharArray(),
            recoveryCodes = listOf()
        )
    }


    @Test(expected = IllegalArgumentException::class)
    fun `save throws exception if any recovery code is empty`() = runBlocking {
        val session = GenerateNewRecoveryCodesSession(
            masterPassword = "master".toCharArray(),
            recoveryCodes = listOf("".toCharArray())
        )
    }


    @Test
    fun `save successfully stores recovery codes`() = runBlocking {
        val masterPassword = "master".toCharArray()
        val recoveryCode1 = "code1".toCharArray()
        val recoveryCode2 = "code2".toCharArray()
        val session = GenerateNewRecoveryCodesSession(masterPassword, listOf(recoveryCode1, recoveryCode2))

        val decryptedKek = byteArrayOf(1, 2, 3)
        val salt1 = byteArrayOf(4, 5, 6)
        val salt2 = byteArrayOf(7, 8, 9)
        val encryptedKek1 = byteArrayOf(10, 11, 12)
        val encryptedKek2 = byteArrayOf(13, 14, 15)

        // Mocks
        whenever(readonlyAuthRepository.getMasterPasswordSalt()).thenReturn(byteArrayOf(1, 2, 3))
        whenever(readonlyAuthRepository.getMasterPasswordKek()).thenReturn(byteArrayOf(4, 5, 6))
        whenever(sourceKeyService.decryptKekWithSource(any(), any(), any())).thenReturn(decryptedKek)
        whenever(saltGeneratorService.generateSalt()).thenReturn(salt1, salt2)
        whenever(sourceKeyService.encryptKekWithSource(recoveryCode1, salt1, decryptedKek, false)).thenReturn(encryptedKek1)
        whenever(sourceKeyService.encryptKekWithSource(recoveryCode2, salt2, decryptedKek, false)).thenReturn(encryptedKek2)

        useCase.save(session)

        // Verify transaction and save calls
        verify(authRepository, times(1)).beginTransaction()
        verify(authRepository, times(1)).addRecoveryCode(encryptedKek1, salt1)
        verify(authRepository, times(1)).addRecoveryCode(encryptedKek2, salt2)
        verify(authRepository, times(1)).commitTransaction()
    }

}
