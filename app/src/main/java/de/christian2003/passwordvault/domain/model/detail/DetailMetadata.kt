package de.christian2003.passwordvault.domain.model.detail

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 * Domain value object models the metadata for a detail.
 *
 * @param createdAt     Date time at which the detail was created.
 * @param editedAt      Date time at which the detail was edited.
 * @param isObfuscated  Whether the detail content is obfuscated when displayed to the user.
 * @param isVisible     Whether the detail is visible.
 */
class DetailMetadata(
    createdAt: LocalDateTime = LocalDateTime.now(),
    editedAt: LocalDateTime = LocalDateTime.now(),
    isObfuscated: Boolean = false,
    isVisible: Boolean = true
) {

    /**
     * Date time at which the detail was created.
     */
    var createdAt: LocalDateTime = createdAt
        private set(value) {
            require(!value.isAfter(LocalDateTime.now())) { "Created date time cannot be after current timestamp" }
            field = value
        }

    /**
     * Date time at which the detail was edited.
     */
    var editedAt: LocalDateTime = editedAt
        private set(value) {
            require(!value.isAfter(LocalDateTime.now())) { "Edited date time cannot be after current timestamp" }
            require(!value.isBefore(createdAt)) { "Edited date time must be at least the created timestamp" }
            field = value
        }

    /**
     * Indicates whether the detail content is obfuscated when displayed to the user. Obfuscated
     * content is displayed with "*****" instead of the plain text.
     */
    var isObfuscated: Boolean = isObfuscated
        private set

    /**
     * Indicates whether the detail is visible. If this is set to false, the detail must be
     * explicitly shown by the user by clicking a "More" button. This allows less relevant details
     * to be hidden.
     */
    var isVisible: Boolean = isVisible
        private set


    /**
     * Initializes a new metadata instance.
     */
    init {
        this.createdAt = createdAt
        this.editedAt = editedAt
        this.isObfuscated = isObfuscated
        this.isVisible = isVisible
    }


    /**
     * Helper function copies the metadata instance. This returns a new value object with the
     * adjusted values.
     *
     * @param createdAt     Date time at which the detail was created.
     * @param editedAt      Date time at which the detail was edited.
     * @param isObfuscated  Whether the detail content is obfuscated when displayed.
     * @param isVisible     Whether the detail is visible or hidden.
     */
    fun copy(
        createdAt: LocalDateTime = this.createdAt,
        editedAt: LocalDateTime = this.editedAt,
        isObfuscated: Boolean = this.isObfuscated,
        isVisible: Boolean = this.isVisible
    ): DetailMetadata {
        return DetailMetadata(
            createdAt = createdAt,
            editedAt = editedAt,
            isObfuscated = isObfuscated,
            isVisible = isVisible
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
        hash = 31 * hash + isObfuscated.hashCode()
        hash = 31 * hash + isVisible.hashCode()
        return hash
    }


    /**
     * Tests whether the object passed is identical to this metadata instance.
     *
     * @param other Other object to test.
     * @return      Whether the other object is identical to this metadata instance.
     */
    override fun equals(other: Any?): Boolean {
        return other is DetailMetadata
                && other.createdAt == this.createdAt
                && other.editedAt == this.editedAt
                && other.isObfuscated == this.isObfuscated
                && other.isVisible == this.isVisible
    }


    /**
     * Converts this metadata instance into a string format. This string format can be used for
     * debugging and logging.
     *
     * @return  String representation of this metadata instance.
     */
    override fun toString(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return "[CreatedAt: ${createdAt.format(formatter)}] [EditedAt: ${editedAt.format(formatter)}] [IsObfuscated: $isObfuscated] [IsVisible: $isVisible]"
    }

}
