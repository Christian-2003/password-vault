package de.christian2003.core.security.domain.entities

import org.junit.Assert
import org.junit.Test


class SecurityAliasesUnitTest {

    @Test
    fun `getAlias without index returns correct alias`() {
        Assert.assertEquals("hardware_backed_key", SecurityAliases.HardwareBackedKey.getAlias())
        Assert.assertEquals("master_key", SecurityAliases.MasterKey.getAlias())
        Assert.assertEquals("master_password_kek", SecurityAliases.MasterPasswordKek.getAlias())
        Assert.assertEquals("master_password_salt", SecurityAliases.MasterPasswordSalt.getAlias())
        Assert.assertEquals("hardware_backed_key_biometrics", SecurityAliases.BiometricsHardwareBackedKey.getAlias())
        Assert.assertEquals("biometrics_kek", SecurityAliases.BiometricsKek.getAlias())
    }


    @Test
    fun `getAlias with index formats recovery code alias`() {
        val index = 1
        Assert.assertEquals("recovery_1_kek", SecurityAliases.RecoveryCodeKek.getAlias(index))
        Assert.assertEquals("recovery_1_salt", SecurityAliases.RecoveryCodeSalt.getAlias(index))
    }


    @Test
    fun `getAlias with null index returns default alias`() {
        Assert.assertEquals("recovery_%1\$d_kek", SecurityAliases.RecoveryCodeKek.getAlias(null))
        Assert.assertEquals("recovery_%1\$d_salt", SecurityAliases.RecoveryCodeSalt.getAlias(null))
    }


    @Test
    fun `getAlias with multiple indices formats correctly`() {
        // Just to show formatting works for different numbers
        Assert.assertEquals("recovery_5_kek", SecurityAliases.RecoveryCodeKek.getAlias(5))
        Assert.assertEquals("recovery_42_salt", SecurityAliases.RecoveryCodeSalt.getAlias(42))
    }

}
