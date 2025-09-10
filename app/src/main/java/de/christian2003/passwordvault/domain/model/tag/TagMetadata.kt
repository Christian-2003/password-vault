package de.christian2003.passwordvault.domain.model.tag

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 * Domain value object models the metadata for a tag.
 *
 * @param createdAt     Date time at which the tag was created.
 * @param editedAt      Date time at which the tag was edited.
 */
class TagMetadata(
    createdAt: LocalDateTime = LocalDateTime.now(),
    editedAt: LocalDateTime = LocalDateTime.now()
) {

    /**
     * Date time at which the tag was created.
     */
    var createdAt: LocalDateTime = createdAt
        private set(value) {
            require(!value.isAfter(LocalDateTime.now())) { "Created date time cannot be after current timestamp" }
            field = value
        }

    /**
     * Date time at which the tag was edited.
     */
    var editedAt: LocalDateTime = editedAt
        private set(value) {
            require(!value.isAfter(LocalDateTime.now())) { "Edited date time cannot be after current timestamp" }
            require(!value.isBefore(createdAt)) { "Edited date time must be at least the created timestamp" }
            field = value
        }


    /**
     * Initializes a new metadata instance.
     */
    init {
        this.createdAt = createdAt
        this.editedAt = editedAt
    }


    /**
     * Helper function copies the metadata instance. This returns a new value object with the
     * adjusted values.
     *
     * @param createdAt     Date time at which the tag was created.
     * @param editedAt      Date time at which the tag was edited.
     */
    fun copy(
        createdAt: LocalDateTime = this.createdAt,
        editedAt: LocalDateTime = this.editedAt
    ): TagMetadata {
        return TagMetadata(
            createdAt = createdAt,
            editedAt = editedAt
        )
    }


    /**
     * Hash code for the metadata instance.
     *
     * @return  Hash code.
     */
    override fun hashCode(): Int {
        var hash: Int = createdAt.hashCode()
        hash = 31 * hash + editedAt.hashCode()
        return hash
    }


    /**
     * Tests whether the object passed is identical to this metadata instance.
     *
     * @param other Other object to test.
     * @return      Whether the other object is identical to this metadata instance.
     */
    override fun equals(other: Any?): Boolean {
        return other is TagMetadata
                && other.createdAt == this.createdAt
                && other.editedAt == this.editedAt
    }


    /**
     * Converts this metadata instance into a string format. This string format can be used for
     * debugging and logging.
     *
     * @return  String representation of this metadata instance.
     */
    override fun toString(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return "[CreatedAt: ${createdAt.format(formatter)}] [EditedAt: ${editedAt.format(formatter)}]"
    }

}
