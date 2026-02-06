package de.christian2003.feature.accounts.models.dto

import de.christian2003.data.accounts.domain.entities.TagMetadata
import kotlin.uuid.Uuid


/**
 * Models a DTO for tags that is used in the presentation layer.
 *
 * The DTO is required for the following reason:
 *      In the domain layer, the equals- and hashCode-methods only regard the tag ID, because this
 *      adheres to the domain and business logic. However, the LazyColumn of Jetpack Compose
 *      determines whether an item needs to be recomposed based on those equals- and hashCode-methods.
 *      Therefore, if the name changes in these domain layer objects, the LazyColumn would not update
 *      because the IDs do not change. For this reason, we need to map tags to this DTO, which uses
 *      a different implementation for equals- and hashCode.
 *
 * @param name      Name for the tag.
 * @param id        ID for the tag.
 * @param metadata  Metadata for the tag.
 */
data class TagUiDto(
    val name: String,
    val id: Uuid,
    val metadata: TagMetadata
)
