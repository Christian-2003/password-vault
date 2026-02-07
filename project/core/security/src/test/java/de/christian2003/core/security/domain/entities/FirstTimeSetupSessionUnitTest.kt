package de.christian2003.core.security.domain.entities

import org.junit.Assert
import org.junit.Test


class FirstTimeSetupSessionUnitTest {

    @Test
    fun `constructor should create session when inputs are valid`() {
        val master = charArrayOf('p', 'a', 's', 's')
        val codes = listOf(
            charArrayOf('1', '2', '3'),
            charArrayOf('4', '5', '6')
        )

        val session = FirstTimeSetupSession(master, codes, useBiometrics = true)

        Assert.assertEquals(master.contentToString(), session.masterPassword.contentToString())
        Assert.assertEquals(codes.size, session.recoveryCodes.size)
        Assert.assertEquals(true, session.useBiometrics)
    }


    @Test(expected = IllegalArgumentException::class)
    fun `constructor should throw when master password is empty`() {
        FirstTimeSetupSession(
            masterPassword = charArrayOf(),
            recoveryCodes = listOf(charArrayOf('1', '2', '3')),
            useBiometrics = false
        )
    }


    @Test(expected = IllegalArgumentException::class)
    fun `constructor should throw when recovery codes list is empty`() {
        FirstTimeSetupSession(
            masterPassword = charArrayOf('p', 'a', 's', 's'),
            recoveryCodes = emptyList(),
            useBiometrics = false
        )
    }


    @Test(expected = IllegalArgumentException::class)
    fun `constructor should throw when any recovery code is empty`() {
        FirstTimeSetupSession(
            masterPassword = charArrayOf('p', 'a', 's', 's'),
            recoveryCodes = listOf(charArrayOf('1', '2', '3'), charArrayOf()),
            useBiometrics = false
        )
    }


    @Test
    fun `equals should return true for same content`() {
        val session1 = FirstTimeSetupSession(
            masterPassword = charArrayOf('a', 'b', 'c'),
            recoveryCodes = listOf(
                charArrayOf('1', '2', '3'),
                charArrayOf('4', '5', '6')
            ),
            useBiometrics = true
        )

        val session2 = FirstTimeSetupSession(
            masterPassword = charArrayOf('a', 'b', 'c'),
            recoveryCodes = listOf(
                charArrayOf('1', '2', '3'),
                charArrayOf('4', '5', '6')
            ),
            useBiometrics = true
        )

        Assert.assertTrue(session1 == session2)
        Assert.assertEquals(session1.hashCode(), session2.hashCode())
    }


    @Test
    fun `equals should return false when biometrics flag differs`() {
        val baseCodes = listOf(charArrayOf('1'), charArrayOf('2'))

        val session1 = FirstTimeSetupSession(
            masterPassword = charArrayOf('a'),
            recoveryCodes = baseCodes,
            useBiometrics = true
        )

        val session2 = FirstTimeSetupSession(
            masterPassword = charArrayOf('a'),
            recoveryCodes = baseCodes,
            useBiometrics = false
        )

        Assert.assertFalse(session1 == session2)
    }


    @Test
    fun `equals should return false when master password differs`() {
        val codes = listOf(charArrayOf('1'), charArrayOf('2'))

        val session1 = FirstTimeSetupSession(
            masterPassword = charArrayOf('a'),
            recoveryCodes = codes,
            useBiometrics = true
        )

        val session2 = FirstTimeSetupSession(
            masterPassword = charArrayOf('b'),
            recoveryCodes = codes,
            useBiometrics = true
        )

        Assert.assertFalse(session1 == session2)
    }


    @Test
    fun `equals should return false when recovery codes differ`() {
        val session1 = FirstTimeSetupSession(
            masterPassword = charArrayOf('a'),
            recoveryCodes = listOf(charArrayOf('1'), charArrayOf('2')),
            useBiometrics = true
        )

        val session2 = FirstTimeSetupSession(
            masterPassword = charArrayOf('a'),
            recoveryCodes = listOf(charArrayOf('9'), charArrayOf('9')),
            useBiometrics = true
        )

        Assert.assertFalse(session1 == session2)
    }


    @Test
    fun `equals should return false for different type`() {
        val session = FirstTimeSetupSession(
            masterPassword = charArrayOf('a'),
            recoveryCodes = listOf(charArrayOf('1')),
            useBiometrics = true
        )

        Assert.assertFalse(session.equals("not a session"))
    }


    @Test
    fun `clear should wipe master password and recovery codes`() {
        val master = charArrayOf('s', 'e', 'c', 'r', 'e', 't')
        val code1 = charArrayOf('1', '2', '3')
        val code2 = charArrayOf('4', '5', '6')

        val session = FirstTimeSetupSession(
            masterPassword = master,
            recoveryCodes = listOf(code1, code2),
            useBiometrics = true
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

        val session = FirstTimeSetupSession(
            masterPassword = master,
            recoveryCodes = listOf(code1, code2),
            useBiometrics = false
        )

        session.clear()

        Assert.assertTrue(master.all { it == '\u0000' })
        Assert.assertTrue(code1.all { it == '\u0000' })
        Assert.assertTrue(code2.all { it == '\u0000' })
    }

}
