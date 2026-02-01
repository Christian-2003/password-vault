package de.christian2003.security.domain.entities

import org.junit.Assert
import org.junit.Test


class EnableBiometricsSessionUnitTest {

    @Test
    fun `init throws exception if master password is empty`() {
        try {
            EnableBiometricsSession(charArrayOf())
            Assert.fail("Expected IllegalArgumentException for empty master password")
        } catch (e: IllegalArgumentException) {
            Assert.assertEquals("Master password cannot be empty", e.message)
        }
    }


    @Test
    fun `equals returns true for sessions with same master password`() {
        val password1 = charArrayOf('a', 'b', 'c')
        val password2 = charArrayOf('a', 'b', 'c')

        val session1 = EnableBiometricsSession(password1)
        val session2 = EnableBiometricsSession(password2)

        Assert.assertEquals(session1, session2)
        Assert.assertEquals(session1.hashCode(), session2.hashCode())
    }


    @Test
    fun `equals returns false for sessions with different master passwords`() {
        val session1 = EnableBiometricsSession(charArrayOf('a', 'b', 'c'))
        val session2 = EnableBiometricsSession(charArrayOf('x', 'y', 'z'))

        Assert.assertFalse(session1 == session2)
        Assert.assertFalse(session1.hashCode() == session2.hashCode())
    }


    @Test
    fun `equals returns true for same object reference`() {
        val session = EnableBiometricsSession(charArrayOf('1','2','3'))
        Assert.assertEquals(session, session)
    }


    @Test
    fun `equals returns false for different type`() {
        val session = EnableBiometricsSession(charArrayOf('1','2','3'))
        val other = "not a session"
        Assert.assertFalse(session.equals(other))
    }


    @Test
    fun `clear should wipe password`() {
        val current = charArrayOf('s', 'e', 'c', 'r', 'e', 't')

        val session = EnableBiometricsSession(current)

        session.clear()

        Assert.assertTrue(session.masterPassword.all { it == '\u0000' })
    }


    @Test
    fun `clear should mutate original array`() {
        val current = charArrayOf('o', 'l', 'd')

        val session = EnableBiometricsSession(current)
        session.clear()

        Assert.assertTrue(current.all { it == '\u0000' })
    }

}
