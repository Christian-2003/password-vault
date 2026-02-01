package de.christian2003.security.application.usecases

import de.christian2003.security.domain.services.BiometricsService
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.whenever


class BiometricAuthUseCaseUnitTest {

    private lateinit var biometricsService: BiometricsService
    private lateinit var useCase: BiometricAuthUseCase


    @Before
    fun setup() {
        biometricsService = Mockito.mock(BiometricsService::class.java)
        useCase = BiometricAuthUseCase(biometricsService)
    }


    @Test
    fun `authenticate returns true when biometrics service succeeds`() = runBlocking {
        whenever(biometricsService.authenticate()).thenReturn(true)

        val result = useCase.authenticate()

        Assert.assertTrue(result)
    }


    @Test
    fun `authenticate returns false when biometrics service fails`() = runBlocking {
        whenever(biometricsService.authenticate()).thenReturn(false)

        val result = useCase.authenticate()

        Assert.assertFalse(result)
    }


    @Test
    fun `authenticate returns false when biometrics service throws exception`() = runBlocking {
        whenever(biometricsService.authenticate()).thenThrow(RuntimeException("Biometric error"))

        val result = useCase.authenticate()

        Assert.assertFalse(result)
    }

}
