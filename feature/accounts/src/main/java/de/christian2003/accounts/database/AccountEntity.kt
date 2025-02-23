package de.christian2003.accounts.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "accounts")
class AccountEntity (

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

    //TODO: Implement methods to access decrypted data

}
