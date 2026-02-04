package de.christian2003.data.accounts.domain.entities

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 * Domain value object models the metadata for an account.
 *
 * @param createdAt     Local date time at which the account was created.
 * @param editedAt      Local date time at which the account was edited.
 * @param accessedAt    Local date time at which the account was accessed.
 */
data class AccountMetadata(
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val editedAt: LocalDateTime = LocalDateTime.now(),
    val accessedAt: LocalDateTime = LocalDateTime.now()
) {

    /**
     * Initializes the metadata instance.
     */
    init {
        require(!createdAt.isAfter(LocalDateTime.now())) { "Created date time cannot be after current timestamp" }
        require(!editedAt.isAfter(LocalDateTime.now())) { "Edited date time cannot be after current timestamp" }
        require(!editedAt.isBefore(createdAt)) { "Edited date time must be at least the created timestamp" }
        require(!accessedAt.isAfter(LocalDateTime.now())) { "Accessed date time cannot be after current timestamp" }
        require(!accessedAt.isBefore(createdAt)) { "Accessed date time must be at least the created timestamp" }
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
