package de.christian2003.security.infrastructure.repositories.dto

import org.junit.Assert
import org.junit.Test


class AuthRepositoryKekItemUnitTest {

    @Test
    fun `constructor should store keyBytes and salt correctly`() {
        val key = byteArrayOf(1, 2, 3)
        val salt = byteArrayOf(4, 5, 6)

        val item = AuthRepositoryKekItem(key, salt)

        Assert.assertEquals(key.contentToString(), item.keyBytes.contentToString())
        Assert.assertEquals(salt.contentToString(), item.salt.contentToString())
    }


    @Test
    fun `equals should return true for same content`() {
        val key1 = byteArrayOf(1, 2, 3)
        val salt1 = byteArrayOf(4, 5, 6)

        val item1 = AuthRepositoryKekItem(key1, salt1)
        val item2 = AuthRepositoryKekItem(byteArrayOf(1, 2, 3), byteArrayOf(4, 5, 6))

        Assert.assertTrue(item1 == item2)
        Assert.assertEquals(item1.hashCode(), item2.hashCode())
    }


    @Test
    fun `equals should return false when keyBytes differ`() {
        val item1 = AuthRepositoryKekItem(byteArrayOf(1, 2, 3), byteArrayOf(4, 5, 6))
        val item2 = AuthRepositoryKekItem(byteArrayOf(9, 9, 9), byteArrayOf(4, 5, 6))

        Assert.assertFalse(item1 == item2)
    }


    @Test
    fun `equals should return false when salt differs`() {
        val item1 = AuthRepositoryKekItem(byteArrayOf(1, 2, 3), byteArrayOf(4, 5, 6))
        val item2 = AuthRepositoryKekItem(byteArrayOf(1, 2, 3), byteArrayOf(9, 9, 9))

        Assert.assertFalse(item1 == item2)
    }


    @Test
    fun `equals should return false for different type`() {
        val item = AuthRepositoryKekItem(byteArrayOf(1), byteArrayOf(2))
        Assert.assertFalse(item.equals("not an item"))
    }


    @Test
    fun `hashCode should be consistent`() {
        val key = byteArrayOf(1, 2, 3)
        val salt = byteArrayOf(4, 5, 6)
        val item = AuthRepositoryKekItem(key, salt)

        val hash1 = item.hashCode()
        val hash2 = item.hashCode()

        Assert.assertEquals(hash1, hash2)
    }

}
