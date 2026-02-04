package de.christian2003.data.accounts.infrastructure.db.dto

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import de.christian2003.data.accounts.infrastructure.db.entities.AccountEntity
import de.christian2003.data.accounts.infrastructure.db.entities.TagEntity


internal data class AccountWithTags(

    @Embedded
    val account: AccountEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = AccountWithTags::class,
            parentColumn = "account",
            entityColumn = "tag"
        )
    )
    val tags: List<TagEntity>

)
