package de.christian2003.security.domain.entities

import org.junit.Assert
import org.junit.Test


class RecoverySessionUnitTest {

    @Test
    fun `constructor should create session when inputs are valid`() {
        val recovery = charArrayOf('r','e','c')
        val newPassword = charArrayOf('n','e','w')
        val codes = listOf(charArrayOf('1','2'), charArrayOf('3','4'))

        val session = RecoverySession(recovery, newPassword, codes)

        Assert.assertEquals(recovery.contentToString(), session.recoveryCode.contentToString())
        Assert.assertEquals(newPassword.contentToString(), session.newMasterPassword.contentToString())
        Assert.assertEquals(codes.size, session.newRecoveryCodes.size)
    }


    @Test(expected = IllegalArgumentException::class)
    fun `constructor should throw when recovery code is empty`() {
        RecoverySession(
            recoveryCode = charArrayOf(),
            newMasterPassword = charArrayOf('n','e','w'),
            newRecoveryCodes = listOf(charArrayOf('1'))
        )
    }


    @Test(expected = IllegalArgumentException::class)
    fun `constructor should throw when new master password is empty`() {
        RecoverySession(
            recoveryCode = charArrayOf('r','e','c'),
            newMasterPassword = charArrayOf(),
            newRecoveryCodes = listOf(charArrayOf('1'))
        )
    }


    @Test(expected = IllegalArgumentException::class)
    fun `constructor should throw when new recovery codes list is empty`() {
        RecoverySession(
            recoveryCode = charArrayOf('r'),
            newMasterPassword = charArrayOf('n'),
            newRecoveryCodes = emptyList()
        )
    }


    @Test(expected = IllegalArgumentException::class)
    fun `constructor should throw when any new recovery code is empty`() {
        RecoverySession(
            recoveryCode = charArrayOf('r'),
            newMasterPassword = charArrayOf('n'),
            newRecoveryCodes = listOf(charArrayOf('1'), charArrayOf())
        )
    }


    @Test
    fun `equals should return true for same content`() {
        val session1 = RecoverySession(
            recoveryCode = charArrayOf('r','e','c'),
            newMasterPassword = charArrayOf('n','e','w'),
            newRecoveryCodes = listOf(charArrayOf('1'), charArrayOf('2'))
        )

        val session2 = RecoverySession(
            recoveryCode = charArrayOf('r','e','c'),
            newMasterPassword = charArrayOf('n','e','w'),
            newRecoveryCodes = listOf(charArrayOf('1'), charArrayOf('2'))
        )

        Assert.assertTrue(session1 == session2)
    }


    @Test
    fun `equals should return false when recovery code differs`() {
        val codes = listOf(charArrayOf('1'))
        val session1 = RecoverySession(charArrayOf('a'), charArrayOf('n'), codes)
        val session2 = RecoverySession(charArrayOf('b'), charArrayOf('n'), codes)

        Assert.assertFalse(session1 == session2)
    }


    @Test
    fun `equals should return false when new master password differs`() {
        val codes = listOf(charArrayOf('1'))
        val session1 = RecoverySession(charArrayOf('r'), charArrayOf('n'), codes)
        val session2 = RecoverySession(charArrayOf('r'), charArrayOf('x'), codes)

        Assert.assertFalse(session1 == session2)
    }


    @Test
    fun `equals should return false when new recovery codes differ`() {
        val session1 = RecoverySession(charArrayOf('r'), charArrayOf('n'), listOf(charArrayOf('1')))
        val session2 = RecoverySession(charArrayOf('r'), charArrayOf('n'), listOf(charArrayOf('9')))

        Assert.assertFalse(session1 == session2)
    }


    @Test
    fun `equals should return false for different type`() {
        val session = RecoverySession(charArrayOf('r'), charArrayOf('n'), listOf(charArrayOf('1')))
        Assert.assertFalse(session.equals("not a session"))
    }


    @Test
    fun `clear should wipe all sensitive data`() {
        val recovery = charArrayOf('r','e','c')
        val newPassword = charArrayOf('n','e','w')
        val code1 = charArrayOf('1','2')
        val code2 = charArrayOf('3','4')

        val session = RecoverySession(recovery, newPassword, listOf(code1, code2))
        session.clear()

        Assert.assertTrue(session.recoveryCode.all { it == '\u0000' })
        Assert.assertTrue(session.newMasterPassword.all { it == '\u0000' })
        Assert.assertTrue(session.newRecoveryCodes.all { code -> code.all { it == '\u0000' } })
    }


    @Test
    fun `clear should mutate original arrays`() {
        val recovery = charArrayOf('r','e','c')
        val newPassword = charArrayOf('n','e','w')
        val code1 = charArrayOf('1','2')
        val code2 = charArrayOf('3','4')

        val session = RecoverySession(recovery, newPassword, listOf(code1, code2))
        session.clear()

        Assert.assertTrue(recovery.all { it == '\u0000' })
        Assert.assertTrue(newPassword.all { it == '\u0000' })
        Assert.assertTrue(code1.all { it == '\u0000' })
        Assert.assertTrue(code2.all { it == '\u0000' })
    }

}
