package de.christian2003.passwordvault.plugin.infrastructure.db.dto

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.EntryEntity
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.EntryTagCrossRef
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.TagEntity

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
    val tags: List<TagEntity>

)