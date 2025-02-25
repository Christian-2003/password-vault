package de.christian2003.accounts.database

import androidx.room.ColumnInfo
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
    @ColumnInfo(name = "id")
    val id: UUID = UUID.randomUUID(),

    /**
     * Name of the account.
     */
    @ColumnInfo(name = "name")
    var name: String,

    /**
     * Description of the account.
     */
    @ColumnInfo(name = "description")
    var description: String,

    /**
     * Date at which the account was created.
     */
    @ColumnInfo(name = "created")
    val created: LocalDateTime = LocalDateTime.now(),

    /**
     * Date at which the account was edited the last time.
     */
    @ColumnInfo(name = "changed")
    var changed: LocalDateTime = LocalDateTime.now()

)
