package de.christian2003.passwordvault.domain.model.tag

import java.time.LocalDateTime
import kotlin.uuid.Uuid


/**
 * Models a tag that can be assigned to an entry for easier grouping.
 *
 * @parm id         UUID which identifies the tag.
 * @param name      Name for the tag which is displayed to the user.
 * @param metadata  Metadata for the tag.
 */
class Tag(
    id: Uuid = Uuid.Companion.random(),
    name: String,
    metadata: TagMetadata = TagMetadata()
) {

    /**
     * Type 4 UUID that identifies the tag.
     */
    var id: Uuid = id
        private set(value) {
            require(value != Uuid.NIL) { "Tag ID cannot be Nil-UUID" }
            field = value
        }

    /**
     * Name for the tag instance. This name is displayed to the user.
     */
    var name: String = name
        set(value) {
            require(value.isNotBlank()) { "Tag name cannot be blank" }
            field = value
            metadata = metadata.copy(editedAt = LocalDateTime.now())
        }

    /**
     * Metadata for the tag instance.
     */
    var metadata: TagMetadata = metadata
        private set


    /**
     * Initializes the tag instance.
     */
    init {
        this.id = id
        this.name = name
        this.metadata = metadata
    }


    /**
     * Returns the hash code for the tag. This is identical to the hash code of the ID of the tag.
     *
     * @return  Hash code for the tag.
     */
    override fun hashCode(): Int {
        return id.hashCode()
    }


    /**
     * Tests whether the object passed is equal to this tag, based on their IDs.
     *
     * @param other Other object to test.
     * @return      Whether the IDs of both objects are identical.
     */
    override fun equals(other: Any?): Boolean {
        return (other is Tag) && (other.id == this.id)
    }


    /**
     * Converts this tag instance into a string format. This string format can be used for
     * debugging and logging.
     *
     * @return  String representation of this tag instance.
     */
    override fun toString(): String {
        return "[$id] [$name]"
    }

}
