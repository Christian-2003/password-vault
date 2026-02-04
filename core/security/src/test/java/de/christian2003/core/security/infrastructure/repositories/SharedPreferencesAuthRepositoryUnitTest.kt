package de.christian2003.core.security.infrastructure.repositories

import android.content.Context
import android.content.SharedPreferences
import de.christian2003.core.security.domain.exceptions.AuthTransactionException
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.isNull
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Base64


class SharedPreferencesAuthRepositoryUnitTest {

    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var repository: SharedPreferencesAuthRepository


    @Before
    fun setup() {
        context = mock(Context::class.java)
        sharedPreferences = mock(SharedPreferences::class.java)
        editor = mock(SharedPreferences.Editor::class.java)

        whenever(context.getSharedPreferences(any(), any())).thenReturn(sharedPreferences)
        whenever(sharedPreferences.edit()).thenReturn(editor)
        whenever(editor.putString(any(), any())).thenReturn(editor)
        whenever(editor.putInt(any(), any())).thenReturn(editor)
        whenever(editor.remove(any())).thenReturn(editor)

        repository = SharedPreferencesAuthRepository(context)
    }


    @Test
    fun `beginTransaction should reset transaction state`() {
        repository.beginTransaction()
        repository.setMasterPassword(byteArrayOf(1), byteArrayOf(2))
        repository.addRecoveryCode(byteArrayOf(3), byteArrayOf(4))
        repository.setBiometricsKek(byteArrayOf(5))
        repository.setMasterKey(byteArrayOf(6))
        // No assert here, just ensure no exception
    }


    @Test(expected = AuthTransactionException::class)
    fun `commitTransaction without beginTransaction should throw`() {
        repository.commitTransaction()
    }


    @Test
    fun `setMasterPassword works only in transaction`() {
        repository.beginTransaction()
        val key = byteArrayOf(1,2)
        val salt = byteArrayOf(3,4)
        repository.setMasterPassword(key, salt)
        // committing should call editor.putString with base64 values
        repository.commitTransaction()
        verify(editor).putString(eq("master_password_kek"), any())
        verify(editor).putString(eq("master_password_salt"), any())
    }


    @Test(expected = AuthTransactionException::class)
    fun `setMasterPassword without transaction should throw`() {
        repository.setMasterPassword(byteArrayOf(1), byteArrayOf(2))
    }


    @Test
    fun `addRecoveryCode works in transaction`() {
        repository.beginTransaction()
        repository.addRecoveryCode(byteArrayOf(1), byteArrayOf(2))
        repository.commitTransaction()
        verify(editor).putString(eq("recovery_0_kek"), any())
        verify(editor).putString(eq("recovery_0_salt"), any())
        verify(editor).putInt(eq("recovery_count"), eq(1))
    }


    @Test
    fun `setBiometricsKek works in transaction`() {
        repository.beginTransaction()
        val bytes = byteArrayOf(1,2,3)
        repository.setBiometricsKek(bytes)
        repository.commitTransaction()
        verify(editor).putString(eq("biometrics_kek"), any())
    }


    @Test
    fun `setMasterKey works in transaction`() {
        repository.beginTransaction()
        val bytes = byteArrayOf(9,8,7)
        repository.setMasterKey(bytes)
        repository.commitTransaction()
        verify(editor).putString(eq("master_key"), any())
    }


    @Test
    fun `getMasterPasswordKek reads from SharedPreferences`() {
        val encoded = Base64.getEncoder().encodeToString(byteArrayOf(1,2))
        whenever(sharedPreferences.getString(eq("master_password_kek"), isNull())).thenReturn(encoded)
        val result = repository.getMasterPasswordKek()
        Assert.assertArrayEquals(byteArrayOf(1,2), result)
    }


    @Test
    fun `getMasterPasswordSalt reads from SharedPreferences`() {
        val encoded = Base64.getEncoder().encodeToString(byteArrayOf(3,4))
        whenever(sharedPreferences.getString(eq("master_password_salt"), isNull())).thenReturn(encoded)
        val result = repository.getMasterPasswordSalt()
        Assert.assertArrayEquals(byteArrayOf(3,4), result)
    }


    @Test
    fun `isMasterPasswordConfigured returns true when keys exist`() {
        whenever(sharedPreferences.contains("master_password_kek")).thenReturn(true)
        whenever(sharedPreferences.contains("master_password_salt")).thenReturn(true)
        Assert.assertTrue(repository.isMasterPasswordConfigured())
    }


    @Test
    fun `isMasterPasswordConfigured returns false when missing`() {
        whenever(sharedPreferences.contains("master_password_kek")).thenReturn(true)
        whenever(sharedPreferences.contains("master_password_salt")).thenReturn(false)
        Assert.assertFalse(repository.isMasterPasswordConfigured())
    }


    @Test
    fun `getNumberOfRecoveryCodes reads correct value`() {
        whenever(sharedPreferences.getInt("recovery_count", 0)).thenReturn(5)
        Assert.assertEquals(5, repository.getNumberOfRecoveryCodes())
    }


    @Test
    fun `areRecoveryCodesConfigured returns false when count zero`() {
        whenever(sharedPreferences.getInt("recovery_count", 0)).thenReturn(0)
        Assert.assertFalse(repository.areRecoveryCodesConfigured())
    }


    @Test
    fun `isBiometricsConfigured returns true when present`() {
        whenever(sharedPreferences.contains("biometrics_kek")).thenReturn(true)
        Assert.assertTrue(repository.isBiometricsConfigured())
    }

}
