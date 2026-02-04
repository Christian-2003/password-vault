package de.christian2003.core.security.domain.entities

import org.junit.Assert
import org.junit.Test


class ChangePasswordSessionUnitTest {

    @Test
    fun `constructor should create session when passwords are not empty`() {
        val current = charArrayOf('o', 'l', 'd')
        val new = charArrayOf('n', 'e', 'w')

        val session = ChangePasswordSession(current, new)

        Assert.assertEquals(current.contentToString(), session.currentMasterPassword.contentToString())
        Assert.assertEquals(new.contentToString(), session.newMasterPassword.contentToString())
    }


    @Test(expected = IllegalArgumentException::class)
    fun `constructor should throw when current password is empty`() {
        ChangePasswordSession(charArrayOf(), charArrayOf('n', 'e', 'w'))
    }


    @Test(expected = IllegalArgumentException::class)
    fun `constructor should throw when new password is empty`() {
        ChangePasswordSession(charArrayOf('o', 'l', 'd'), charArrayOf())
    }


    @Test
    fun `equals should return true for same content`() {
        val session1 = ChangePasswordSession(
            charArrayOf('a', 'b', 'c'),
            charArrayOf('1', '2', '3')
        )

        val session2 = ChangePasswordSession(
            charArrayOf('a', 'b', 'c'),
            charArrayOf('1', '2', '3')
        )

        Assert.assertTrue(session1 == session2)
        Assert.assertEquals(session1.hashCode(), session2.hashCode())
    }


    @Test
    fun `equals should return false for different current password`() {
        val session1 = ChangePasswordSession(
            charArrayOf('a', 'b', 'c'),
            charArrayOf('1', '2', '3')
        )

        val session2 = ChangePasswordSession(
            charArrayOf('x', 'y', 'z'),
            charArrayOf('1', '2', '3')
        )

        Assert.assertFalse(session1 == session2)
    }


    @Test
    fun `equals should return false for different new password`() {
        val session1 = ChangePasswordSession(
            charArrayOf('a', 'b', 'c'),
            charArrayOf('1', '2', '3')
        )

        val session2 = ChangePasswordSession(
            charArrayOf('a', 'b', 'c'),
            charArrayOf('9', '9', '9')
        )

        Assert.assertFalse(session1 == session2)
    }


    @Test
    fun `equals should return false for different type`() {
        val session = ChangePasswordSession(
            charArrayOf('a', 'b', 'c'),
            charArrayOf('1', '2', '3')
        )

        Assert.assertFalse(session.equals("not a session"))
    }


    @Test
    fun `clear should wipe both passwords`() {
        val current = charArrayOf('s', 'e', 'c', 'r', 'e', 't')
        val new = charArrayOf('n', 'e', 'w', 's', 'e', 'c')

        val session = ChangePasswordSession(current, new)

        session.clear()

        Assert.assertTrue(session.currentMasterPassword.all { it == '\u0000' })
        Assert.assertTrue(session.newMasterPassword.all { it == '\u0000' })
    }


    @Test
    fun `clear should mutate original arrays`() {
        val current = charArrayOf('o', 'l', 'd')
        val new = charArrayOf('n', 'e', 'w')

        val session = ChangePasswordSession(current, new)
        session.clear()

        Assert.assertTrue(current.all { it == '\u0000' })
        Assert.assertTrue(new.all { it == '\u0000' })
    }

}
