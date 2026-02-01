package de.christian2003.security.infrastructure.services

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AesKeyGeneratorServiceInstrumentedTest {

    private lateinit var keyGeneratorService: AesKeyGeneratorService


    @Before
    fun setup() {
        keyGeneratorService = AesKeyGeneratorService()
    }


    @Test
    fun `generate returns 32 byte key`() = runBlocking {
        val keyBytes = keyGeneratorService.generate()
        Assert.assertNotNull(keyBytes)
        Assert.assertEquals(32, keyBytes.size) // 256 bits / 8
    }


    @Test
    fun `generate keys are different`() = runBlocking {
        val key1 = keyGeneratorService.generate()
        val key2 = keyGeneratorService.generate()
        Assert.assertFalse(key1.contentEquals(key2))
    }


    @Test
    fun `getKeyGenParameterSpec returns correct spec`() {
        val alias = "my_aes_key"
        val spec: KeyGenParameterSpec = keyGeneratorService.getKeyGenParameterSpec(alias)

        Assert.assertEquals(alias, spec.keystoreAlias)
        Assert.assertEquals(KeyProperties.BLOCK_MODE_GCM, spec.blockModes[0])
        Assert.assertEquals(KeyProperties.ENCRYPTION_PADDING_NONE, spec.encryptionPaddings[0])
        Assert.assertEquals(KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT, spec.purposes)
        Assert.assertFalse(spec.isUserAuthenticationRequired)
    }


    @Test
    fun `getKeyGenParameterSpecForSecureKey returns spec with auth`() {
        val alias = "secure_key"
        val timeout = 60
        val spec: KeyGenParameterSpec = keyGeneratorService.getKeyGenParameterSpecForSecureKey(alias, timeout)

        Assert.assertEquals(alias, spec.keystoreAlias)
        Assert.assertEquals(KeyProperties.BLOCK_MODE_GCM, spec.blockModes[0])
        Assert.assertEquals(KeyProperties.ENCRYPTION_PADDING_NONE, spec.encryptionPaddings[0])
        Assert.assertEquals(KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT, spec.purposes)
        Assert.assertTrue(spec.isUserAuthenticationRequired)
        Assert.assertEquals(timeout, spec.userAuthenticationValidityDurationSeconds)
        Assert.assertFalse(spec.isInvalidatedByBiometricEnrollment)
    }

}
