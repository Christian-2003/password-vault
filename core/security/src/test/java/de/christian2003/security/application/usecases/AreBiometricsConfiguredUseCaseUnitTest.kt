package de.christian2003.security.application.usecases

import de.christian2003.security.domain.repositories.ReadonlyAuthRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.whenever


class AreBiometricsConfiguredUseCaseUnitTest {

    private lateinit var readonlyAuthRepository: ReadonlyAuthRepository
    private lateinit var useCase: AreBiometricsConfiguredUseCase


    @Before
    fun setup() {
        readonlyAuthRepository = Mockito.mock(ReadonlyAuthRepository::class.java)
        useCase = AreBiometricsConfiguredUseCase(readonlyAuthRepository)
    }


    @Test
    fun `returns true when biometrics configured and available`() {
        whenever(readonlyAuthRepository.isBiometricsConfigured()).thenReturn(true)
        whenever(readonlyAuthRepository.isBiometricsAvailable()).thenReturn(true)

        val result = useCase.areBiometricsConfigured()

        Assert.assertTrue(result)
    }


    @Test
    fun `returns false when biometrics configured but not available`() {
        whenever(readonlyAuthRepository.isBiometricsConfigured()).thenReturn(true)
        whenever(readonlyAuthRepository.isBiometricsAvailable()).thenReturn(false)

        val result = useCase.areBiometricsConfigured()

        Assert.assertFalse(result)
    }


    @Test
    fun `returns false when biometrics not configured but available`() {
        whenever(readonlyAuthRepository.isBiometricsConfigured()).thenReturn(false)
        whenever(readonlyAuthRepository.isBiometricsAvailable()).thenReturn(true)

        val result = useCase.areBiometricsConfigured()

        Assert.assertFalse(result)
    }


    @Test
    fun `returns false when biometrics not configured and not available`() {
        whenever(readonlyAuthRepository.isBiometricsConfigured()).thenReturn(false)
        whenever(readonlyAuthRepository.isBiometricsAvailable()).thenReturn(false)

        val result = useCase.areBiometricsConfigured()

        Assert.assertFalse(result)
    }

}
