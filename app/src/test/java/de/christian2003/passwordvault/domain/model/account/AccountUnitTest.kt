package de.christian2003.passwordvault.domain.model.account

import de.christian2003.passwordvault.domain.model.tag.Tag
import org.junit.Assert
import org.junit.Test
import java.time.LocalDateTime
import kotlin.uuid.Uuid


class AccountUnitTest {

    @Test
    fun createValidEntry() {
        Account(
            descriptor = AccountDescriptor(
                name = "GitHub Account",
                description = "My personal account"
            )
        )
    }


    @Test
    fun createEntryWithImmutableListOfTags() {
        val tags: List<Tag> = listOf(
            Tag("Private"),
            Tag("Finance")
        )
        val account = Account(
            descriptor = AccountDescriptor(
                name = "Bank account"
            ),
            tags = tags
        )
        val entryTags: MutableList<Tag> = account.tags.toMutableList()
        entryTags.add(Tag("New tag"))

        Assert.assertNotEquals(account.tags, entryTags)

        account.tags = entryTags

        Assert.assertEquals(account.tags, entryTags)
    }


    @Test
    fun testUpdateOfMetadata() {
        val account = Account(
            descriptor = AccountDescriptor(
                name = "GitHub account"
            ),
            metadata = AccountMetadata(
                createdAt = LocalDateTime.of(2025, 10, 1, 12, 45),
                editedAt = LocalDateTime.of(2025, 10, 1, 12, 45),
                accessedAt = LocalDateTime.of(2025, 10, 1, 12, 45)
            )
        )

        val metadata1 = account.metadata

        Assert.assertEquals(metadata1.createdAt, account.metadata.createdAt)
        Assert.assertEquals(metadata1.editedAt, account.metadata.editedAt)
        Assert.assertEquals(metadata1.accessedAt, account.metadata.accessedAt)

        Thread.sleep(1)
        account.descriptor = account.descriptor.copy(name = "Bank account")
        val metadata2 = account.metadata

        Assert.assertEquals(metadata2.createdAt, metadata1.createdAt)
        Assert.assertNotEquals(metadata2.editedAt, metadata1.editedAt)
        Assert.assertEquals(metadata2.accessedAt, metadata1.accessedAt)

        Thread.sleep(1)
        account.descriptor = account.descriptor.copy(description = "My personal account")
        val metadata3 = account.metadata

        Assert.assertEquals(metadata3.createdAt, metadata2.createdAt)
        Assert.assertNotEquals(metadata3.editedAt, metadata2.editedAt)
        Assert.assertEquals(metadata3.accessedAt, metadata2.accessedAt)

        Thread.sleep(1)
        account.tags = listOf(Tag("MyTag"))
        val metadata4 = account.metadata

        Assert.assertEquals(metadata4.createdAt, metadata3.createdAt)
        Assert.assertNotEquals(metadata4.editedAt, metadata3.editedAt)
        Assert.assertEquals(metadata4.accessedAt, metadata3.accessedAt)
    }


    @Test
    fun testEqualsAndHashCode() {
        val id = Uuid.random()
        val account1 = Account(
            descriptor = AccountDescriptor(
                id = id,
                name = "Account 1"
            )
        )
        val account2 = Account(
            descriptor = AccountDescriptor(
                id = id,
                name = "Account 2"
            )
        )
        val account3 = Account(
            descriptor = AccountDescriptor(
                id = Uuid.random(),
                name = "Account 3"
            )
        )

        Assert.assertEquals(account1, account2)
        Assert.assertNotEquals(account1, account3)

        Assert.assertEquals(account1.hashCode(), account2.hashCode())
        Assert.assertNotEquals(account1.hashCode(), account3.hashCode())
    }

}
