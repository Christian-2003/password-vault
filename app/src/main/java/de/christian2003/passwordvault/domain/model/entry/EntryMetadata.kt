package de.christian2003.passwordvault.domain.model.entry

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 * Domain value object models the metadata for an entry.
 *
 * @param createdAt     Local date time at which the entry was created.
 * @param editedAt      Local date time at which the entry was edited.
 * @param accessedAt    Local date time at which the entry was accessed.
 */
class EntryMetadata(
    createdAt: LocalDateTime = LocalDateTime.now(),
    editedAt: LocalDateTime = LocalDateTime.now(),
    accessedAt: LocalDateTime = LocalDateTime.now()
) {

    /**
     * Date time at which the entry was created.
     */
    var createdAt: LocalDateTime = createdAt
        private set(value) {
            require(!value.isAfter(LocalDateTime.now())) { "Created date time cannot be after current timestamp" }
            field = value
        }

    /**
     * Date time at which the entry was edited.
     */
    var editedAt: LocalDateTime = editedAt
        private set(value) {
            require(!value.isAfter(LocalDateTime.now())) { "Edited date time cannot be after current timestamp" }
            require(!value.isBefore(createdAt)) { "Edited date time must be at least the created timestamp" }
            field = value
        }

    /**
     * Date time at which the entry was accessed.
     */
    var accessedAt: LocalDateTime = accessedAt
        private set(value) {
            require(!value.isAfter(LocalDateTime.now())) { "Accessed date time cannot be after current timestamp" }
            require(!value.isBefore(createdAt)) { "Accessed date time must be at least the created timestamp" }
        }


    /**
 * Initializes the metadata instance.
     */
    init {
        this.createdAt = createdAt
        this.editedAt = editedAt
        this.accessedAt = accessedAt
    }


    /**
     * Helper function copies the metadata instance. This returns a new value object with the
     * adjusted values.
     *
     * @param createdAt     Date time at which the entry was created.
     * @param editedAt      Date time at which the entry was edited.
     * @param accessedAt    Date time at which the entry was accessed.
     */
    fun copy(
        createdAt: LocalDateTime = this.createdAt,
        editedAt: LocalDateTime = this.editedAt,
        accessedAt: LocalDateTime = this.accessedAt
    ): EntryMetadata {
        return EntryMetadata(
            createdAt = createdAt,
            editedAt = editedAt,
            accessedAt = accessedAt
        )
    }


    /**
     * Hash code for the metadata instance.
     *
     * @return  Hash code.
     */
    override fun hashCode(): Int {
        var hash = createdAt.hashCode()
        hash = 31 * hash + editedAt.hashCode()
        hash = 31 * hash + accessedAt.hashCode()
        return hash
    }


    /**
     * Tests whether the object passed is identical to this metadata instance.
     *
     * @param other Other object to test.
     * @return      Whether the other object is identical to this metadata instance.
     */
    override fun equals(other: Any?): Boolean {
        return other is EntryMetadata
                && other.createdAt == this.createdAt
                && other.editedAt == this.editedAt
                && other.accessedAt == this.accessedAt
    }


    /**
     * Converts this metadata instance into a string format. This string format can be used for
     * debugging and logging.
     *
     * @return  String representation of this metadata instance.
     */
    override fun toString(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return "[CreatedAt: ${createdAt.format(formatter)}] [EditedAt: ${editedAt.format(formatter)}] [AccessedAt: ${accessedAt.format(formatter)}]"
    }

}
