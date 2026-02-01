package de.christian2003.security.infrastructure.services

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.security.InvalidKeyException
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


class AesCipherServiceUnitTest {

    private lateinit var cipherService: AesCipherService
    private lateinit var secretKey: SecretKey


    @Before
    fun setup() {
        cipherService = AesCipherService()
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        secretKey = keyGen.generateKey()
    }


    @Test
    fun `encrypt and decrypt ByteArray with SecretKey returns original`() {
        val plain = "Hello AES GCM!".toByteArray()
        val cipher = runBlocking { cipherService.encrypt(plain, secretKey) }
        val decrypted = runBlocking { cipherService.decrypt(cipher, secretKey) }
        Assert.assertArrayEquals(plain, decrypted)
    }


    @Test
    fun `encrypt and decrypt ByteArray with keyBytes returns original`() {
        val plain = "Hello AES GCM with bytes!".toByteArray()
        val keyBytes = secretKey.encoded
        val cipher = runBlocking { cipherService.encrypt(plain, keyBytes) }
        val decrypted = runBlocking { cipherService.decrypt(cipher, keyBytes) }
        Assert.assertArrayEquals(plain, decrypted)
    }


    @Test(expected = InvalidKeyException::class)
    fun `decrypt with wrong SecretKey throws InvalidKeyException`() {
        val plain = "Secret message".toByteArray()
        val cipher: ByteArray = runBlocking { cipherService.encrypt(plain, secretKey) }

        val wrongKey = KeyGenerator.getInstance("AES").apply { init(256) }.generateKey()
        runBlocking { cipherService.decrypt(cipher, wrongKey) }
    }


    @Test(expected = InvalidKeyException::class)
    fun `decrypt with wrong keyBytes throws InvalidKeyException`() {
        val plain = "Secret message bytes".toByteArray()
        val cipher: ByteArray = runBlocking { cipherService.encrypt(plain, secretKey.encoded) }

        val wrongKeyBytes = ByteArray(32)
        runBlocking { cipherService.decrypt(cipher, wrongKeyBytes) }
    }


    @Test
    fun `encryption output includes IV prefix`() {
        val plain = "Test IV".toByteArray()
        val cipher: ByteArray = runBlocking { cipherService.encrypt(plain, secretKey) }
        // IV should be 12 bytes prefix
        Assert.assertEquals(12 + plain.size + 16, cipher.size)
        // Note: GCM tag is 16 bytes
    }


    @Test
    fun `decrypt restores original for multiple messages`() {
        val messages = listOf(
            "Short".toByteArray(),
            "Medium length message for AES GCM testing".toByteArray(),
            ByteArray(1024) { it.toByte() } // 1KB of sequential bytes
        )

        for (msg in messages) {
            val cipher: ByteArray = runBlocking { cipherService.encrypt(msg, secretKey) }
            val decrypted: ByteArray = runBlocking { cipherService.decrypt(cipher, secretKey) }
            Assert.assertArrayEquals(msg, decrypted)
        }
    }

}
