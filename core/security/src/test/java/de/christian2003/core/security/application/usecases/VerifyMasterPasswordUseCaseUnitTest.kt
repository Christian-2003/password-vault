package de.christian2003.core.security.application.usecases

import de.christian2003.core.security.application.services.SourceKeyService
import de.christian2003.core.security.domain.repositories.ReadonlyAuthRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever


class VerifyMasterPasswordUseCaseUnitTest {

    private lateinit var readonlyAuthRepository: ReadonlyAuthRepository
    private lateinit var sourceKeyService: SourceKeyService
    private lateinit var useCase: VerifyMasterPasswordUseCase


    @Before
    fun setup() {
        readonlyAuthRepository = mock()
        sourceKeyService = mock()
        useCase = VerifyMasterPasswordUseCase(readonlyAuthRepository, sourceKeyService)
    }


    @Test
    fun `verify returns false if master password is empty`() = runBlocking {
        val result = useCase.verify(charArrayOf())
        Assert.assertFalse(result)
    }


    @Test
    fun `verify returns false if master password KEK or salt is missing`() = runBlocking {
        whenever(readonlyAuthRepository.getMasterPasswordKek()).thenReturn(null)
        whenever(readonlyAuthRepository.getMasterPasswordSalt()).thenReturn(null)

        val result = useCase.verify(charArrayOf('a','b','c'))
        Assert.assertFalse(result)
    }


    @Test
    fun `verify returns false if decryptKekWithSource throws exception`() = runBlocking {
        val masterPassword = charArrayOf('a','b','c')
        whenever(readonlyAuthRepository.getMasterPasswordKek()).thenReturn(byteArrayOf(1,2,3))
        whenever(readonlyAuthRepository.getMasterPasswordSalt()).thenReturn(byteArrayOf(4,5,6))
        whenever(sourceKeyService.decryptKekWithSource(any(), eq(masterPassword), any())).thenThrow(RuntimeException("Invalid"))

        val result = useCase.verify(masterPassword)
        Assert.assertFalse(result)
    }


    @Test
    fun `verify returns true if master password decrypts KEK successfully`() = runBlocking {
        val masterPassword = charArrayOf('a','b','c')
        val decryptedKek = byteArrayOf(10,20,30)

        whenever(readonlyAuthRepository.getMasterPasswordKek()).thenReturn(byteArrayOf(1,2,3))
        whenever(readonlyAuthRepository.getMasterPasswordSalt()).thenReturn(byteArrayOf(4,5,6))
        whenever(sourceKeyService.decryptKekWithSource(any(), eq(masterPassword), any())).thenReturn(decryptedKek)

        val result = useCase.verify(masterPassword)
        Assert.assertTrue(result)
    }

}
