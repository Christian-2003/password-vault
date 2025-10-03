package de.christian2003.passwordvault.domain.model.entry

import de.christian2003.passwordvault.domain.model.tag.Tag
import org.junit.Assert
import org.junit.Test
import java.time.LocalDateTime
import kotlin.uuid.Uuid


class EntryUnitTest {

    @Test
    fun createValidEntry() {
        Entry(
            name = "GitHub Account",
            description = "My personal account"
        )
    }


    @Test
    fun createEntryWithBlankDescription() {
        val entry = Entry(
            name = "GitHub Account",
            description = " "
        )

        Assert.assertEquals("", entry.description)
    }


    @Test
    fun createEntryWithNoDescription() {
        Entry(
            name = "GitHub Account",
            description = ""
        )
    }


    @Test
    fun createEntryWithBlankName() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            Entry(
                name = " "
            )
        }
    }


    @Test
    fun createEntryWithNoName() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            Entry(
                name = ""
            )
        }
    }


    @Test
    fun createEntryWithImmutableListOfTags() {
        val tags: List<Tag> = listOf(
            Tag("Private"),
            Tag("Finance")
        )
        val entry = Entry(
            name = "Bank account",
            description = "",
            tags = tags
        )
        val entryTags: MutableList<Tag> = entry.tags.toMutableList()
        entryTags.add(Tag("New tag"))

        Assert.assertNotEquals(entry.tags, entryTags)

        entry.tags = entryTags

        Assert.assertEquals(entry.tags, entryTags)
    }


    @Test
    fun testUpdateOfMetadata() {
        val entry = Entry(
            name = "GitHub account",
            metadata = EntryMetadata(
                createdAt = LocalDateTime.of(2025, 10, 1, 12, 45),
                editedAt = LocalDateTime.of(2025, 10, 1, 12, 45),
                accessedAt = LocalDateTime.of(2025, 10, 1, 12, 45)
            )
        )

        val metadata1 = entry.metadata

        Assert.assertEquals(metadata1.createdAt, entry.metadata.createdAt)
        Assert.assertEquals(metadata1.editedAt, entry.metadata.editedAt)
        Assert.assertEquals(metadata1.accessedAt, entry.metadata.accessedAt)

        Thread.sleep(1)
        entry.name = "Bank account"
        val metadata2 = entry.metadata

        Assert.assertEquals(metadata2.createdAt, metadata1.createdAt)
        Assert.assertNotEquals(metadata2.editedAt, metadata1.editedAt)
        Assert.assertEquals(metadata2.accessedAt, metadata1.accessedAt)

        Thread.sleep(1)
        entry.description = "My account"
        val metadata3 = entry.metadata

        Assert.assertEquals(metadata3.createdAt, metadata2.createdAt)
        Assert.assertNotEquals(metadata3.editedAt, metadata2.editedAt)
        Assert.assertEquals(metadata3.accessedAt, metadata2.accessedAt)

        Thread.sleep(1)
        entry.tags = listOf()
        val metadata4 = entry.metadata

        Assert.assertEquals(metadata4.createdAt, metadata3.createdAt)
        Assert.assertNotEquals(metadata4.editedAt, metadata3.editedAt)
        Assert.assertEquals(metadata4.accessedAt, metadata3.accessedAt)
    }


    @Test
    fun testEqualsAndHashCode() {
        val id = Uuid.random()
        val entry1 = Entry(
            id = id,
            name = "Entry 1"
        )
        val entry2 = Entry(
            id = id,
            name = "Entry 2"
        )
        val entry3 = Entry(
            id = Uuid.random(),
            name = "Entry 3"
        )

        Assert.assertEquals(entry1, entry2)
        Assert.assertNotEquals(entry1, entry3)

        Assert.assertEquals(entry1.hashCode(), entry2.hashCode())
        Assert.assertNotEquals(entry1.hashCode(), entry3.hashCode())
    }

}
