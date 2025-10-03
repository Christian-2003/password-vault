package de.christian2003.passwordvault.domain.model.account

import org.junit.Assert
import org.junit.Test


class AccountDescriptorUnitTest {

    @Test
    fun createValidAccountDescriptor() {
        AccountDescriptor(
            name = "GitHub account"
        )
        AccountDescriptor(
            name = "GitHub account",
            description = "Programming"
        )
    }


    @Test
    fun createAccountWithBlankName() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            AccountDescriptor(
                name = " "
            )
        }
    }


    @Test
    fun createAccountWithNoName() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            AccountDescriptor(
                name = ""
            )
        }
    }


    @Test
    fun createAccountWithBlankDescription() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            AccountDescriptor(
                name = "GitHub account",
                description = " "
            )
        }
    }


    @Test
    fun createAccountWithNoDescription() {
        AccountDescriptor(
            name = "GitHub account",
            description = ""
        )
    }

}
