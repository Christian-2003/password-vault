package de.christian2003.core.security.application.usecases

import de.christian2003.core.security.domain.exceptions.UnlockFailedException
import de.christian2003.core.security.domain.repositories.HardwareBackedKeyRepository
import de.christian2003.core.security.domain.repositories.ReadonlyAuthRepository
import de.christian2003.core.security.domain.repositories.UnlockedMasterKeyRepository
import de.christian2003.core.security.domain.services.BiometricsService
import de.christian2003.core.security.domain.services.CipherService
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import javax.crypto.SecretKey


class UnlockWithBiometricsUseCaseUnitTest {

    private lateinit var readonlyAuthRepository: ReadonlyAuthRepository
    private lateinit var hardwareBackedKeyRepository: HardwareBackedKeyRepository
    private lateinit var unlockedMasterKeyRepository: UnlockedMasterKeyRepository
    private lateinit var biometricsService: BiometricsService
    private lateinit var cipherService: CipherService
    private lateinit var useCase: UnlockWithBiometricsUseCase


    @Before
    fun setup() {
        readonlyAuthRepository = mock()
        hardwareBackedKeyRepository = mock()
        unlockedMasterKeyRepository = mock()
        biometricsService = mock()
        cipherService = mock()

        useCase = UnlockWithBiometricsUseCase(
            readonlyAuthRepository,
            hardwareBackedKeyRepository,
            unlockedMasterKeyRepository,
            biometricsService,
            cipherService
        )
    }


    @Test
    fun `unlock returns false if biometric keys are missing`() = runBlocking {
        whenever(hardwareBackedKeyRepository.getKey(any())).thenReturn(null)
        whenever(readonlyAuthRepository.getBiometricsKek()).thenReturn(null)
        whenever(readonlyAuthRepository.getEncryptedMasterKey()).thenReturn(null)

        try {
            useCase.unlock()
            Assert.fail("Expected UnlockFailedException")
        } catch (e: UnlockFailedException) {
            Assert.assertEquals("Biometrics have not yet been set up", e.message)
        }
    }


    @Test
    fun `unlock returns false if biometrics not available`() = runBlocking {
        val secretKey: SecretKey = mock()
        whenever(hardwareBackedKeyRepository.getKey(any())).thenReturn(secretKey)
        whenever(readonlyAuthRepository.getBiometricsKek()).thenReturn(byteArrayOf(1,2,3))
        whenever(readonlyAuthRepository.getEncryptedMasterKey()).thenReturn(byteArrayOf(4,5,6))
        whenever(readonlyAuthRepository.isBiometricsAvailable()).thenReturn(false)

        val result = useCase.unlock()
        Assert.assertFalse(result)
    }


    @Test
    fun `unlock returns false if biometric authentication fails`() = runBlocking {
        val secretKey: SecretKey = mock()
        whenever(hardwareBackedKeyRepository.getKey(any())).thenReturn(secretKey)
        whenever(readonlyAuthRepository.getBiometricsKek()).thenReturn(byteArrayOf(1,2,3))
        whenever(readonlyAuthRepository.getEncryptedMasterKey()).thenReturn(byteArrayOf(4,5,6))
        whenever(readonlyAuthRepository.isBiometricsAvailable()).thenReturn(true)
        whenever(biometricsService.authenticate()).thenReturn(false)

        val result = useCase.unlock()
        Assert.assertFalse(result)
    }


    @Test
    fun `unlock returns false if KEK decryption fails`() = runBlocking {
        val secretKey: SecretKey = mock()
        whenever(hardwareBackedKeyRepository.getKey(any())).thenReturn(secretKey)
        whenever(readonlyAuthRepository.getBiometricsKek()).thenReturn(byteArrayOf(1,2,3))
        whenever(readonlyAuthRepository.getEncryptedMasterKey()).thenReturn(byteArrayOf(4,5,6))
        whenever(readonlyAuthRepository.isBiometricsAvailable()).thenReturn(true)
        whenever(biometricsService.authenticate()).thenReturn(true)
        whenever(cipherService.decrypt(any(), eq(secretKey))).thenThrow(RuntimeException("Decrypt failed"))

        val result = useCase.unlock()
        Assert.assertFalse(result)
    }


    @Test
    fun `unlock successfully decrypts KEK and master key`() = runBlocking {
        val secretKey: SecretKey = mock()
        val decryptedKek = byteArrayOf(10, 20, 30)
        val decryptedMasterKey = byteArrayOf(40, 50, 60)

        whenever(hardwareBackedKeyRepository.getKey(any())).thenReturn(secretKey)
        whenever(readonlyAuthRepository.getBiometricsKek()).thenReturn(byteArrayOf(1,2,3))
        whenever(readonlyAuthRepository.getEncryptedMasterKey()).thenReturn(byteArrayOf(4,5,6))
        whenever(readonlyAuthRepository.isBiometricsAvailable()).thenReturn(true)
        whenever(biometricsService.authenticate()).thenReturn(true)
        whenever(cipherService.decrypt(byteArrayOf(1,2,3), secretKey)).thenReturn(decryptedKek)
        whenever(cipherService.decrypt(byteArrayOf(4,5,6), decryptedKek)).thenReturn(decryptedMasterKey)

        val result = useCase.unlock()
        Assert.assertTrue(result)
        verify(unlockedMasterKeyRepository, times(1)).setUnlockedMasterKeyBytes(decryptedMasterKey)
    }

}
