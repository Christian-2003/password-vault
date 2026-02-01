package de.christian2003.security.application.usecases

import de.christian2003.security.application.services.SaltGeneratorService
import de.christian2003.security.application.services.SourceKeyService
import de.christian2003.security.domain.entities.FirstTimeSetupSession
import de.christian2003.security.domain.exceptions.AuthSetupException
import de.christian2003.security.domain.repositories.AuthTransactionRepository
import de.christian2003.security.domain.repositories.HardwareBackedKeyRepository
import de.christian2003.security.domain.services.CipherService
import de.christian2003.security.domain.services.KdfService
import de.christian2003.security.domain.services.KeyGeneratorService
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


class SaveFirstTimeSetupSessionUseCaseUnitTest {

    private lateinit var authRepository: AuthTransactionRepository
    private lateinit var hardwareBackedKeyRepository: HardwareBackedKeyRepository
    private lateinit var kdfService: KdfService
    private lateinit var cipherService: CipherService
    private lateinit var keyGeneratorService: KeyGeneratorService
    private lateinit var saltGeneratorService: SaltGeneratorService
    private lateinit var sourceKeyService: SourceKeyService
    private lateinit var useCase: SaveFirstTimeSetupSessionUseCase


    @Before
    fun setup() {
        authRepository = mock()
        hardwareBackedKeyRepository = mock()
        kdfService = mock()
        cipherService = mock()
        keyGeneratorService = mock()
        saltGeneratorService = mock()
        sourceKeyService = mock()

        useCase = SaveFirstTimeSetupSessionUseCase(
            authRepository,
            hardwareBackedKeyRepository,
            kdfService,
            cipherService,
            keyGeneratorService,
            saltGeneratorService,
            sourceKeyService
        )
    }


    @Test(expected = IllegalArgumentException::class)
    fun `save throws exception if master password is empty`() = runBlocking {
        val session = FirstTimeSetupSession(
            masterPassword = charArrayOf(),
            recoveryCodes = listOf("code".toCharArray()),
            useBiometrics = false
        )

        try {
            useCase.save(session)
            Assert.fail("Expected AuthSetupException")
        } catch (e: AuthSetupException) {
            Assert.assertEquals("Master password cannot be empty", e.message)
        }
    }


    @Test(expected = IllegalArgumentException::class)
    fun `save throws exception if recovery code is empty`() = runBlocking {
        val session = FirstTimeSetupSession(
            masterPassword = "master".toCharArray(),
            recoveryCodes = listOf(charArrayOf()),
            useBiometrics = false
        )
    }


    @Test
    fun `save successfully commits first-time setup without biometrics`() = runBlocking {
        val masterPassword = "master".toCharArray()
        val recoveryCode = "code".toCharArray()
        val session = FirstTimeSetupSession(masterPassword, listOf(recoveryCode), useBiometrics = false)

        val decryptedKek = byteArrayOf(1, 2, 3)
        val encryptedMasterPassword = byteArrayOf(4, 5, 6)
        val salt = byteArrayOf(7, 8, 9)
        val masterKeyBytes = byteArrayOf(10, 11, 12)

        // Mock services
        whenever(keyGeneratorService.generate()).thenReturn(decryptedKek, masterKeyBytes) // first call KEK, second call master key
        whenever(saltGeneratorService.generateSalt()).thenReturn(salt)
        whenever(sourceKeyService.encryptKekWithSource(any(), any(), any(), any())).thenReturn(encryptedMasterPassword)
        whenever(cipherService.encrypt(any(), any<ByteArray>())).thenReturn(masterKeyBytes)

        useCase.save(session)

        // Verify transaction flow
        verify(authRepository, times(1)).beginTransaction()
        verify(authRepository, times(1)).setMasterPassword(encryptedMasterPassword, salt)
        verify(authRepository, times(1)).addRecoveryCode(encryptedMasterPassword, salt)
        verify(authRepository, times(1)).setMasterKey(masterKeyBytes)
        verify(authRepository, times(1)).commitTransaction()
    }


    @Test
    fun `save commits first-time setup with biometrics`() = runBlocking {
        val masterPassword = "master".toCharArray()
        val recoveryCode = "code".toCharArray()
        val session = FirstTimeSetupSession(masterPassword, listOf(recoveryCode), useBiometrics = true)

        val decryptedKek = byteArrayOf(1, 2, 3)
        val encryptedMasterPassword = byteArrayOf(4, 5, 6)
        val salt = byteArrayOf(7, 8, 9)
        val masterKeyBytes = byteArrayOf(10, 11, 12)
        val biometricsKey: SecretKey = mock()
        val encryptedKekForBiometrics = byteArrayOf(13, 14, 15)

        // Mock services
        whenever(keyGeneratorService.generate()).thenReturn(decryptedKek, masterKeyBytes)
        whenever(saltGeneratorService.generateSalt()).thenReturn(salt)
        whenever(sourceKeyService.encryptKekWithSource(any(), any(), any(), any())).thenReturn(encryptedMasterPassword)
        whenever(cipherService.encrypt(any(), any<ByteArray>())).thenReturn(masterKeyBytes, encryptedKekForBiometrics)
        whenever(hardwareBackedKeyRepository.containsKey(any())).thenReturn(false)
        whenever(hardwareBackedKeyRepository.generateNewKey(any(), any(), any())).thenReturn(biometricsKey)

        useCase.save(session)

        // Verify all main calls
        verify(authRepository, times(1)).beginTransaction()
        verify(authRepository, times(1)).setMasterPassword(encryptedMasterPassword, salt)
        verify(authRepository, times(1)).addRecoveryCode(encryptedMasterPassword, salt)
        verify(authRepository, times(1)).setMasterKey(masterKeyBytes)
        verify(authRepository, times(1)).commitTransaction()
    }

}
