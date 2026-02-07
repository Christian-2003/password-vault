package de.christian2003.passwordvault.domain.model.tag

import org.junit.Assert
import org.junit.Test
import kotlin.uuid.Uuid


class TagUnitTest {

    @Test
    fun createValidTag() {
        Tag(
            name = "My tag"
        )
    }


    @Test
    fun createTagWithBlankName() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            Tag(
                name = " "
            )
        }
    }


    @Test
    fun createTagWithNoName() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            Tag(
                name = ""
            )
        }
    }


    @Test
    fun testUpdateOfMetadata() {
        val tag = Tag(
            name = "Tag 1"
        )
        val metadata1 = tag.metadata

        Thread.sleep(1)
        tag.name = "New name"
        val metadata2 = tag.metadata

        Assert.assertEquals(metadata1.createdAt, metadata2.createdAt)
        Assert.assertNotEquals(metadata1.editedAt, metadata2.editedAt)
    }


    @Test
    fun testEqualsAndHashCode() {
        val id = Uuid.random()
        val tag1 = Tag(
            id = id,
            name = "Tag 1"
        )
        val tag2 = Tag(
            id = id,
            name = "Tag 2"
        )
        val tag3 = Tag(
            id = Uuid.random(),
            name = "Tag 3"
        )

        Assert.assertEquals(tag1, tag2)
        Assert.assertNotEquals(tag1, tag3)

        Assert.assertEquals(tag1.hashCode(), tag2.hashCode())
        Assert.assertNotEquals(tag1.hashCode(), tag3.hashCode())
    }

}
