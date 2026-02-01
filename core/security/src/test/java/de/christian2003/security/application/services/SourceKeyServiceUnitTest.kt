package de.christian2003.security.application.services

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import de.christian2003.security.domain.entities.SecurityAliases
import de.christian2003.security.domain.exceptions.AuthSetupException
import de.christian2003.security.domain.exceptions.UnlockFailedException
import de.christian2003.security.domain.exceptions.UnlockSourceInvalidException
import de.christian2003.security.domain.repositories.HardwareBackedKeyRepository
import de.christian2003.security.domain.services.CipherService
import de.christian2003.security.domain.services.KdfService
import de.christian2003.security.domain.services.KeyGeneratorService
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import javax.crypto.SecretKey


class SourceKeyServiceUnitTest {

    private val hardwareBackedKeyRepository: HardwareBackedKeyRepository = mock()
    private val cipherService: CipherService = mock()
    private val kdfService: KdfService = mock()
    private val keyGeneratorService: KeyGeneratorService = mock()

    private lateinit var service: SourceKeyService

    private val alias = SecurityAliases.HardwareBackedKey.getAlias()
    private val hardwareKey: SecretKey = mock()


    @Before
    fun setup() {
        service = SourceKeyService(
            hardwareBackedKeyRepository,
            cipherService,
            kdfService,
            keyGeneratorService
        )
    }


    @Test
    fun `encryptKekWithSource - happy path`() = runTest {
        val source = charArrayOf('p', 'a', 's', 's')
        val salt = byteArrayOf(1, 2, 3)
        val kek = byteArrayOf(9, 9, 9)

        val derivedKey = byteArrayOf(4, 4, 4)
        val partlyEncrypted = byteArrayOf(5, 5, 5)
        val fullyEncrypted = byteArrayOf(6, 6, 6)

        whenever(hardwareBackedKeyRepository.containsKey(alias)).thenReturn(true)
        whenever(hardwareBackedKeyRepository.getKey(alias)).thenReturn(hardwareKey)
        whenever(kdfService.derive(source, salt)).thenReturn(derivedKey)
        whenever(cipherService.encrypt(kek, derivedKey)).thenReturn(partlyEncrypted)
        whenever(cipherService.encrypt(partlyEncrypted, hardwareKey)).thenReturn(fullyEncrypted)

        val result = service.encryptKekWithSource(source, salt, kek)

        Assert.assertArrayEquals(fullyEncrypted, result)
        verify(cipherService).encrypt(kek, derivedKey)
        verify(cipherService).encrypt(partlyEncrypted, hardwareKey)
    }


    @Test
    fun `encryptKekWithSource - throws when hardware key missing`() {
        whenever(hardwareBackedKeyRepository.containsKey(alias)).thenReturn(false)

        Assert.assertThrows(AuthSetupException::class.java) {
            runTest {
                service.encryptKekWithSource(charArrayOf('x'), byteArrayOf(1), byteArrayOf(2))
            }
        }
    }


    @Test
    fun `encryptKekWithSource - throws when kdf fails`() {
        runTest {
            whenever(hardwareBackedKeyRepository.containsKey(alias)).thenReturn(true)
            whenever(hardwareBackedKeyRepository.getKey(alias)).thenReturn(hardwareKey)
            whenever(kdfService.derive(any<CharArray>(), any<ByteArray>())).thenThrow(RuntimeException("boom"))
        }

        Assert.assertThrows(AuthSetupException::class.java) {
            runTest {
                service.encryptKekWithSource(charArrayOf('x'), byteArrayOf(1), byteArrayOf(2))
            }
        }
    }


