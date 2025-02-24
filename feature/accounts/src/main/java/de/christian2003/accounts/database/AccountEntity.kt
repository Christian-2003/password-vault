package de.christian2003.accounts.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID


@Entity(tableName = "accounts")
data class AccountEntity (

    /**
     * UUID of the account.
     */
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),

    /**
     * Encrypted content of the account.
     */
    val content: ByteArray = ByteArray(0)

) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AccountEntity

        if (id != other.id) return false
        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + content.contentHashCode()
        return result
    }

}
