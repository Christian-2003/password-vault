package de.christian2003.core.security.application.usecases

import android.security.keystore.KeyGenParameterSpec
import de.christian2003.core.security.application.services.SourceKeyService
import de.christian2003.core.security.domain.entities.EnableBiometricsSession
import de.christian2003.core.security.domain.exceptions.AuthSetupException
import de.christian2003.core.security.domain.repositories.AuthTransactionRepository
import de.christian2003.core.security.domain.repositories.HardwareBackedKeyRepository
import de.christian2003.core.security.domain.repositories.ReadonlyAuthRepository
import de.christian2003.core.security.domain.services.CipherService
import de.christian2003.core.security.domain.services.KeyGeneratorService
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import javax.crypto.SecretKey


class SaveEnableBiometricsSessionUseCaseUnitTest {

    private lateinit var authRepository: AuthTransactionRepository
    private lateinit var readonlyAuthRepository: ReadonlyAuthRepository
    private lateinit var hardwareBackedKeyRepository: HardwareBackedKeyRepository
    private lateinit var sourceKeyService: SourceKeyService
    private lateinit var keyGeneratorService: KeyGeneratorService
    private lateinit var cipherService: CipherService
    private lateinit var useCase: SaveEnableBiometricsSessionUseCase


    @Before
    fun setup() {
        authRepository = mock()
        readonlyAuthRepository = mock()
        hardwareBackedKeyRepository = mock()
        sourceKeyService = mock()
        keyGeneratorService = mock()
        cipherService = mock()

        useCase = SaveEnableBiometricsSessionUseCase(
            authRepository,
            readonlyAuthRepository,
            hardwareBackedKeyRepository,
            sourceKeyService,
            keyGeneratorService,
            cipherService
        )
    }


    @Test
    fun `save throws exception if biometrics unavailable`() = runBlocking {
        whenever(readonlyAuthRepository.isBiometricsAvailable()).thenReturn(false)

        val session = EnableBiometricsSession(charArrayOf('a', 'b', 'c'))

        try {
            useCase.save(session)
            Assert.fail("Expected AuthSetupException")
        } catch (e: AuthSetupException) {
            Assert.assertEquals("Biometrics are unavailable", e.message)
        }
    }


    @Test
    fun `save returns immediately if biometrics already configured`() = runBlocking {
        whenever(readonlyAuthRepository.isBiometricsAvailable()).thenReturn(true)
        whenever(readonlyAuthRepository.isBiometricsConfigured()).thenReturn(true)

        val session = EnableBiometricsSession(charArrayOf('a', 'b', 'c'))
        useCase.save(session)

        // Success if no exception is thrown
        Assert.assertTrue(true)
    }


    @Test
    fun `save successfully saves KEK when biometric authentication succeeds`() = runBlocking {
        val masterPassword = charArrayOf('a', 'b', 'c')
        val decryptedKek = byteArrayOf(1, 2, 3)
        val encryptedKek = byteArrayOf(4, 5, 6)
        val salt = byteArrayOf(7, 8, 9)
        val secretKey: SecretKey = mock()
        val keyGenSpec: KeyGenParameterSpec = mock()

        val session = EnableBiometricsSession(masterPassword)

        // Mock repository and services
        whenever(readonlyAuthRepository.isBiometricsAvailable()).thenReturn(true)
        whenever(readonlyAuthRepository.isBiometricsConfigured()).thenReturn(false)
        whenever(readonlyAuthRepository.getMasterPasswordSalt()).thenReturn(salt)
        whenever(readonlyAuthRepository.getMasterPasswordKek()).thenReturn(byteArrayOf(10, 11, 12))
        whenever(sourceKeyService.decryptKekWithSource(any(), any(), any())).thenReturn(decryptedKek)
        whenever(hardwareBackedKeyRepository.containsKey(any())).thenReturn(false)
        whenever(keyGeneratorService.getKeyGenParameterSpecForSecureKey(any(), any())).thenReturn(keyGenSpec)
        whenever(hardwareBackedKeyRepository.generateNewKey(any(), any(), any())).thenReturn(secretKey)
        whenever(cipherService.encrypt(decryptedKek, secretKey)).thenReturn(encryptedKek)

        // Call use case
        useCase.save(session)

        // Verify transaction flow
        verify(authRepository, times(1)).beginTransaction()
        verify(authRepository, times(1)).setBiometricsKek(encryptedKek)
        verify(authRepository, times(1)).commitTransaction()
    }


    @Test
    fun `decrypted KEK is wiped after save`() = runBlocking {
        val masterPassword = charArrayOf('a', 'b', 'c')
        val decryptedKek = byteArrayOf(1, 2, 3)
        val encryptedKek = byteArrayOf(4, 5, 6)
        val salt = byteArrayOf(7, 8, 9)
        val secretKey: SecretKey = mock()
        val keyGenSpec: KeyGenParameterSpec = mock()

        val session = EnableBiometricsSession(masterPassword)

        whenever(readonlyAuthRepository.isBiometricsAvailable()).thenReturn(true)
        whenever(readonlyAuthRepository.isBiometricsConfigured()).thenReturn(false)
        whenever(readonlyAuthRepository.getMasterPasswordSalt()).thenReturn(salt)
        whenever(readonlyAuthRepository.getMasterPasswordKek()).thenReturn(byteArrayOf(10, 11, 12))
        whenever(sourceKeyService.decryptKekWithSource(any(), any(), any())).thenReturn(decryptedKek)
        whenever(hardwareBackedKeyRepository.containsKey(any())).thenReturn(false)
        whenever(keyGeneratorService.getKeyGenParameterSpecForSecureKey(any(), any())).thenReturn(keyGenSpec)
        whenever(hardwareBackedKeyRepository.generateNewKey(any(), any(), any())).thenReturn(secretKey)
        whenever(cipherService.encrypt(decryptedKek, secretKey)).thenReturn(encryptedKek)

        useCase.save(session)

        // KEK bytes should be zeroed
        decryptedKek.forEach { byte ->
            Assert.assertEquals(0, byte.toInt())
        }
    }

}
