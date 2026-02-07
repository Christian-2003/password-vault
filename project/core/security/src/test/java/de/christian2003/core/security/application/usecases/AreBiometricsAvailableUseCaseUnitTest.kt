package de.christian2003.core.security.application.usecases

import de.christian2003.core.security.domain.repositories.ReadonlyAuthRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


class AreBiometricsAvailableUseCaseUnitTest {

    private lateinit var readonlyAuthRepository: ReadonlyAuthRepository
    private lateinit var useCase: AreBiometricsAvailableUseCase


    @Before
    fun setup() {
        readonlyAuthRepository = mock()

        useCase = AreBiometricsAvailableUseCase(
            readonlyAuthRepository
        )
    }


    @Test
    fun `areBiometricsAvailable returns true when biometrics are available`() {
        whenever(readonlyAuthRepository.isBiometricsAvailable()).thenReturn(true)

        val result: Boolean = useCase.areBiometricsAvailable()

        Assert.assertEquals(true, result)
        verify(readonlyAuthRepository, times(1)).isBiometricsAvailable()
    }


    @Test
    fun `areBiometricsAvailable returns false when biometrics are not available`() {
        whenever(readonlyAuthRepository.isBiometricsAvailable()).thenReturn(false)

        val result: Boolean = useCase.areBiometricsAvailable()

        Assert.assertEquals(false, result)
        verify(readonlyAuthRepository, times(1)).isBiometricsAvailable()
    }

}
