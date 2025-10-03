package de.christian2003.passwordvault.domain.model.entry

import de.christian2003.passwordvault.domain.model.tag.Tag
import de.christian2003.passwordvault.domain.model.target.Target
import java.time.LocalDateTime
import kotlin.uuid.Uuid


/**
 * Domain entity models an entry (i.e. an account).
 *
 * @param name          Name for the entry which is displayed to the user.
 * @param id            UUID for the entry.
 * @param description   Description for the entry which is displayed to the user.
 * @param tags          List of tags associated with the entry.
 * @param targets       List of targets for the entry.
 * @param metadata      Metadata for the entry.
 */
class Entry (
    name: String,
    val id: Uuid = Uuid.random(),
    description: String = "",
    tags: List<Tag> = listOf(),
    targets: List<Target> = listOf(),
    metadata: EntryMetadata = EntryMetadata()
) {

    /**
     * Name of the entry which is shown to the user. This cannot be blank or empty.
     */
    var name: String = name
        set(value) {
            require(value.isNotBlank()) { "Entry name cannot be blank" }
            field = value
            metadata = metadata.copy(editedAt = LocalDateTime.now())
        }

    /**
     * Description of the entry which is shown to the user.
     */
    var description: String = description
        set(value) {
            field = value.ifBlank { "" }
            metadata = metadata.copy(editedAt = LocalDateTime.now())
        }

    /**
     * List of tags that are associated with the entry.
     */
    var tags: List<Tag> = tags
        get() {
            return field.toList()
        }
        set(value) {
            field = value.toList()
        }

    /**
     * List of targets for the entry.
     */
    var targets: List<Target> = targets
        get() {
            return field.toList()
        }
        set(value) {
            field = value.toList()
        }

    /**
     * Metadata for the entry.
     */
    var metadata: EntryMetadata = metadata
        private set


    /**
     * Initializes the entry instance.
     */
    init {
        require(id != Uuid.NIL) { "Entry ID cannot be Nil-UUID" }
        this.name = name
        this.description = description
        this.tags = tags
        this.targets = targets
        this.metadata = metadata
    }


    /**
     * Returns the hash code for the entry. This is identical to the hash code of the ID of the
     * entry.
     *
     * @return  Hash code for the entry.
     */
    override fun hashCode(): Int {
        return id.hashCode()
    }


    /**
     * Tests whether the object passed is equal to this entry, based on their IDs.
     *
     * @param other Other object to test.
     * @return      Whether the IDs of both objects are identical.
     */
    override fun equals(other: Any?): Boolean {
        return (other is Entry) && (other.id == this.id)
    }


    /**
     * Converts this entry instance into a string format. This string format can be used for
     * debugging and logging.
     *
     * @return  String representation of this entry instance.
     */
    override fun toString(): String {
        return "[$id] [$name]"
    }

}
