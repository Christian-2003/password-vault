package de.christian2003.passwordvault.plugin.infrastructure.db.dto

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.AccountEntity
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.AccountTagCrossRef
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.TagEntity


data class AccountWithTags(

    @Embedded
    val account: AccountEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = AccountTagCrossRef::class,
            parentColumn = "account",
            entityColumn = "tag"
        )
    )
    val tags: List<TagEntity>

)
