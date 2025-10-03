package de.christian2003.passwordvault.domain.model.account

import org.junit.Assert
import org.junit.Test
import java.time.LocalDateTime


class AccountMetadataUnitTest {

    @Test
    fun createValidEntryMetadata() {
        AccountMetadata(
            createdAt = LocalDateTime.of(2025, 10, 1, 12, 47),
            editedAt = LocalDateTime.of(2025, 10, 2, 12, 47),
            accessedAt = LocalDateTime.of(2025, 10, 3, 12, 47)
        )
    }


    @Test
    fun createEntryMetadataWithIdenticalDates() {
        AccountMetadata(
            createdAt = LocalDateTime.of(2025, 10, 3, 12, 47),
            editedAt = LocalDateTime.of(2025, 10, 3, 12, 47),
            accessedAt = LocalDateTime.of(2025, 10, 3, 12, 47)
        )
    }


    @Test
    fun createEntryMetadataWithEditedBeforeCreated() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            AccountMetadata(
                createdAt = LocalDateTime.of(2025, 10, 2, 12, 47),
                editedAt = LocalDateTime.of(2025, 10, 1, 12, 47),
                accessedAt = LocalDateTime.of(2025, 10, 3, 12, 47)
            )
        }
    }


    @Test
    fun createEntryMetadataWithAccessedBeforeCreated() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            AccountMetadata(
                createdAt = LocalDateTime.of(2025, 10, 2, 12, 47),
                editedAt = LocalDateTime.of(2025, 10, 3, 12, 47),
                accessedAt = LocalDateTime.of(2025, 10, 1, 12, 47)
            )
        }
    }


    @Test
    fun createEntryMetadataWithCreatedAfterCurrentDate() {
        val createdAt = LocalDateTime.now().plusYears(1)
        Assert.assertThrows(IllegalArgumentException::class.java) {
            AccountMetadata(
                createdAt = createdAt,
                editedAt = createdAt,
                accessedAt = createdAt
            )
        }
    }


    @Test
    fun createEntryMetadataWithEditedAfterCurrentDate() {
        val editedAt = LocalDateTime.now().plusYears(1)
        Assert.assertThrows(IllegalArgumentException::class.java) {
            AccountMetadata(
                createdAt = LocalDateTime.of(2025, 10, 3, 12, 47),
                editedAt = editedAt,
                accessedAt = editedAt
            )
        }
    }


    @Test
    fun createEntryMetadataWithAccessedAfterCurrentDate() {
        val accessedAt = LocalDateTime.now().plusYears(1)
        Assert.assertThrows(IllegalArgumentException::class.java) {
            AccountMetadata(
                createdAt = LocalDateTime.of(2025, 10, 3, 12, 47),
                editedAt = LocalDateTime.of(2025, 10, 3, 12, 47),
                accessedAt = accessedAt
            )
        }
    }

}
