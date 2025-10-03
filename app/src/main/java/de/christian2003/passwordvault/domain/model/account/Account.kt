package de.christian2003.passwordvault.domain.model.account

import de.christian2003.passwordvault.domain.model.tag.Tag
import de.christian2003.passwordvault.domain.model.target.Target
import java.time.LocalDateTime


/**
 * Domain entity models an entry (i.e. an account).
 *
 * @param descriptor    Descriptor contains base information about the account, such as the name,
 *                      description and ID.
 * @param tags          List of tags associated with the entry.
 * @param targets       List of targets for the entry.
 * @param metadata      Metadata for the entry.
 */
class Account (
    descriptor: AccountDescriptor,
    tags: List<Tag> = listOf(),
    targets: List<Target> = listOf(),
    metadata: AccountMetadata = AccountMetadata()
) {

    /**
     * Descriptor contains base information about the account, such as the name, description and ID.
     */
    var descriptor: AccountDescriptor = descriptor
        set(value) {
            if (field != value) {
                field = value
                metadata = metadata.copy(editedAt = LocalDateTime.now())
            }
        }

    /**
     * List of tags that are associated with the entry.
     */
    var tags: List<Tag> = tags
        get() {
            return field.toList()
        }
        set(value) {
            if (field != value) {
                field = value.toList()
                metadata = metadata.copy(editedAt = LocalDateTime.now())
            }
        }

    /**
     * List of targets for the entry.
     */
    var targets: List<Target> = targets
        get() {
            return field.toList()
        }
        set(value) {
            if (field != value) {
                field = value.toList()
                metadata = metadata.copy(editedAt = LocalDateTime.now())
            }
        }

    /**
     * Metadata for the entry.
     */
    var metadata: AccountMetadata = metadata
        private set


    /**
     * Initializes the entry instance.
     */
    init {
        this.descriptor = descriptor
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
        return descriptor.id.hashCode()
    }


    /**
     * Tests whether the object passed is equal to this entry, based on their IDs.
     *
     * @param other Other object to test.
     * @return      Whether the IDs of both objects are identical.
     */
    override fun equals(other: Any?): Boolean {
        return (other is Account) && (other.descriptor.id == this.descriptor.id)
    }


    /**
     * Converts this entry instance into a string format. This string format can be used for
     * debugging and logging.
     *
     * @return  String representation of this entry instance.
     */
    override fun toString(): String {
        return "[Descriptor: $descriptor] [Metadata: $metadata]"
    }

}
