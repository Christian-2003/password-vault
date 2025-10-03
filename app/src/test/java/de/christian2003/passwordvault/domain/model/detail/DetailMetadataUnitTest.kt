package de.christian2003.passwordvault.domain.model.detail

import org.junit.Assert
import org.junit.Test
import java.time.LocalDateTime


class DetailMetadataUnitTest {

    @Test
    fun createValidMetadata() {
        DetailMetadata(
            createdAt = LocalDateTime.of(2025, 10, 2, 12, 47),
            editedAt = LocalDateTime.of(2025, 10, 3, 12, 47),
            isObfuscated = true,
            isVisible = false
        )
    }


    @Test
    fun createMetadataWithIdenticalDates() {
        DetailMetadata(
            createdAt = LocalDateTime.of(2025, 10, 3, 12, 47),
            editedAt = LocalDateTime.of(2025, 10, 3, 12, 47)
        )
    }


    @Test
    fun createMetadataWithEditedBeforeCreated() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            DetailMetadata(
                createdAt = LocalDateTime.of(2025, 10, 3, 12, 47),
                editedAt = LocalDateTime.of(2025, 10, 2, 12, 47)
            )
        }
    }


    @Test
    fun createMetadataWithCreatedAfterCurrentDate() {
        val createdAt = LocalDateTime.now().plusYears(1)
        Assert.assertThrows(IllegalArgumentException::class.java) {
            DetailMetadata(
                createdAt = createdAt,
                editedAt = createdAt
            )
        }
    }


    @Test
    fun createMetadataWithEditedAfterCurrentDate() {
        val editedAt = LocalDateTime.now().plusYears(1)
        Assert.assertThrows(IllegalArgumentException::class.java) {
            DetailMetadata(
                createdAt = LocalDateTime.of(2025, 10, 3, 12, 47),
                editedAt = editedAt
            )
        }
    }

}
