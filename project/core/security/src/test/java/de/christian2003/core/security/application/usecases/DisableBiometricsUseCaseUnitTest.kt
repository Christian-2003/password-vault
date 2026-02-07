package de.christian2003.core.security.application.usecases

import de.christian2003.core.security.domain.entities.SecurityAliases
import de.christian2003.core.security.domain.repositories.AuthTransactionRepository
import de.christian2003.core.security.domain.repositories.HardwareBackedKeyRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


class DisableBiometricsUseCaseUnitTest {

    private lateinit var authRepository: AuthTransactionRepository
    private lateinit var hardwareBackedKeyRepository: HardwareBackedKeyRepository
    private lateinit var useCase: DisableBiometricsUseCase


    @Before
    fun setup() {
        authRepository = mock()
        hardwareBackedKeyRepository = mock()

        useCase = DisableBiometricsUseCase(
            authRepository,
            hardwareBackedKeyRepository
        )
    }


    @Test
    fun `disable successfully removes biometrics data`() {
        useCase.disable()

        verify(authRepository, times(1)).beginTransaction()
        verify(authRepository, times(1)).deleteBiometricsKek()
        verify(authRepository, times(1)).commitTransaction()
        verify(hardwareBackedKeyRepository, times(1))
            .deleteKey(SecurityAliases.BiometricsHardwareBackedKey.getAlias())
    }


    @Test
    fun `disable does not crash if repository throws exception`() {
        whenever(authRepository.beginTransaction()).thenThrow(RuntimeException("Boom"))

        try {
            useCase.disable()
            Assert.assertTrue(true) // success if no crash
        } catch (e: Exception) {
            Assert.fail("disable() should swallow exceptions, but threw: ${e.message}")
        }
    }


    @Test
    fun `disable does not crash if key deletion throws exception`() {
        whenever(hardwareBackedKeyRepository.deleteKey(any())).thenThrow(RuntimeException("Key error"))

        try {
            useCase.disable()
            Assert.assertTrue(true) // success if no crash
        } catch (e: Exception) {
            Assert.fail("disable() should swallow exceptions, but threw: ${e.message}")
        }
    }

}
