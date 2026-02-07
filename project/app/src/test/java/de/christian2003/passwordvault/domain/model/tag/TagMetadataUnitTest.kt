package de.christian2003.passwordvault.domain.model.tag

import org.junit.Assert
import org.junit.Test
import java.time.LocalDateTime


class TagMetadataUnitTest {

    @Test
    fun createValidMetadata() {
        TagMetadata(
            createdAt = LocalDateTime.of(2025, 10, 2, 12, 47),
            editedAt = LocalDateTime.of(2025, 10, 3, 12, 47)
        )
    }


    @Test
    fun createMetadataWithIdenticalDates() {
        TagMetadata(
            createdAt = LocalDateTime.of(2025, 10, 3, 12, 47),
            editedAt = LocalDateTime.of(2025, 10, 3, 12, 47)
        )
    }


    @Test
    fun createMetadataWithEditedBeforeCreated() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            TagMetadata(
                createdAt = LocalDateTime.of(2025, 10, 3, 12, 47),
                editedAt = LocalDateTime.of(2025, 10, 2, 12, 47)
            )
        }
    }


    @Test
    fun createMetadataWithCreatedAfterCurrentDate() {
        val createdAt = LocalDateTime.now().plusYears(1)
        Assert.assertThrows(IllegalArgumentException::class.java) {
            TagMetadata(
                createdAt = createdAt,
                editedAt = createdAt
            )
        }
    }


    @Test
    fun createMetadataWithEditedAfterCurrentDate() {
        val editedAt = LocalDateTime.now().plusYears(1)
        Assert.assertThrows(IllegalArgumentException::class.java) {
            TagMetadata(
                createdAt = LocalDateTime.now(),
                editedAt = editedAt
            )
        }
    }

}
