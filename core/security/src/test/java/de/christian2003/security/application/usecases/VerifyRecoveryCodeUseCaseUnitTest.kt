package de.christian2003.security.application.usecases

import de.christian2003.security.application.services.SourceKeyService
import de.christian2003.security.domain.repositories.ReadonlyAuthRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever


class VerifyRecoveryCodeUseCaseUnitTest {

    private lateinit var readonlyAuthRepository: ReadonlyAuthRepository
    private lateinit var sourceKeyService: SourceKeyService
    private lateinit var useCase: VerifyRecoveryCodeUseCase


    @Before
    fun setup() {
        readonlyAuthRepository = mock()
        sourceKeyService = mock()
        useCase = VerifyRecoveryCodeUseCase(readonlyAuthRepository, sourceKeyService)
    }


    @Test
    fun `verify returns false if no recovery codes exist`() = runBlocking {
        whenever(readonlyAuthRepository.getNumberOfRecoveryCodes()).thenReturn(0)

        val result = useCase.verify(charArrayOf('a','b','c'))
        Assert.assertFalse(result)
    }


    @Test
    fun `verify returns false if recovery code KEK or salt is missing`() = runBlocking {
        val recoveryCode = charArrayOf('1','2','3')
        whenever(readonlyAuthRepository.getNumberOfRecoveryCodes()).thenReturn(1)
        whenever(readonlyAuthRepository.getRecoveryCodeSalt(0)).thenReturn(null)
        whenever(readonlyAuthRepository.getRecoveryCodeKek(0)).thenReturn(null)

        val result = useCase.verify(recoveryCode)
        Assert.assertFalse(result)
    }


    @Test
    fun `verify returns false if decryptKekWithSource throws exception`() = runBlocking {
        val recoveryCode = charArrayOf('1','2','3')
        whenever(readonlyAuthRepository.getNumberOfRecoveryCodes()).thenReturn(1)
        whenever(readonlyAuthRepository.getRecoveryCodeSalt(0)).thenReturn(byteArrayOf(1,2,3))
        whenever(readonlyAuthRepository.getRecoveryCodeKek(0)).thenReturn(byteArrayOf(4,5,6))
        whenever(sourceKeyService.decryptKekWithSource(any(), eq(recoveryCode), any())).thenThrow(RuntimeException("Invalid"))

        val result = useCase.verify(recoveryCode)
        Assert.assertFalse(result)
    }


    @Test
    fun `verify returns true if any recovery code decrypts KEK successfully`() = runBlocking {
        val recoveryCode = charArrayOf('1','2','3')
        val decryptedKek = byteArrayOf(10,20,30)

        whenever(readonlyAuthRepository.getNumberOfRecoveryCodes()).thenReturn(2)
        whenever(readonlyAuthRepository.getRecoveryCodeSalt(0)).thenReturn(byteArrayOf(1,2,3))
        whenever(readonlyAuthRepository.getRecoveryCodeKek(0)).thenReturn(byteArrayOf(4,5,6))
        whenever(sourceKeyService.decryptKekWithSource(any(), eq(recoveryCode), any())).thenThrow(RuntimeException("fail"))

        whenever(readonlyAuthRepository.getRecoveryCodeSalt(1)).thenReturn(byteArrayOf(7,8,9))
        whenever(readonlyAuthRepository.getRecoveryCodeKek(1)).thenReturn(byteArrayOf(10,11,12))
        whenever(sourceKeyService.decryptKekWithSource(any(), eq(recoveryCode), any())).thenReturn(decryptedKek)

        val result = useCase.verify(recoveryCode)
        Assert.assertTrue(result)
    }

}