    @Test
    fun `encryptKekWithSource - generates hardware key when required`() = runTest {
        val spec = mock<KeyGenParameterSpec>()
        val generatedKey: SecretKey = mock()

        whenever(hardwareBackedKeyRepository.containsKey(alias)).thenReturn(false)
        whenever(keyGeneratorService.getKeyGenParameterSpec(alias)).thenReturn(spec)
        whenever(hardwareBackedKeyRepository.generateNewKey(alias, KeyProperties.KEY_ALGORITHM_AES, spec))
            .thenReturn(generatedKey)

        whenever(kdfService.derive(any<CharArray>(), any<ByteArray>())).thenReturn(byteArrayOf(1))
        whenever(cipherService.encrypt(any(), any<ByteArray>())).thenReturn(byteArrayOf(2))
        whenever(cipherService.encrypt(any(), any<SecretKey>())).thenReturn(byteArrayOf(3))

        val result = service.encryptKekWithSource(charArrayOf('x'), byteArrayOf(1), byteArrayOf(2), true)

        Assert.assertNotNull(result)
        verify(hardwareBackedKeyRepository).generateNewKey(eq(alias), eq(KeyProperties.KEY_ALGORITHM_AES), eq(spec))
    }


    @Test
    fun `decryptKekWithSource - happy path`() = runTest {
        val encryptedKek = byteArrayOf(1, 1, 1)
        val source = charArrayOf('p', 'a', 's', 's')
        val salt = byteArrayOf(2, 2, 2)

        val derivedKey = byteArrayOf(3, 3, 3)
        val partlyDecrypted = byteArrayOf(4, 4, 4)
        val fullyDecrypted = byteArrayOf(5, 5, 5)

        whenever(hardwareBackedKeyRepository.containsKey(alias)).thenReturn(true)
        whenever(hardwareBackedKeyRepository.getKey(alias)).thenReturn(hardwareKey)
        whenever(kdfService.derive(source, salt)).thenReturn(derivedKey)
        whenever(cipherService.decrypt(encryptedKek, hardwareKey)).thenReturn(partlyDecrypted)
        whenever(cipherService.decrypt(partlyDecrypted, derivedKey)).thenReturn(fullyDecrypted)

        val result = service.decryptKekWithSource(encryptedKek, source, salt)

        Assert.assertArrayEquals(fullyDecrypted, result)
    }


    @Test
    fun `decryptKekWithSource - throws when hardware key missing`() {
        whenever(hardwareBackedKeyRepository.containsKey(alias)).thenReturn(false)

        Assert.assertThrows(UnlockFailedException::class.java) {
            runTest {
                service.decryptKekWithSource(byteArrayOf(1), charArrayOf('x'), byteArrayOf(1))
            }
        }
    }


    @Test
    fun `decryptKekWithSource - throws when source invalid`() {
        runTest {
            whenever(hardwareBackedKeyRepository.containsKey(alias)).thenReturn(true)
            whenever(hardwareBackedKeyRepository.getKey(alias)).thenReturn(hardwareKey)
            whenever(kdfService.derive(any<CharArray>(), any<ByteArray>())).thenThrow(RuntimeException("bad source"))
        }

        Assert.assertThrows(UnlockSourceInvalidException::class.java) {
            runTest {
                service.decryptKekWithSource(byteArrayOf(1), charArrayOf('x'), byteArrayOf(1))
            }
        }
    }


    @Test
    fun `encryptKekWithSource zeroizes sensitive buffers`() = runTest {
        val sourceKey = byteArrayOf(9, 9, 9)
        val partlyEncrypted = byteArrayOf(8, 8, 8)

        whenever(hardwareBackedKeyRepository.containsKey(alias)).thenReturn(true)
        whenever(hardwareBackedKeyRepository.getKey(alias)).thenReturn(hardwareKey)
        whenever(kdfService.derive(any<CharArray>(), any<ByteArray>())).thenReturn(sourceKey)
        whenever(cipherService.encrypt(any(), any<ByteArray>())).thenReturn(partlyEncrypted)
        whenever(cipherService.encrypt(any(), any<SecretKey>())).thenReturn(byteArrayOf(1))

        service.encryptKekWithSource(charArrayOf('x'), byteArrayOf(1), byteArrayOf(2))

        Assert.assertTrue(sourceKey.all { it == 0.toByte() })
        Assert.assertTrue(partlyEncrypted.all { it == 0.toByte() })
    }

}
