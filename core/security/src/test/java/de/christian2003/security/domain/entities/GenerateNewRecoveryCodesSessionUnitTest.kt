package de.christian2003.security.domain.entities

import org.junit.Assert
import org.junit.Test


class GenerateNewRecoveryCodesSessionUnitTest {

    @Test
    fun `constructor should create session when inputs are valid`() {
        val master = charArrayOf('p', 'a', 's', 's')
        val codes = listOf(
            charArrayOf('1', '2', '3'),
            charArrayOf('4', '5', '6')
        )

        val session = GenerateNewRecoveryCodesSession(master, codes)

        Assert.assertEquals(master.contentToString(), session.masterPassword.contentToString())
        Assert.assertEquals(codes.size, session.recoveryCodes.size)
    }


    @Test(expected = IllegalArgumentException::class)
    fun `constructor should throw when master password is empty`() {
        GenerateNewRecoveryCodesSession(
            masterPassword = charArrayOf(),
            recoveryCodes = listOf(charArrayOf('1', '2', '3'))
        )
    }


    @Test(expected = IllegalArgumentException::class)
    fun `constructor should throw when recovery codes list is empty`() {
        GenerateNewRecoveryCodesSession(
            masterPassword = charArrayOf('p', 'a', 's', 's'),
            recoveryCodes = emptyList()
        )
    }


    @Test(expected = IllegalArgumentException::class)
    fun `constructor should throw when any recovery code is empty`() {
        GenerateNewRecoveryCodesSession(
            masterPassword = charArrayOf('p', 'a', 's', 's'),
            recoveryCodes = listOf(charArrayOf('1', '2', '3'), charArrayOf())
        )
    }


    @Test
    fun `equals should return true for same content`() {
        val session1 = GenerateNewRecoveryCodesSession(
            masterPassword = charArrayOf('a', 'b', 'c'),
            recoveryCodes = listOf(
                charArrayOf('1', '2', '3'),
                charArrayOf('4', '5', '6')
            )
        )

        val session2 = GenerateNewRecoveryCodesSession(
            masterPassword = charArrayOf('a', 'b', 'c'),
            recoveryCodes = listOf(
                charArrayOf('1', '2', '3'),
                charArrayOf('4', '5', '6')
            )
        )

        Assert.assertTrue(session1 == session2)
        Assert.assertEquals(session1.hashCode(), session2.hashCode())
    }


    @Test
    fun `equals should return false when master password differs`() {
        val codes = listOf(charArrayOf('1'), charArrayOf('2'))

        val session1 = GenerateNewRecoveryCodesSession(
            masterPassword = charArrayOf('a'),
            recoveryCodes = codes
        )

        val session2 = GenerateNewRecoveryCodesSession(
            masterPassword = charArrayOf('b'),
            recoveryCodes = codes
        )

        Assert.assertFalse(session1 == session2)
    }


    @Test
    fun `equals should return false when recovery codes differ`() {
        val session1 = GenerateNewRecoveryCodesSession(
            masterPassword = charArrayOf('a'),
            recoveryCodes = listOf(charArrayOf('1'), charArrayOf('2'))
        )

        val session2 = GenerateNewRecoveryCodesSession(
            masterPassword = charArrayOf('a'),
            recoveryCodes = listOf(charArrayOf('9'), charArrayOf('9'))
        )

        Assert.assertFalse(session1 == session2)
    }


    @Test
    fun `equals should return false for different type`() {
        val session = GenerateNewRecoveryCodesSession(
            masterPassword = charArrayOf('a'),
            recoveryCodes = listOf(charArrayOf('1'))
        )

        Assert.assertFalse(session.equals("not a session"))
    }


    @Test
    fun `clear should wipe master password and recovery codes`() {
        val master = charArrayOf('s', 'e', 'c', 'r', 'e', 't')
        val code1 = charArrayOf('1', '2', '3')
        val code2 = charArrayOf('4', '5', '6')

        val session = GenerateNewRecoveryCodesSession(
            masterPassword = master,
            recoveryCodes = listOf(code1, code2)
        )

        session.clear()

        Assert.assertTrue(session.masterPassword.all { it == '\u0000' })
        Assert.assertTrue(session.recoveryCodes.all { code -> code.all { it == '\u0000' } })
    }


    @Test
    fun `clear should mutate original arrays`() {
        val master = charArrayOf('a', 'b', 'c')
        val code1 = charArrayOf('1', '2', '3')
        val code2 = charArrayOf('4', '5', '6')

        val session = GenerateNewRecoveryCodesSession(
            masterPassword = master,
            recoveryCodes = listOf(code1, code2)
        )

        session.clear()

        Assert.assertTrue(master.all { it == '\u0000' })
        Assert.assertTrue(code1.all { it == '\u0000' })
        Assert.assertTrue(code2.all { it == '\u0000' })
    }

}
