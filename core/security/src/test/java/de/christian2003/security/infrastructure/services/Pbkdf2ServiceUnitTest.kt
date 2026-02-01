package de.christian2003.security.infrastructure.services

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test


class Pbkdf2ServiceUnitTest {

    private lateinit var kdfService: Pbkdf2Service


    @Before
    fun setup() {
        kdfService = Pbkdf2Service()
    }


    @Test
    fun `derive with CharArray returns 32 byte key`() = runBlocking {
        val input = "password123".toCharArray()
        val salt = "salty_salt".toByteArray()
        val derived = kdfService.derive(input, salt)

        Assert.assertNotNull(derived)
        Assert.assertEquals(32, derived.size) // 256-bit key
    }


    @Test
    fun `derive with ByteArray returns same as CharArray`() = runBlocking {
        val inputStr = "password123"
        val inputBytes = inputStr.toByteArray()
        val inputChars = inputStr.toCharArray()
        val salt = "salty_salt".toByteArray()

        val derivedFromBytes = kdfService.derive(inputBytes, salt)
        val derivedFromChars = kdfService.derive(inputChars, salt)

        Assert.assertArrayEquals(derivedFromChars, derivedFromBytes)
    }


    @Test
    fun `derive is deterministic for same input and salt`() = runBlocking {
        val input = "deterministic".toCharArray()
        val salt = "fixed_salt".toByteArray()

        val derived1 = kdfService.derive(input, salt)
        val derived2 = kdfService.derive(input, salt)

        Assert.assertArrayEquals(derived1, derived2)
    }


    @Test
    fun `derive produces different keys for different salts`() = runBlocking {
        val input = "same_input".toCharArray()
        val salt1 = "salt1".toByteArray()
        val salt2 = "salt2".toByteArray()

        val key1 = kdfService.derive(input, salt1)
        val key2 = kdfService.derive(input, salt2)

        Assert.assertFalse(key1.contentEquals(key2))
    }


    @Test
    fun `derive with empty input returns a key`() = runBlocking {
        val input = CharArray(0)
        val salt = "some_salt".toByteArray()

        val derived = kdfService.derive(input, salt)
        Assert.assertNotNull(derived)
        Assert.assertEquals(32, derived.size)
    }


    @Test(expected = IllegalArgumentException::class)
    fun `derive with empty salt throws`() = runBlocking {
        val input = "password".toCharArray()
        val salt = ByteArray(0)

        val derived = kdfService.derive(input, salt)
        Assert.assertNotNull(derived)
        Assert.assertEquals(32, derived.size)
    }

}
