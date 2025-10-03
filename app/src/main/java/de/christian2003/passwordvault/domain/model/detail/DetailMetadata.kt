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
data class DetailMetadata(
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val editedAt: LocalDateTime = LocalDateTime.now(),
    val isObfuscated: Boolean = false,
    val isVisible: Boolean = true
) {

    /**
     * Initializes a new metadata instance.
     */
    init {
        require(!createdAt.isAfter(LocalDateTime.now())) { "Created date time cannot be after current timestamp" }
        require(!editedAt.isAfter(LocalDateTime.now())) { "Edited date time cannot be after current timestamp" }
        require(!editedAt.isBefore(createdAt)) { "Edited date time must be at least the created timestamp" }
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
