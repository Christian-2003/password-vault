package de.christian2003.passwordvault.plugin.infrastructure.db.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import de.christian2003.passwordvault.domain.entry.Tag

data class EntryWithTags(

    @Embedded
    val entry: EntryEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = EntryTagCrossRef::class,
            parentColumn = "entry",
            entityColumn = "tag"
        )
    )
    val tags: List<Tag>

)
