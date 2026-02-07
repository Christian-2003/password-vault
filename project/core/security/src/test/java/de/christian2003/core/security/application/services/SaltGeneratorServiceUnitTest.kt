package de.christian2003.core.security.application.services

import org.junit.Assert
import org.junit.Test


class SaltGeneratorServiceUnitTest {

    private val service = SaltGeneratorService()

    @Test
    fun `generateSalt should return 32 bytes`() {
        val salt = service.generateSalt()

        Assert.assertEquals(32, salt.size)
    }


    @Test
    fun `generateSalt should not return all zero bytes`() {
        val salt = service.generateSalt()

        val allZero = salt.all { it == 0.toByte() }
        Assert.assertFalse(allZero)
    }


    @Test
    fun `generateSalt should return different values on consecutive calls`() {
        val salt1 = service.generateSalt()
        val salt2 = service.generateSalt()

        Assert.assertFalse(salt1.contentEquals(salt2))
    }


    @Test
    fun `generateSalt should return a new array instance each time`() {
        val salt1 = service.generateSalt()
        val salt2 = service.generateSalt()

        Assert.assertNotSame(salt1, salt2)
    }

}
