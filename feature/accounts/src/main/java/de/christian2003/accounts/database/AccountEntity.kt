package de.christian2003.accounts.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.UUID


@Entity(tableName = "accounts")
class AccountEntity (

    /**
     * UUID of the account.
     */
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),

    /**
     * Name of the account.
     */
    var name: String,

    /**
     * Description of the account.
     */
    var description: String,

    /**
     * Date at which the account was created.
     */
    val created: LocalDateTime = LocalDateTime.now(),

    /**
     * Date at which the account was edited the last time.
     */
    var changed: LocalDateTime = LocalDateTime.now()

)
