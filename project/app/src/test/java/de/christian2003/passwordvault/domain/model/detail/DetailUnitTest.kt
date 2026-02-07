package de.christian2003.passwordvault.domain.model.detail

import org.junit.Assert
import org.junit.Test
import kotlin.uuid.Uuid


class DetailUnitTest {

    @Test
    fun createValidDetail() {
        Detail(
            name = "Email",
            content = "hello@world.de"
        )
    }


    @Test
    fun createDetailWithBlankName() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            Detail(
                name = " ",
                content = "hello@world.de"
            )
        }
    }


    @Test
    fun createDetailWithNoName() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            Detail(
                name = "",
                content = "hello@world.de"
            )
        }
    }


    @Test
    fun createDetailWithBlankContent() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            Detail(
                name = "Email",
                content = " "
            )
        }
    }


    @Test
    fun createDetailWithNoContent() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            Detail(
                name = "Email",
                content = ""
            )
        }
    }


    @Test
    fun testUpdateOfMetadata() {
        val detail = Detail(
            name = "Email",
            content = "hello@world.de"
        )
        val metadata1 = detail.metadata

        Thread.sleep(1)
        detail.name = "Password"
        val metadata2 = detail.metadata

        Assert.assertEquals(metadata2.createdAt, metadata1.createdAt)
        Assert.assertNotEquals(metadata2.editedAt, metadata1.editedAt)

        Thread.sleep(1)
        detail.content = "abc123"
        val metadata3 = detail.metadata

        Assert.assertEquals(metadata3.createdAt, metadata2.createdAt)
        Assert.assertNotEquals(metadata3.editedAt, metadata2.editedAt)
    }


    @Test
    fun testEqualsAndHashCode() {
        val id = Uuid.random()
        val detail1 = Detail(
            id = id,
            name = "Detail 1",
            content = "Content 1"
        )
        val detail2 = Detail(
            id = id,
            name = "Detail 2",
            content = "Content 2"
        )
        val detail3 = Detail(
            id = Uuid.random(),
            name = "Detail 3",
            content = "Content 3"
        )

        Assert.assertEquals(detail1, detail2)
        Assert.assertNotEquals(detail1, detail3)

        Assert.assertEquals(detail1.hashCode(), detail2.hashCode())
        Assert.assertNotEquals(detail1.hashCode(), detail3.hashCode())
    }

}
